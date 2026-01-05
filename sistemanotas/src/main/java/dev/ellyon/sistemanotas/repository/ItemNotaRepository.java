package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.model.ItemNota;
import dev.ellyon.sistemanotas.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface ItemNotaRepository extends JpaRepository<ItemNota, Long> {
}
