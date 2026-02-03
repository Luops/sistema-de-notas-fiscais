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
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<SuccessResponseDTO> update(@PathVariable Long id, @RequestBody @Valid UsuarioUpdateRequestDTO dto) {
        UsuarioResponseDTO usuarioResponseDTO = usuarioService.update(id, dto);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuário atualizado com sucesso",
                usuarioResponseDTO
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para deletar um usuário
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SuccessResponseDTO> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuário deletado com sucesso",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para desativar (soft delete) um usuário
    @PutMapping("/update/soft-delete/{id}")
    public ResponseEntity<SuccessResponseDTO> softDelete(@PathVariable Long id) {
        usuarioService.softDelete(id);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuário desativado com sucesso",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para ativar um usuário
    @PutMapping("/update/activate/{id}")
    public ResponseEntity<SuccessResponseDTO> activate(@PathVariable Long id) {
        usuarioService.activate(id);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuário ativado com sucesso",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
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
    public ResponseEntity<SuccessResponseDTO> findById(@PathVariable Long id) {
        UsuarioResponseDTO usuarioResponseDTO = usuarioService.findById(id);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuário encontrado com sucesso",
                usuarioResponseDTO
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para obter todos os usuários
    @GetMapping("/findAll")
    public ResponseEntity<SuccessResponseDTO> findAll() {
        List<UsuarioResponseDTO> usuarios = usuarioService.findAll();
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuários encontrados com sucesso",
                usuarios
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para obter usuários por email
    @GetMapping("/findByEmail/{email}")
    public ResponseEntity<SuccessResponseDTO> findByEmail(@PathVariable String email) {
        List<UsuarioResponseDTO> usuarios = usuarioService.findByEmail(email);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuários encontrados com sucesso",
                usuarios
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para obter usuários por empresa
    @GetMapping("/findByEmpresaId/{empresaId}")
    public ResponseEntity<SuccessResponseDTO> findByEmpresaId(@PathVariable Long empresaId) {
        List<EmpresaUsuarioResponseDTO> usuarios = usuarioService.findByEmpresaId(empresaId);
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
        List<EmpresaUsuarioResponseDTO> usuarios = usuarioService.findByPerfil(perfil);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuários encontrados com sucesso",
                usuarios
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para obter usuários por nome
    @GetMapping("/findByNome/{nome}")
    public ResponseEntity<SuccessResponseDTO> findByNome(@PathVariable String nome) {
        List<UsuarioResponseDTO> usuarios = usuarioService.findByNome(nome);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuários encontrados com sucesso",
                usuarios
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para obter usuários por status de ativo
    @GetMapping("/findByAtivo/{ativo}")
    public ResponseEntity<SuccessResponseDTO> findByAtivo(@PathVariable boolean ativo) {
        List<UsuarioResponseDTO> usuarios = usuarioService.findByAtivo(ativo);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Usuários encontrados com sucesso",
                usuarios
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
