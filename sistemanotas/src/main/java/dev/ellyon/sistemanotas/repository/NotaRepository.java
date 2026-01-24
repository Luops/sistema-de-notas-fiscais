package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface NotaRepository extends JpaRepository<Nota, Long> {
    @Query("""
        SELECT MAX(CAST(n.numero AS integer))
        FROM Nota n
        WHERE n.empresa.id = :empresaId
    """)
    Integer findUltimoNumeroPorEmpresa(@Param("empresaId") Long empresaId);
}
