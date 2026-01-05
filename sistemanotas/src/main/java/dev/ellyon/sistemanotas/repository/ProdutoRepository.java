package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    boolean existsByCodigoProduto(String codigoProduto);
}
