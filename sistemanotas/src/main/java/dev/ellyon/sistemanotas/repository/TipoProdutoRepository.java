package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.model.TipoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface TipoProdutoRepository extends JpaRepository<TipoProduto, Long> {
}
