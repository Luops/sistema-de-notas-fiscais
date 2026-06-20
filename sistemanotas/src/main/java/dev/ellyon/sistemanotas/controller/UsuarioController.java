package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.auth.LoginRequestDTO;
import dev.ellyon.sistemanotas.dto.auth.LoginResponseDTO;
import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioResponseDTO;
import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioRequestDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioResponseDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioUpdateRequestDTO;
import dev.ellyon.sistemanotas.model.Usuario;
import dev.ellyon.sistemanotas.service.AuthService;
import dev.ellyon.sistemanotas.service.UsuarioService;
import dev.ellyon.sistemanotas.service.mapper.UsuarioMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usuario")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final AuthService authService;

    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper, AuthService authService) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
        this.authService = authService;
    }

    // Rota para criar um novo usuário
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SuccessResponseDTO> create(@RequestBody @Valid UsuarioRequestDTO dto) {
        UsuarioResponseDTO usuarioResponseDTO = usuarioService.create(dto);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Usuário criado com sucesso",
                usuarioResponseDTO
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Rota para atualizar um usuário existente
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or @usuarioServiceImpl.isOwnProfile(#id)")
    public ResponseEntity<SuccessResponseDTO> update(@PathVariable Long id, @RequestBody @Valid UsuarioUpdateRequestDTO dto, Authentication authentication) {
        UsuarioResponseDTO response = usuarioService.update(id, dto, authentication);
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuário atualizado com sucesso",
                response
        );
        return ResponseEntity.ok(successResponse);
    }

    // Rota para deletar um usuário
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> delete(@PathVariable Long id,@RequestBody Map<String, String> body, Authentication authentication) {
        String senha = body.get("senha");
        usuarioService.delete(id, senha, authentication);
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuário deletado com sucesso",
                null
        );
        return ResponseEntity.ok(successResponse);
    }

    // Rota para desativar (soft delete) um usuário
    @PutMapping("/update/soft-delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> softDelete(@PathVariable Long id, Authentication authentication) {
        usuarioService.softDelete(id, authentication);
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuário desativado com sucesso",
                null
        );
        return ResponseEntity.ok(successResponse);
    }

    // Rota para ativar um usuário
    @PutMapping("/update/activate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> activate(@PathVariable Long id, Authentication authentication) {
        usuarioService.activate(id, authentication);
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuário ativado com sucesso",
                null
        );
        return ResponseEntity.ok(successResponse);
    }

    @PostMapping("/auth/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SuccessResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        LoginResponseDTO loginResponse = authService.autenticar(dto);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Login realizado com sucesso",
                loginResponse
        );
        return ResponseEntity.ok(response);
    }

    // Rota para obter um usuário por ID
    @GetMapping("/findById/{id}")
    @PreAuthorize("hasRole('ADMIN') or @usuarioServiceImpl.isOwnProfile(#id)")
    public ResponseEntity<SuccessResponseDTO> findById(@PathVariable Long id, Authentication authentication) {
        UsuarioResponseDTO response = usuarioService.findById(id, authentication);
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuário encontrado com sucesso",
                response
        );
        return ResponseEntity.ok(successResponse);
    }

    // Rota para obter todos os usuários
    @GetMapping("/findAll")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findAll(Authentication authentication) {
        List<UsuarioResponseDTO> response = usuarioService.findAll(authentication);
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuários encontrados com sucesso",
                response
        );
        return ResponseEntity.ok(successResponse);
    }

    // Rota para obter usuários por email
    @GetMapping("/findByEmail/{email}")
    @PreAuthorize("hasRole('ADMIN') or @usuarioServiceImpl.isOwnProfileByEmail(#email)")
    public ResponseEntity<SuccessResponseDTO> findByEmail(@PathVariable String email, Authentication authentication) {
        List<UsuarioResponseDTO> response = usuarioService.findByEmail(email, authentication);
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuários encontrados com sucesso",
                response
        );
        return ResponseEntity.ok(successResponse);
    }

    // Rota para obter usuários por nome
    @GetMapping("/findByNome/{nome}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SuccessResponseDTO> findByNome(@PathVariable String nome, Authentication authentication) {
        List<UsuarioResponseDTO> response = usuarioService.findByNome(nome, authentication);
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuários encontrados com sucesso",
                response
        );
        return ResponseEntity.ok(successResponse);
    }

    // Rota para obter usuários por status de ativo
    @GetMapping("/findByAtivo/{ativo}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SuccessResponseDTO> findByAtivo(@PathVariable boolean ativo, Authentication authentication) {
        List<UsuarioResponseDTO> response = usuarioService.findByAtivo(ativo, authentication);
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuários encontrados com sucesso",
                response
        );
        return ResponseEntity.ok(successResponse);
    }

}
