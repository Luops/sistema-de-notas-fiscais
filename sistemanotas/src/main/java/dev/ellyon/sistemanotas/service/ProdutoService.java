package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.produto.ProdutoListResponseDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoResponseDTO;
import org.springframework.security.core.Authentication;
import dev.ellyon.sistemanotas.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ProdutoService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete - Soft Delete - Activate
    ProdutoResponseDTO create(ProdutoRequestDTO dto, Authentication authentication); // Criar um novo produto
    void delete(Long id, Authentication authentication); // Deletar um produto por ID
    ProdutoResponseDTO update(Long id, ProdutoRequestDTO dto, Authentication authentication); // Atualizar um produto por ID
    void softDelete(Long id, Authentication authentication); // Desativar um produto por ID
    void activate(Long id, Authentication authentication); // Ativar um produto por ID

    // Buscas
    ProdutoResponseDTO findById(Long id, Authentication authentication); // Buscar um produto por ID
    List<ProdutoListResponseDTO> findAll(Authentication authentication); // Buscar todos os produtos
    List<ProdutoListResponseDTO> findByTipoProdutoId(Long tipoProdutoId, Authentication authentication); // Buscar produtos por tipo (ID)
    List<ProdutoListResponseDTO> findByTipoProdutoNome(String tipoProdutoNome, Authentication authentication); // Buscar produtos por tipo (Nome)
    List<ProdutoListResponseDTO> findByIsAtivo(Boolean ativo, Authentication authentication); // Buscar produtos por status (Ativo/Inativo)
    List<ProdutoListResponseDTO> findByNomeContainingIgnoreCase(String nome, Authentication authentication); // Buscar produtos por nome (contendo, case insensitive)
    List<ProdutoListResponseDTO> findByCodigoProdutoContainingIgnoreCase(String codigoProduto, Authentication authentication); // Buscar produtos por código (contendo, case insensitive)
    List<ProdutoListResponseDTO> findByPrecoVendaBetween(BigDecimal precoMinimo, BigDecimal precoMaximo, Authentication authentication); // Buscar produtos por faixa de preço de venda
    List<ProdutoListResponseDTO> findByCreatedAtBetween(LocalDateTime dataInicio, LocalDateTime dataFim, Authentication authentication); // Buscar produtos por faixa de data de criação
    Page<ProdutoListResponseDTO> findAllPaged(Pageable pageable, Authentication authentication); // Buscar todos os produtos com paginação
}
