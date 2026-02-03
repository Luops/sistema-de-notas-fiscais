package dev.ellyon.sistemanotas.dto.empresaUsuario;

import java.time.LocalDateTime;

public class EmpresaUsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private Boolean isAtivo;
    private String perfil;
    private LocalDateTime createdAt;

    // Construtor padrão
    public EmpresaUsuarioResponseDTO() {
    }

    // Construtor completo
    public EmpresaUsuarioResponseDTO(Long id, String nome, String email, Boolean isAtivo,
                                     String perfil, LocalDateTime createdAt) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.isAtivo = isAtivo;
        this.perfil = perfil;
        this.createdAt = createdAt;
    }

    // Getters e Setters
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

    public Boolean getIsAtivo() {
        return isAtivo;
    }

    public void setIsAtivo(Boolean isAtivo) {
        this.isAtivo = isAtivo;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
