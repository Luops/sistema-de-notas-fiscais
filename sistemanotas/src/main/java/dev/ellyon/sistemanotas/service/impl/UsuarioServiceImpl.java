package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioResponseDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioRequestDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioResponseDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioUpdateRequestDTO;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.EntityNotFoundException;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.Empresa;
import dev.ellyon.sistemanotas.model.EmpresaUsuario;
import dev.ellyon.sistemanotas.model.Usuario;
import dev.ellyon.sistemanotas.model.enums.Perfil;
import dev.ellyon.sistemanotas.repository.EmpresaRepository;
import dev.ellyon.sistemanotas.repository.EmpresaUsuarioRepository;
import dev.ellyon.sistemanotas.repository.NotaRepository;
import dev.ellyon.sistemanotas.repository.UsuarioRepository;
import dev.ellyon.sistemanotas.service.UsuarioService;
import dev.ellyon.sistemanotas.service.mapper.EmpresaUsuarioMapper;
import dev.ellyon.sistemanotas.service.mapper.UsuarioMapper;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final EmpresaUsuarioRepository empresaUsuarioRepository;
    private final NotaRepository notaRepository;
    private final UsuarioMapper usuarioMapper;
    private final EmpresaUsuarioMapper empresaUsuarioMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, EmpresaRepository empresaRepository, EmpresaUsuarioRepository empresaUsuarioRepository, NotaRepository notaRepository, UsuarioMapper usuarioMapper, EmpresaUsuarioMapper empresaUsuarioMapper, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.empresaUsuarioRepository = empresaUsuarioRepository;
        this.notaRepository = notaRepository;
        this.usuarioMapper = usuarioMapper;
        this.empresaUsuarioMapper = empresaUsuarioMapper;
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

    // Atualizar um usuário existente
    @Override
    public UsuarioResponseDTO update(Long id, UsuarioUpdateRequestDTO dto) {
        Map<String, String> errors = new HashMap<>();

        // 1. BUSCAR USUÁRIO EXISTENTE
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Usuário não encontrado com ID: " + id, null));

        // 2. VALIDAÇÕES

        // Verificar se usuário está ativo
        if (!usuarioExistente.getIsAtivo()) {
            errors.put("usuario", "Não é possível atualizar usuário inativo");
        }

        // Verificar unicidade do email (se alterado)
        if (!usuarioExistente.getEmail().equals(dto.getEmail()) &&
                usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            errors.put("email", "Email já cadastrado no sistema");
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do usuário", errors);
        }

        // 3. Atualizar dados do usuário, pessoais
        usuarioExistente.setNome(dto.getNome());
        usuarioExistente.setEmail(dto.getEmail());

        // Atualizar senha apenas se fornecida
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuarioExistente.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        // 4. Salvar usuário atualizado
        Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);
        return usuarioMapper.toResponseDTO(usuarioAtualizado);
    }

    // Deletar um usuário
    @Override
    public void delete(Long id) {
        Map<String, String> errors = new HashMap<>();

        // 1. Buscar usuário existente
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Usuário não encontrado com ID: " + id, null));

        // 2. Validações
        // Verificar se usuário está ativo
        if (usuarioExistente.getIsAtivo()) {
            errors.put("usuario", "Não é possível deletar um usuário ativo. Desative-o primeiro.");
            throw new ValidationException("Erro de validação", errors);
        }

        // Verificar se usuário possui notas vinculadas
        long qtdNotasVinculadas = notaRepository.countByCreatedById(id);

        if (qtdNotasVinculadas > 0) {
            errors.put("notas", String.format(
                    "Não é possível deletar o usuário. Existem %d nota(s) vinculada(s) a ele. " +
                            "Considere manter o usuário inativo em vez de deletá-lo.",
                    qtdNotasVinculadas
            ));
            throw new ValidationException("Erro de validação", errors);
        }

        // 3. Deletar associações EmpresaUsuario
        List<EmpresaUsuario> associacoes = empresaUsuarioRepository.findByUsuarioId(id);
        if (!associacoes.isEmpty()) {
            empresaUsuarioRepository.deleteAll(associacoes);
        }

        // 4. Deletar usuário
        usuarioRepository.delete(usuarioExistente);
    }

    // Desativar um usuário
    @Override
    public void softDelete(Long id) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Usuário não encontrado com ID: " + id, null));
        usuarioExistente.setIsAtivo(false);
        usuarioRepository.save(usuarioExistente);
    }

    // Ativar um usuário
    @Override
    public void activate(Long id) {
        Usuario usuarioExistente = usuarioRepository.findById(id).orElseThrow(() -> new ValidationException("Usuário não encontrado com ID: " + id, null));
        usuarioExistente.setIsAtivo(true);
        usuarioRepository.save(usuarioExistente);
    }

    // Obter um usuário por ID
    @Override
    public UsuarioResponseDTO findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Usuário não encontrado com ID: " + id, null));

        return usuarioMapper.toResponseDTO(usuario);
    }

    // Obter todos os usuários
    @Override
    public List<UsuarioResponseDTO> findAll() {
        Usuario usuario = usuarioRepository.findAll().stream().findFirst().orElseThrow(() -> new ValidationException("Nenhum usuário encontrado", null));
        return List.of(usuarioMapper.toResponseDTO(usuario));
    }

    // Obter usuários por email
    @Override
    public List<UsuarioResponseDTO> findByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmailContainingIgnoreCase(email)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ValidationException("Nenhum usuário encontrado com email: " + email, null));
        return List.of(usuarioMapper.toResponseDTO(usuario));
    }

    // Obter usuários por empresa
    @Override
    public List<EmpresaUsuarioResponseDTO> findByEmpresaId(Long empresaId) {
        // Verificar se empresa existe
        Empresa empresa = empresaRepository.findByIdAndIsAtivo(empresaId, true)
                .orElseThrow(() -> new EntityNotFoundException("Empresa", empresaId));

        // Buscar associações
        List<EmpresaUsuario> empresaUsuarios = empresaUsuarioRepository.findByEmpresaId(empresaId);

        if (empresaUsuarios.isEmpty()) {
            throw new EntityNotFoundException("Nenhum usuário encontrado para a empresa com ID: " + empresaId);
        }

        // Mapear para DTO incluindo o perfil
        return empresaUsuarios.stream().map(empresaUsuarioMapper::toResponseDTO).collect(Collectors.toList());

    }

    // Obter usuários por perfil
    @Override
    public List<EmpresaUsuarioResponseDTO> findByPerfil(String perfilStr) {
        // Converter a string do perfil para o enum Perfil
        Perfil perfil;
        try {
            perfil = Perfil.fromCodigo(perfilStr);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Perfil inválido: " + perfilStr + ". Valores permitidos: ADMIN, VENDEDOR, VISUALIZADOR");
        }

        List<EmpresaUsuario> usuarios = empresaUsuarioRepository.findByPerfil(perfil);

        if (usuarios.isEmpty()){
            throw new EntityNotFoundException("Nenhum usuário encontrado com o perfil: " + perfilStr);
        }

        return usuarios.stream()
                .map(empresaUsuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Obter usuários por nome
    @Override
    public List<UsuarioResponseDTO> findByNome(String nome) {
        List<Usuario> usuarios = usuarioRepository.findByNomeContainingIgnoreCase(nome);
        if (usuarios.isEmpty()) {
            throw new EntityNotFoundException("Nenhum usuário encontrado com o nome: " + nome);
        }
        return usuarios.stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Obter usuários por status de ativo
    @Override
    public List<UsuarioResponseDTO> findByAtivo(boolean ativo) {
        List<Usuario> usuarios = usuarioRepository.findByIsAtivo(ativo);
        if (usuarios.isEmpty()) {
            throw new EntityNotFoundException("Nenhum usuário encontrado com o status ativo: " + ativo);
        }
        return usuarios.stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
