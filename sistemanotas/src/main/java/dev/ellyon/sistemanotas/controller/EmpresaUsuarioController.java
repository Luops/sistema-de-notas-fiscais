package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioRequestDTO;
import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioResponseDTO;
import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.service.EmpresaUsuarioService;
import dev.ellyon.sistemanotas.service.mapper.EmpresaUsuarioMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<SuccessResponseDTO> associarEmpresaUsuario(
            @RequestBody @Valid EmpresaUsuarioRequestDTO dto) {

        EmpresaUsuarioResponseDTO responseDTO = empresaUsuarioService.associarEmpresaUsuario(dto);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Usuário associado à empresa com sucesso",
                responseDTO
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Rota para alterar perfil
    @PostMapping("/update-perfil")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SuccessResponseDTO> alterarPerfil(
            @RequestBody @Valid EmpresaUsuarioRequestDTO dto) {

        EmpresaUsuarioResponseDTO responseDTO = empresaUsuarioService.alterarPerfil(dto);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Perfil alterado com sucesso",
                responseDTO
        );
        return ResponseEntity.ok(response);
    }

    // Rota para obter usuários por empresa
    @GetMapping("/findByEmpresaId/{empresaId}")
    public ResponseEntity<SuccessResponseDTO> findByEmpresaId(@PathVariable Long empresaId) {
        List<EmpresaUsuarioResponseDTO> usuarios = empresaUsuarioService.findByEmpresaId(empresaId);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuários encontrados com sucesso",
                usuarios
        );
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    // Rota para obter usuários por perfil
    @GetMapping("/findByPerfil/{perfil}")
    public ResponseEntity<SuccessResponseDTO> findByPerfil(@PathVariable String perfil) {
        List<EmpresaUsuarioResponseDTO> usuarios = empresaUsuarioService.findByPerfil(perfil);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuários encontrados com sucesso",
                usuarios
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para obter empresas por usuario
    @GetMapping("/findByUsuarioId/{usuarioId}")
    public ResponseEntity<SuccessResponseDTO> findByUsuarioId(@PathVariable Long usuarioId) {
        List<EmpresaUsuarioResponseDTO> empresas = empresaUsuarioService.findByUsuarioId(usuarioId);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresas encontradas com sucesso",
                empresas
        );
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
