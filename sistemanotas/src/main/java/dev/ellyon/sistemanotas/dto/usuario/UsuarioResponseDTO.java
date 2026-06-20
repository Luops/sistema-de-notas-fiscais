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
    private String telefone;
    private String cidade;
    private String endereco;
    private String cep;
    private String numeroEndereco;
    private Boolean isAtivo;
    private List<EmpresaUsuarioSimpleResponseDTO> empresas;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

    // Construtor padrao
    public UsuarioResponseDTO() {}

    // Construtor completo
    public UsuarioResponseDTO(Long id, String nome, String email, String telefone, String cidade, String endereco, String cep, String numeroEndereco, Boolean isAtivo, List<EmpresaUsuarioSimpleResponseDTO> empresas, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cidade = cidade;
        this.endereco = endereco;
        this.cep = cep;
        this.numeroEndereco = numeroEndereco;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNumeroEndereco() {
        return numeroEndereco;
    }

    public void setNumeroEndereco(String numeroEndereco) {
        this.numeroEndereco = numeroEndereco;
    }

    public Boolean getIsAtivo() {
        return isAtivo;
    }

    public void setIsAtivo(Boolean ativo) {
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
