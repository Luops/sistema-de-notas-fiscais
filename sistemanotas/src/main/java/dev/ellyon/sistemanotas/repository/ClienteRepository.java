package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.model.Cliente;
import dev.ellyon.sistemanotas.model.enums.TipoPessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByCpfCnpj(String cpfCnpj);
    boolean existsByEmail(String email);
    Optional<Cliente> findByCpfCnpj(String cpfCnpj); // Buscar cliente por CPF/CNPJ
    List<Cliente> findByTipoPessoa(TipoPessoa tipoPessoa); // Buscar cliente por tipo
    Optional<Cliente> findByEmailContainingIgnoreCase(String email); // Buscar cliente pelo email
    Optional<Cliente> findByEmpresaIdAndEmailContainingIgnoreCase(Long empresaId, String email); // Buscar cliente pelo email e empresa
    List<Cliente> findByTelefoneContainingIgnoreCase(String telefone); // Buscar cliente pelo telefone
    List<Cliente> findByEmpresaIdAndTelefoneContainingIgnoreCase(Long empresaId, String telefone); // Buscar cliente pelo telefone e empresa
    List<Cliente> findByCidadeContainingIgnoreCase(String cidade); // Buscar cliente pela cidade
    List<Cliente> findByEmpresaIdAndCidadeContainingIgnoreCase(Long empresaId, String cidade); // Buscar cliente pela cidade e empresa
    List<Cliente> findByEstadoUF(String estadoUF); // Buscar cliente pelo estado
    List<Cliente> findByEmpresaIdAndEstadoUF(Long empresaId, String estadoUF); // Buscar cliente pelo estado e empresa
    List<Cliente> findByCep(String cep); // Buscar cliente pelo CEP
    List<Cliente> findByEmpresaIdAndCep(Long empresaId, String cep); // Buscar cliente pelo CEP e empresa
    List<Cliente> findByIsAtivo(Boolean isAtivo); // Buscar cliente pelo
    List<Cliente> findByEmpresaIdAndIsAtivo(Long empresaId, Boolean isAtivo); // Buscar cliente pelo status e empresa
    List<Cliente> findByCreatedAtBetween(LocalDateTime dataInicio, LocalDateTime dataFim); // Buscar clientes por faixa de data de criação
    List<Cliente> findByEmpresaIdAndCreatedAtBetween(Long empresaId, LocalDateTime dataInicio, LocalDateTime dataFim); // Buscar clientes por faixa de data de criação e empresa
    List<Cliente> findByNomeContainingIgnoreCase(String nome); // Buscar clientes por nome (contendo, case insensitive)
    List<Cliente> findByEmpresaIdAndNomeContainingIgnoreCase(Long empresaId, String nome); // Buscar clientes por nome e empresa
    List<Cliente> findByEmpresaId(Long empresaId); // Buscar clientes por empresa
    Page<Cliente> findByEmpresaId(Long empresaId, Pageable pageable); // Buscar clientes por empresa com paginação
    List<Cliente> findByEmpresaIdAndTipoPessoa(Long empresaId, TipoPessoa tipoPessoa); // Buscar clientes por empresa e tipo

    Optional<Cliente> findByIdAndIsAtivo(Long id, Boolean isAtivo); // Buscar cliente por Id e status (Ativo/Inativo)
    Optional<Cliente> findByCpfCnpjHash(String cpfCnpjHash); // Buscar cliente por hash do CPF/CNPJ
}
