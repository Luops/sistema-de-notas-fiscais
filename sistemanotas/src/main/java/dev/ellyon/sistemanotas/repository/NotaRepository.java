package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface NotaRepository extends JpaRepository<Nota, Long> {
    @Query("""
        SELECT MAX(CAST(n.numero AS integer))
        FROM Nota n
        WHERE n.empresa.id = :empresaId
    """)
    Integer findUltimoNumeroPorEmpresa(@Param("empresaId") Long empresaId);
    Nota findByNumeroAndEmpresaId(@Param("numero") String numero, @Param("empresaId") Long empresaId);

    List<Nota> findByDataEmissaoBetweenOrderByDataEmissaoDesc(
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    );

    List<Nota> findByDataCancelamentoBetweenOrderByDataCancelamentoDesc(
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    );
}
