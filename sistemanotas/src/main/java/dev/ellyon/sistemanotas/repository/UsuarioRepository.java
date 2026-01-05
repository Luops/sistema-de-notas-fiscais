package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.model.ItemNota;
import dev.ellyon.sistemanotas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
