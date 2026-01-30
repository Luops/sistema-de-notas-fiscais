package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.model.ItemNota;
import dev.ellyon.sistemanotas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByIdAndIsAtivo(Long id, Boolean isAtivo);

    Optional<Usuario> findByEmail(String email);
}
