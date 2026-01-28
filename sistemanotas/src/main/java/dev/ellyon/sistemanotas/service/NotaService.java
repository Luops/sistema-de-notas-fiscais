package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.itemNota.ItemNotaRequestDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaListResponseDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaRequestDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotaService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete
    NotaResponseDTO create(NotaRequestDTO dto);// Criar nova nota
    NotaResponseDTO addItem(Long notaId, ItemNotaRequestDTO itemNotaRequestDTO); // Adicionar item a nota
    NotaResponseDTO updateItem(Long notaId, Long itemId, ItemNotaRequestDTO itemNotaRequestDTO); // Atualizar item da nota
    NotaResponseDTO removeItem(Long notaId, Long itemId); // Remover item da nota
    NotaResponseDTO emitirNota(Long notaId); // Emitir nota
    NotaResponseDTO updateNota(Long notaId, NotaRequestDTO dto); // Atualizar dados da nota
    void cancelarNota(Long notaId); // Cancelar nota

    // Buscas
    NotaResponseDTO findById(Long notaId); // Buscar nota por ID
    List<NotaListResponseDTO> findAll(); // Buscar todas as notas
    Page<NotaListResponseDTO> findAllPaged(Pageable pageable); // Buscar todas as notas com paginação
    NotaResponseDTO findByNumeroAndEmpresaId(Long empresaId, String numero); // Buscar nota por número e empresa
}
