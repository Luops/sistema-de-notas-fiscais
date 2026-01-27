package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.itemNota.ItemNotaRequestDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaRequestDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaResponseDTO;

public interface NotaService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete
    NotaResponseDTO create(NotaRequestDTO dto);// Criar nova nota
    NotaResponseDTO addItem(Long notaId, ItemNotaRequestDTO itemNotaRequestDTO); // Adicionar item a nota
    NotaResponseDTO updateItem(Long notaId, Long itemId, ItemNotaRequestDTO itemNotaRequestDTO); // Atualizar item da nota
    NotaResponseDTO removeItem(Long notaId, Long itemId); // Remover item da nota
    NotaResponseDTO emitirNota(Long notaId); // Emitir nota
    NotaResponseDTO updateNota(Long notaId, NotaRequestDTO dto); // Atualizar dados da nota
}
