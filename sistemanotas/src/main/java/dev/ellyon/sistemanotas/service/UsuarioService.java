package dev.ellyon.sistemanotas.service;


import dev.ellyon.sistemanotas.dto.usuario.UsuarioRequestDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioResponseDTO;

public interface UsuarioService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete - Soft Delete - Activate
    UsuarioResponseDTO create(UsuarioRequestDTO dto); // Criar um novo usuário

}
