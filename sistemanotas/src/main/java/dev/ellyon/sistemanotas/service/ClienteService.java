package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.cliente.ClienteRequestDTO;
import dev.ellyon.sistemanotas.dto.cliente.ClienteResponseDTO;

public interface ClienteService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete - Soft Delete - Activate
    ClienteResponseDTO create(ClienteRequestDTO dto); // Criar um novo cliente
}
