package dev.ellyon.sistemanotas.repository;

import dev.ellyon.sistemanotas.model.EmpresaUsuario;
import dev.ellyon.sistemanotas.model.Usuario;
import dev.ellyon.sistemanotas.model.enums.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// Repositório JPA para a entidade. Fazer operações de CRUD no banco de dados.
public interface EmpresaUsuarioRepository extends JpaRepository<EmpresaUsuario, Long> {
    List<EmpresaUsuario> findByUsuarioId(Long usuarioId);

    Optional<EmpresaUsuario> findFirstByUsuarioId(Long usuarioId);

    List<EmpresaUsuario> findByEmpresaId(Long empresaId);

    Optional<EmpresaUsuario> findByUsuarioIdAndEmpresaId(Long usuarioId, Long empresaId);

    boolean existsByUsuarioIdAndEmpresaId(Long usuarioId, Long empresaId);

    List<EmpresaUsuario> findByPerfil(Perfil perfil);

    int countEmpresaUsuarioByEmpresaId(Long empresaId); // Contar quantas associacoes contem
}
