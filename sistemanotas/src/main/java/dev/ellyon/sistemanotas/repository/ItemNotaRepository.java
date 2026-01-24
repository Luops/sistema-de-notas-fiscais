package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.model.ItemNota;
import dev.ellyon.sistemanotas.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface ItemNotaRepository extends JpaRepository<ItemNota, Long> {
}
