package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.model.Cliente;
import dev.ellyon.sistemanotas.model.enums.TipoPessoa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByCpfCnpj(String cpfCnpj);
    boolean existsByEmail(String email);
    List<Cliente> findByCpfCnpj(String cpfCnpj); // Buscar cliente por CPF/CNPJ
    List<Cliente> findByTipoPessoa(TipoPessoa tipoPessoa); // Buscar cliente por tipo
}
