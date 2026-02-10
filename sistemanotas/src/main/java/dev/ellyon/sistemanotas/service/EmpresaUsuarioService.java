package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioRequestDTO;
import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioResponseDTO;

import java.util.List;

public interface EmpresaUsuarioService {
    // "Contratos" - O que esse serviço deve oferecer
    EmpresaUsuarioResponseDTO associarEmpresaUsuario(EmpresaUsuarioRequestDTO dto);
    EmpresaUsuarioResponseDTO alterarPerfil(EmpresaUsuarioRequestDTO dto);

    // Buscas
    List<EmpresaUsuarioResponseDTO> findByEmpresaId(Long empresaId); // Obter usuários por empresa
    List<EmpresaUsuarioResponseDTO> findByPerfil(String perfil); // Obter usuários por perfil
    List<EmpresaUsuarioResponseDTO> findByUsuarioId(Long usuarioId); // Obter empresas por usuario
    EmpresaUsuarioResponseDTO findByEmpresaIdUsuarioId(Long empresaId, Long usuarioId); // Buscar vinculo especifico
}
