package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.model.Cliente;
import dev.ellyon.sistemanotas.model.enums.TipoPessoa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByCpfCnpj(String cpfCnpj);
    boolean existsByEmail(String email);
    List<Cliente> findByCpfCnpj(String cpfCnpj); // Buscar cliente por CPF/CNPJ
    List<Cliente> findByTipoPessoa(TipoPessoa tipoPessoa); // Buscar cliente por tipo
    List<Cliente> findByEmailContainingIgnoreCase(String email); // Buscar cliente pelo email
    List<Cliente> findByTelefoneContainingIgnoreCase(String telefone); // Buscar cliente pelo telefone
    List<Cliente> findByCidadeContainingIgnoreCase(String cidade); // Buscar cliente pela cidade
    List<Cliente> findByEstadoUF(String estadoUF); // Buscar cliente pelo estado
    List<Cliente> findByCep(String cep); // Buscar cliente pelo CEP
    List<Cliente> findByIsAtivo(Boolean isAtivo); // Buscar cliente pelo
    List<Cliente> findByCreatedAtBetween(LocalDateTime dataInicio, LocalDateTime dataFim); // Buscar clientes por faixa de data de criação
    List<Cliente> findByNomeContainingIgnoreCase(String nome); // Buscar clientes por nome (contendo, case insensitive)
}
