package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.dto.empresa.EmpresaResponseDTO;
import dev.ellyon.sistemanotas.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    boolean existsByCnpj(String cnpj);
    boolean existsByEmail(String email);

    Optional<Empresa> findByCnpj(String cnpj);  // ✅ Retorna Optional
    Optional<Empresa> findByEmail(String email); // ✅ Retorna Optional
}
