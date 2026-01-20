package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.model.TipoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface TipoProdutoRepository extends JpaRepository<TipoProduto, Long> {
    boolean existsByNomeIgnoreCase(String nome);

    List<TipoProduto> findByIsAtivo(Boolean ativo);
    Optional<TipoProduto> findByNome(String nome);
    Optional<TipoProduto> findByNomeContainingIgnoreCase(String nome);
    List<TipoProduto> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fim);
}
