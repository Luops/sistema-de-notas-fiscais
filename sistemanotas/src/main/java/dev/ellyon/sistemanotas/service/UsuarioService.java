package dev.ellyon.sistemanotas.service;


import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioResponseDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioRequestDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioResponseDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioUpdateRequestDTO;

import java.util.List;

public interface UsuarioService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete - Soft Delete - Activate
    UsuarioResponseDTO create(UsuarioRequestDTO dto); // Criar um novo usuário
    UsuarioResponseDTO update(Long id, UsuarioUpdateRequestDTO dto); // Atualizar um usuário existente
    void delete(Long id); // Deletar um usuário
    void softDelete(Long id); // Deletar um usuário logicamente
    void activate(Long id); // Ativar um usuário

    // Consultas
    UsuarioResponseDTO findById(Long id); // Obter um usuário por ID
    List<UsuarioResponseDTO> findAll(); // Obter todos os usuários
    List<UsuarioResponseDTO> findByEmail(String email); // Obter usuários por email
    List<UsuarioResponseDTO> findByNome(String nome); // Obter usuários por nome
    List<UsuarioResponseDTO> findByAtivo(boolean ativo); // Obter usuários por status de ativo

}
