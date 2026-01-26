package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.dto.produto.ProdutoListResponseDTO;
import dev.ellyon.sistemanotas.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    boolean existsByCodigoProduto(String codigoProduto);

    List<Produto> findByTipoProdutoId(Long tipoProdutoId); // Buscar produtos por tipo (Id)
    List<Produto> findByTipoProdutoNome(String tipoProdutoNome); // Buscar produtos por tipo (Nome)
    List<Produto> findByIsAtivo(Boolean ativo); // Buscar produtos por status (Ativo/Inativo)
    List<Produto> findByNomeContainingIgnoreCase(String nome); // Buscar produtos por nome (contendo, case insensitive)
    List<Produto> findByCodigoProdutoContainingIgnoreCase(String codigoProduto); // Buscar produtos por código (contendo, case insensitive)
    List<Produto> findByPrecoVendaBetween(BigDecimal precoMinimo, BigDecimal precoMaximo); // Buscar produtos por faixa de preço de venda
    List<Produto> findByCreatedAtBetween(LocalDateTime dataInicio, LocalDateTime dataFim); // Buscar produtos por faixa de data de criação

    long countByTipoProdutoId(Long tipoProdutoId); // Contar produtos por tipo (Id)

    Optional<Produto> findByIdAndIsAtivo(Long id, Boolean isAtivo); // Buscar produto por Id e status (Ativo/Inativo)
}
