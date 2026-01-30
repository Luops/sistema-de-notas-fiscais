package dev.ellyon.sistemanotas.dto.usuario;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.ellyon.sistemanotas.dto.empresa.EmpresaSimpleResponseDTO;
import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioSimpleResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private Boolean isAtivo;
    private List<EmpresaUsuarioSimpleResponseDTO> empresas;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

    // Construtor padrao
    public UsuarioResponseDTO() {}

    // Construtor completo
    public UsuarioResponseDTO(Long id, String nome, String email, Boolean isAtivo,
                              List<EmpresaUsuarioSimpleResponseDTO> empresas,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.isAtivo = isAtivo;
        this.empresas = empresas;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getAtivo() {
        return isAtivo;
    }

    public void setAtivo(Boolean ativo) {
        isAtivo = ativo;
    }

    public List<EmpresaUsuarioSimpleResponseDTO> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(List<EmpresaUsuarioSimpleResponseDTO> empresas) {
        this.empresas = empresas;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
