package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.produto.ProdutoListResponseDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoResponseDTO;
import dev.ellyon.sistemanotas.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ProdutoService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete - Soft Delete - Activate
    ProdutoResponseDTO create(ProdutoRequestDTO dto); // Criar um novo produto
    void delete(Long id); // Deletar um produto por ID
    ProdutoResponseDTO update(Long id, ProdutoRequestDTO dto); // Atualizar um produto por ID
    void softDelete(Long id); // Desativar um produto por ID
    void activate(Long id); // Ativar um produto por ID

    // Buscas
    ProdutoResponseDTO findById(Long id); // Buscar um produto por ID
    List<ProdutoListResponseDTO> findAll(); // Buscar todos os produtos
    List<ProdutoListResponseDTO> findByTipoProdutoId(Long tipoProdutoId); // Buscar produtos por tipo (ID)
    List<ProdutoListResponseDTO> findByTipoProdutoNome(String tipoProdutoNome); // Buscar produtos por tipo (Nome)
    List<ProdutoListResponseDTO> findByIsAtivo(Boolean ativo); // Buscar produtos por status (Ativo/Inativo)
    List<ProdutoListResponseDTO> findByNomeContainingIgnoreCase(String nome); // Buscar produtos por nome (contendo, case insensitive)
    List<ProdutoListResponseDTO> findByCodigoProdutoContainingIgnoreCase(String codigoProduto); // Buscar produtos por código (contendo, case insensitive)
    List<ProdutoListResponseDTO> findByPrecoVendaBetween(BigDecimal precoMinimo, BigDecimal precoMaximo); // Buscar produtos por faixa de preço de venda
    List<ProdutoListResponseDTO> findByCreatedAtBetween(LocalDateTime dataInicio, LocalDateTime dataFim); // Buscar produtos por faixa de data de criação
    Page<ProdutoListResponseDTO> findAllPaged(Pageable pageable); // Buscar todos os produtos com paginação
}
