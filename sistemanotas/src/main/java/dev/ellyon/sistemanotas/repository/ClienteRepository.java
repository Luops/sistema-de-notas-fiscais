package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByCpfCnpj(String cpfCnpj);
    boolean existsByEmail(String email);
}
