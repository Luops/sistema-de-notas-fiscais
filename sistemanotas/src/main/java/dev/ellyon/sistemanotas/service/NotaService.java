package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.nota.NotaRequestDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaResponseDTO;

public interface NotaService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete
    NotaResponseDTO create(NotaRequestDTO dto);// Criar nova nota
}
