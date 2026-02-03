package dev.ellyon.sistemanotas.dto.auth;

import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioSimpleResponseDTO;

import java.util.List;

public class LoginResponseDTO {

    private String token;
    private String tipo; // "Bearer"
    private Long usuarioId;
    private String nome;
    private String email;
    private List<EmpresaUsuarioSimpleResponseDTO> empresas;

    // Construtor padrão
    public LoginResponseDTO() {
    }

    // Construtor completo
    public LoginResponseDTO(String token, String tipo, Long usuarioId, String nome, String email,
                            List<EmpresaUsuarioSimpleResponseDTO> empresas) {
        this.token = token;
        this.tipo = tipo;
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.email = email;
        this.empresas = empresas;
    }

    // Getters e Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<EmpresaUsuarioSimpleResponseDTO> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(List<EmpresaUsuarioSimpleResponseDTO> empresas) {
        this.empresas = empresas;
    }
}