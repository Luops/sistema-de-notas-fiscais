package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.produto.ProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoResponseDTO;
import dev.ellyon.sistemanotas.model.Produto;

public interface ProdutoService {
    ProdutoResponseDTO create(ProdutoRequestDTO dto);
}
