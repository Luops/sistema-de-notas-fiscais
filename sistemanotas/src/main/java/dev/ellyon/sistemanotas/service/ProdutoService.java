package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.produto.ProdutoRequestDTO;
import dev.ellyon.sistemanotas.model.Produto;

public interface ProdutoService {
    Produto create(ProdutoRequestDTO dto);
}
