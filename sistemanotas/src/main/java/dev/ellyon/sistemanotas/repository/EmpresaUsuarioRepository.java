package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.model.EmpresaUsuario;
import dev.ellyon.sistemanotas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface EmpresaUsuarioRepository extends JpaRepository<EmpresaUsuario, Long> {
}
