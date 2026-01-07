package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.produto.ProdutoListResponseDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoResponseDTO;
import dev.ellyon.sistemanotas.model.Produto;

import java.util.List;

public interface ProdutoService {
    // "Contratos" - O que esse serviço deve oferecer
    ProdutoResponseDTO create(ProdutoRequestDTO dto); // Criar um novo produto
    void delete(Long id); // Deletar um produto por ID
    ProdutoResponseDTO update(Long id, ProdutoRequestDTO dto); // Atualizar um produto por ID
    void softDelete(Long id); // Desativar um produto por ID
    void activate(Long id); // Ativar um produto por ID

    // Buscas
    List<ProdutoListResponseDTO> findAll(); // Buscar todos os produtos
}
