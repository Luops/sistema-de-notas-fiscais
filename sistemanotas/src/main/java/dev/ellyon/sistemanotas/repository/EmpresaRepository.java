package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.dto.empresa.EmpresaResponseDTO;
import dev.ellyon.sistemanotas.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    boolean existsByCnpj(String cnpj);
    boolean existsByEmail(String email);

    Optional<Empresa> findByCnpj(String cnpj);  // ✅ Retorna Optional
    Optional<Empresa> findByEmail(String email); // ✅ Retorna Optional
    List<Empresa> findByRazaoSocialContainingIgnoreCase(String razaoSocial); // Retorna lista de empresas com razão social contendo o termo
    List<Empresa> findByNomeFantasiaContainingIgnoreCase(String nomeFantasia); // Retorna lista de empresas com nome fantasia contendo o termo
    List<Empresa> findByEmailContainingIgnoreCase(String email); // Retorna lista de empresas com email contendo o termo
    List<Empresa> findByTelefoneContaining(String telefone); // Retorna lista de empresas com o
    List<Empresa> findByCidadeIgnoreCase(String cidade); // Retorna lista de empresas na cidade
    List<Empresa> findByEstadoUFIgnoreCase(String estadoUF); // Retorna lista de empresas no estado (UF)
    List<Empresa> findByCep(String cep); // Retorna lista de empresas com o CEP
    List<Empresa> findByIsAtivo(Boolean ativo); // Retorna lista de empresas por status de ativo/inativo
    List<Empresa> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fim); // Retorna lista de empresas criadas entre duas datas
}
