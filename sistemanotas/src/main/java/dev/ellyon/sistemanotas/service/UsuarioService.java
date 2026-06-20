package dev.ellyon.sistemanotas.service;


import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioResponseDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioRequestDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioResponseDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioUpdateRequestDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UsuarioService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete - Soft Delete - Activate
    UsuarioResponseDTO create(UsuarioRequestDTO dto); // Criar um novo usuário
    UsuarioResponseDTO update(Long id, UsuarioUpdateRequestDTO dto, Authentication authentication); // Atualizar um usuário existente
    void delete(Long id, String senha, Authentication authentication); // Deletar um usuário
    void softDelete(Long id, Authentication authentication); // Deletar um usuário logicamente
    void activate(Long id, Authentication authentication); // Ativar um usuário

    // Consultas
    UsuarioResponseDTO findById(Long id, Authentication authentication); // Obter um usuário por ID
    List<UsuarioResponseDTO> findAll(Authentication authentication); // Obter todos os usuários
    List<UsuarioResponseDTO> findByEmail(String email, Authentication authentication); // Obter usuários por email
    List<UsuarioResponseDTO> findByNome(String nome, Authentication authentication); // Obter usuários por nome
    List<UsuarioResponseDTO> findByAtivo(boolean ativo, Authentication authentication); // Obter usuários por status de ativo

    boolean isOwnProfile(Long userId); // Verificar se é seu próprio perfil
}
