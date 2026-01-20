package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.produto.ProdutoListResponseDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoResponseDTO;
import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoResponseDTO;
import dev.ellyon.sistemanotas.model.TipoProduto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TipoProdutoService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete - Soft Delete - Activate
    TipoProdutoResponseDTO create(TipoProdutoRequestDTO dto); // Criar um novo tipo de produto
    void delete(Long id); // Deletar um tipo de produto por ID
    TipoProdutoResponseDTO update(Long id, TipoProdutoRequestDTO dto); // Atualizar um tipo de produto por ID
    void softDelete(Long id); // Desativar um tipo de produto por ID
    void activate(Long id); // Ativar um tipo de produto por ID

    // Buscas
    TipoProdutoResponseDTO findById(Long id); // Buscar um tipo de produto por ID
    List<TipoProdutoResponseDTO> findAll(); // Buscar todos os tipos de produtos
    List<TipoProdutoResponseDTO> findByAtivoInativo(Boolean ativo); // Buscar tipos de produtos por status (ativo/inativo)
    TipoProdutoResponseDTO findByNomeContainingIgnoreCase(String nome); // Buscar tipos de produtos por nome (contendo, case insensitive)
    List<TipoProdutoResponseDTO> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fim); // Buscar tipos de produtos criados entre duas datas
}
