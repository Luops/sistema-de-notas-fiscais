package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.usuario.UsuarioRequestDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioResponseDTO;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.Empresa;
import dev.ellyon.sistemanotas.model.EmpresaUsuario;
import dev.ellyon.sistemanotas.model.Usuario;
import dev.ellyon.sistemanotas.model.enums.Perfil;
import dev.ellyon.sistemanotas.repository.EmpresaRepository;
import dev.ellyon.sistemanotas.repository.EmpresaUsuarioRepository;
import dev.ellyon.sistemanotas.repository.UsuarioRepository;
import dev.ellyon.sistemanotas.service.UsuarioService;
import dev.ellyon.sistemanotas.service.mapper.UsuarioMapper;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final EmpresaUsuarioRepository empresaUsuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              EmpresaRepository empresaRepository,
                              EmpresaUsuarioRepository empresaUsuarioRepository,
                              UsuarioMapper usuarioMapper,
                              BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.empresaUsuarioRepository = empresaUsuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // Criar um novo usuário
    @Override
    public UsuarioResponseDTO create(UsuarioRequestDTO dto) {
        Map<String, String> errors = new HashMap<>();

        // ========================================
        // 1. VALIDAÇÕES
        // ========================================

        // Verificar unicidade do email
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            errors.put("email", "Email já cadastrado no sistema");
        }

        // Verificar se empresa existe e está ativa
        Empresa empresa = empresaRepository.findByIdAndIsAtivo(dto.getEmpresaId(), true)
                .orElseGet(() -> {
                    errors.put("empresaId", "Empresa não encontrada ou inativa");
                    return null;
                });

        /// Validar e converter perfil de String para Enum
        Perfil perfil = null;
        if (dto.getPerfil() == null || dto.getPerfil().isBlank()) {
            errors.put("perfil", "Perfil é obrigatório");
        } else {
            try {
                perfil = Perfil.valueOf(dto.getPerfil().toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.put("perfil", "Perfil inválido. Valores permitidos: ADMIN, VENDEDOR, VISUALIZADOR");
            }
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do usuário", errors);
        }

        // ========================================
        // 2. CRIAR USUÁRIO
        // ========================================
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha())); // Criptografar senha
        usuario.setIsAtivo(true);

        // Salvar usuário
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // ========================================
        // 3. ASSOCIAR USUÁRIO À EMPRESA
        // ========================================
        EmpresaUsuario empresaUsuario = new EmpresaUsuario(empresa, usuarioSalvo, perfil);
        empresaUsuarioRepository.save(empresaUsuario);

        // ========================================
        // 4. RETORNAR DTO (SEM SENHA)
        // ========================================
        return usuarioMapper.toResponseDTO(usuarioSalvo);
    }
}
