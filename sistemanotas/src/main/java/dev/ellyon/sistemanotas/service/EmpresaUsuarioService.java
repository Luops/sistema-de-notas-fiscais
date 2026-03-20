package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioRequestDTO;
import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioResponseDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface EmpresaUsuarioService {
    // "Contratos" - O que esse serviço deve oferecer
    EmpresaUsuarioResponseDTO associarEmpresaUsuario(EmpresaUsuarioRequestDTO dto, Authentication authentication); // Associar um usuário a uma empresa com um perfil específico (admin)
    EmpresaUsuarioResponseDTO alterarPerfil(EmpresaUsuarioRequestDTO dto, Authentication authentication); // Alterar o perfil de um usuário em uma empresa (admin)

    // Buscas
    List<EmpresaUsuarioResponseDTO> findByEmpresaId(Long empresaId, Authentication authentication); // Obter usuários por empresa (admin)
    List<EmpresaUsuarioResponseDTO> findByPerfil(String perfil, Authentication authentication); // Obter usuários por perfil (admin)
    List<EmpresaUsuarioResponseDTO> findByUsuarioId(Long usuarioId, Authentication authentication); // Obter empresas por usuario (admin e próprio usuário)
    EmpresaUsuarioResponseDTO findByEmpresaIdUsuarioId(Long empresaId, Long usuarioId, Authentication authentication); // Buscar vinculo especifico entre empresa e usuário (admin e próprio usuário)
}
