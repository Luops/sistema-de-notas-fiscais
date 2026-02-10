package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.auth.LoginRequestDTO;
import dev.ellyon.sistemanotas.dto.auth.LoginResponseDTO;
import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioSimpleResponseDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioResponseDTO;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.model.EmpresaUsuario;
import dev.ellyon.sistemanotas.model.Usuario;
import dev.ellyon.sistemanotas.repository.EmpresaUsuarioRepository;
import dev.ellyon.sistemanotas.repository.UsuarioRepository;
import dev.ellyon.sistemanotas.service.AuthService;
import dev.ellyon.sistemanotas.service.JwtService;
import dev.ellyon.sistemanotas.service.mapper.UsuarioMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final EmpresaUsuarioRepository empresaUsuarioRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthServiceImpl(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, EmpresaUsuarioRepository empresaUsuarioRepository, JwtService jwtService, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.empresaUsuarioRepository = empresaUsuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponseDTO autenticar(LoginRequestDTO dto) {
        // Buscar usuário
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessException("Email ou senha inválidos"));

        // Verificar se está ativo
        if (!usuario.getIsAtivo()) {
            throw new BusinessException("Usuário inativo");
        }

        // Validar senha
        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new BusinessException("Email ou senha inválidos");
        }

        // ✅ Buscar perfis do usuário nas empresas
        List<EmpresaUsuario> empresaUsuarios = empresaUsuarioRepository.findByUsuarioId(usuario.getId());
        List<String> perfis = empresaUsuarios.stream()
                .map(eu -> eu.getPerfil().name())
                .distinct()
                .collect(Collectors.toList());

        // Se não tiver perfis, adicionar VISUALIZADOR como padrão
        if (perfis.isEmpty()) {
            perfis.add("VISUALIZADOR");
        }

        // ✅ Gerar token com perfis
        String token = jwtService.gerarToken(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNome(),
                perfis
        );

        // Mapear empresas
        List<EmpresaUsuarioSimpleResponseDTO> empresasDTO = empresaUsuarios.stream()
                .map(eu -> new EmpresaUsuarioSimpleResponseDTO(
                        eu.getEmpresa().getId(),
                        eu.getEmpresa().getNomeFantasia(),
                        eu.getPerfil()
                ))
                .collect(Collectors.toList());

        // Criar LoginResponseDTO com todos os campos
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setTipo("Bearer");
        response.setUsuarioId(usuario.getId());
        response.setNome(usuario.getNome());
        response.setEmail(usuario.getEmail());
        response.setEmpresas(empresasDTO);

        return response;
    }
}