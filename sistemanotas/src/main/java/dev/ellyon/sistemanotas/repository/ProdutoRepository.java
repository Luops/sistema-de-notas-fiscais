package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.dto.produto.ProdutoListResponseDTO;
import dev.ellyon.sistemanotas.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Optional<Produto> findByEmpresaIdAndId(Long empresaId, Long id); // Buscar produto por empresa e Id
    List<Produto> findAllByEmpresaId(Long empresaId); // Buscar todos os produtos por empresa
    List<Produto> findByEmpresaIdAndTipoProdutoId(Long empresaId, Long tipoProdutoId); // Buscar produtos por empresa e tipo (Id)
    List<Produto> findByEmpresaIdAndTipoProdutoNome(Long empresaId, String tipoProdutoNome); // Buscar produtos por empresa e tipo (Nome)
    List<Produto> findByEmpresaIdAndIsAtivo(Long empresaId, Boolean ativo); // Buscar produtos por empresa e status (Ativo/Inativo)
    List<Produto> findByEmpresaIdAndNomeContainingIgnoreCase(Long empresaId, String nome); // Buscar produtos por empresa
    List<Produto> findByEmpresaIdAndCodigoProdutoContainingIgnoreCase(Long empresaId, String codigoProduto); // Buscar produtos por empresa e código (contendo, case insensitive)
    List<Produto> findByEmpresaIdAndPrecoVendaBetween(Long empresaId, BigDecimal precoMinimo, BigDecimal precoMaximo); // Buscar produtos por empresa e faixa de preço de
    List<Produto> findByEmpresaIdAndCreatedAtBetween(Long empresaId, LocalDateTime dataInicio, LocalDateTime dataFim); // Buscar produtos por empresa e faixa de data de criação
    Page<Produto> findAllByEmpresaId(Long empresaId, Pageable pageable);

}
