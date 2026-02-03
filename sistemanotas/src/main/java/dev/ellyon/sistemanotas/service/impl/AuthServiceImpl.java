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

        // ========================================
        // 1. BUSCAR USUÁRIO POR EMAIL
        // ========================================
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessException("Email ou senha inválidos"));

        // ========================================
        // 2. VERIFICAR SE USUÁRIO ESTÁ ATIVO
        // ========================================
        if (!usuario.getIsAtivo()) {
            throw new BusinessException("Usuário inativo. Entre em contato com o administrador.");
        }

        // ========================================
        // 3. VALIDAR SENHA
        // ========================================
        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new BusinessException("Email ou senha inválidos");
        }

        // ========================================
        // 4. GERAR TOKEN JWT
        // ========================================
        String token = jwtService.gerarToken(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNome()
        );

        // ========================================
        // 5. BUSCAR EMPRESAS DO USUÁRIO
        // ========================================
        UsuarioResponseDTO usuarioDTO = usuarioMapper.toResponseDTO(usuario);

        // ========================================
        // 6. MONTAR RESPOSTA
        // ========================================
        return new LoginResponseDTO(
                token,
                "Bearer",
                usuarioDTO.getId(),
                usuarioDTO.getNome(),
                usuarioDTO.getEmail(),
                usuarioDTO.getEmpresas()
        );
    }
}