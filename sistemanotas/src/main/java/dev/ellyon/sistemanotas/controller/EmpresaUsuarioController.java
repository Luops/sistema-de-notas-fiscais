package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioRequestDTO;
import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioResponseDTO;
import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.service.EmpresaUsuarioService;
import dev.ellyon.sistemanotas.service.mapper.EmpresaUsuarioMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/v1/empresa-usuario")
public class EmpresaUsuarioController {
    private final EmpresaUsuarioService empresaUsuarioService;
    private final EmpresaUsuarioMapper empresaUsuarioMapper;
    public EmpresaUsuarioController(EmpresaUsuarioService empresaUsuarioService, EmpresaUsuarioMapper empresaUsuarioMapper) {
        this.empresaUsuarioService = empresaUsuarioService;
        this.empresaUsuarioMapper = empresaUsuarioMapper;
    }

    // Rota para associar empresa e usuário
    @PostMapping("/associar")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> associarEmpresaUsuario(
            @RequestBody @Valid EmpresaUsuarioRequestDTO dto, Authentication authentication) {

        EmpresaUsuarioResponseDTO response = empresaUsuarioService.associarEmpresaUsuario(dto, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Usuário associado à empresa com sucesso",
                response
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
    }

    // Rota para alterar perfil
    @PostMapping("/update-perfil")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SuccessResponseDTO> alterarPerfil(
            @RequestBody @Valid EmpresaUsuarioRequestDTO dto, Authentication authentication) {

        EmpresaUsuarioResponseDTO response = empresaUsuarioService.alterarPerfil(dto, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Perfil alterado com sucesso",
                response
        );
        return ResponseEntity.ok(successResponse);
    }

    // Rota para obter usuários por empresa
    @GetMapping("/findByEmpresaId/{empresaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> findByEmpresaId(@PathVariable Long empresaId, Authentication authentication) {
        List<EmpresaUsuarioResponseDTO> response = empresaUsuarioService.findByEmpresaId(empresaId, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuários encontrados com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);

    }

    // Rota para obter usuários por perfil
    @GetMapping("/findByPerfil/{perfil}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> findByPerfil(@PathVariable String perfil, Authentication authentication) {
        List<EmpresaUsuarioResponseDTO> response = empresaUsuarioService.findByPerfil(perfil, authentication);
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuários encontrados com sucesso",
                response
        );
        return ResponseEntity.ok(successResponse);
    }

    // Rota para obter empresas por usuario
    @GetMapping("/findByUsuarioId/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByUsuarioId(@PathVariable Long usuarioId, Authentication authentication) {
        List<EmpresaUsuarioResponseDTO> response = empresaUsuarioService.findByUsuarioId(usuarioId, authentication);
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresas encontradas com sucesso",
                response
        );
        return ResponseEntity.ok(successResponse);

    }

    // Rota para obter o vinculo entre empresa e usuario especifico
    @GetMapping("/vinculo")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByUsuarioIdAndEmpresaId(@Valid
            @RequestParam  Long empresaId,
            @RequestParam  Long usuarioId, Authentication authentication) {

        EmpresaUsuarioResponseDTO response = empresaUsuarioService.findByEmpresaIdUsuarioId(empresaId,usuarioId, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Encontrado com sucesso vínculo entre Empresa e Usuário!",
                response
        );
        return ResponseEntity.ok(successResponse);
    }
}
