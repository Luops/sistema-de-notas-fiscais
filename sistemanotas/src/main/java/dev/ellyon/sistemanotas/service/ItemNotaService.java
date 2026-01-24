package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.itemNota.ItemNotaRequestDTO;
import dev.ellyon.sistemanotas.dto.itemNota.ItemNotaResponseDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoResponseDTO;

import java.util.List;

public interface ItemNotaService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete
    ItemNotaResponseDTO adicionarItem(Long notaId, ItemNotaRequestDTO dto);// Adicionar item a uma nota existente
    ItemNotaResponseDTO atualizarItem(Long notaId, Long itemId, ItemNotaRequestDTO dto);// Atualizar item existente
    void removerItem(Long notaId, Long itemId);// Remover item da nota
    List<ItemNotaResponseDTO> listarItensDaNota(Long notaId);// Listar itens de uma nota
    ItemNotaResponseDTO buscarItemPorId(Long notaId, Long itemId);// Buscar item específico

    // Buscas
}
