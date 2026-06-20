package dev.ellyon.sistemanotas.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Senha deve conter pelo menos: 1 maiúscula, 1 minúscula e 1 número"
    )
    private String senha;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "Cidade é obrigatório")
    private String cidade;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    @NotBlank(message = "CEP é obrigatório")
    private String cep;

    @NotBlank(message = "Número do endereço é obrigatório")
    private String numeroEndereco;

    @NotNull(message = "Empresa é obrigatória")
    private Long empresaId;

    @NotBlank(message = "Perfil é obrigatório")
    private String perfil;

    // Construtor padrão
    public UsuarioRequestDTO() {
    }

    // Construtor completo
    public UsuarioRequestDTO(String nome, String email, String senha, String telefone, String cidade, String endereco, String cep, String numeroEndereco, Long empresaId, String perfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.cidade = cidade;
        this.endereco = endereco;
        this.cep = cep;
        this.numeroEndereco = numeroEndereco;
        this.empresaId = empresaId;
        this.perfil = perfil;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
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

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }
}
