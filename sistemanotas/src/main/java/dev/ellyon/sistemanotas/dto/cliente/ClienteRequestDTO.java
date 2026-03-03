package dev.ellyon.sistemanotas.dto.cliente;

import dev.ellyon.sistemanotas.model.enums.TipoPessoa;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ClienteRequestDTO {
    @NotBlank(message = "Nome do cliente é obrigatório")
    @Size(min = 3, max = 255, message = "Nome do cliente deve ter no mínimo 3 e no máximo 255 caracteres.")
    private String nome;

    private TipoPessoa tipoPessoa;

    @NotBlank(message = "CPF/CNPJ é obrigatório.")
    @Size(min = 11, max = 18, message = "CPF/CNPJ deve ter entre 11 e 18 caracteres.")
    private String cpfCnpj;

    private String inscricaoEstadual;

    @NotBlank(message = "Email é obrigatório.")
    @Email(message = "Email deve ser válido")
    private String email;

    @NotBlank(message = "Telefone é obrigatório.")
    @Size(min = 10, max = 15, message = "Telefone deve ter entre 10 e 15 caracteres.")
    private String telefone;

    @NotBlank(message = "Endereço completo é obrigatório.")
    @Size(min = 10, max = 255, message = "Endereço completo deve ter no mínimo 10 e no máximo 255 caracteres.")
    private String enderecoCompleto;

    @NotBlank(message = "Cidade é obrigatória.")
    @Size(min = 3, max = 255, message = "Cidade deve ter no mínimo 3 e no máximo 255 caracteres.")
    private String cidade;

    @NotBlank(message = "Estado (UF) é obrigatório.")
    @Size(min = 2, max = 2, message = "Estado (UF) deve ter exatamente 2 caracteres.")
    private String estadoUF;

    @NotBlank(message = "CEP é obrigatório.")
    @Size(min = 8, max = 9, message = "CEP deve ter entre 8 e 9 caracteres.")
    private String cep;

    @NotBlank(message = "Bairro é obrigatório.")
    @Size(min = 3, max = 255, message = "Bairro deve ter no mínimo 3 e no máximo 255 caracteres.")
    private String bairro;

    private Boolean isAtivo;

    // Construtor padrão
    public ClienteRequestDTO() {
    }

    // Construtor completo
    public ClienteRequestDTO(String nome, TipoPessoa tipoPessoa, String cpfCnpj, String inscricaoEstadual, String email, String telefone, String enderecoCompleto, String cidade, String estadoUF, String cep, String bairro, Boolean isAtivo) {
        this.nome = nome;
        this.tipoPessoa = tipoPessoa;
        this.cpfCnpj = cpfCnpj;
        this.inscricaoEstadual = inscricaoEstadual;
        this.email = email;
        this.telefone = telefone;
        this.enderecoCompleto = enderecoCompleto;
        this.cidade = cidade;
        this.estadoUF = estadoUF;
        this.cep = cep;
        this.bairro = bairro;
        this.isAtivo = isAtivo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
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

    public String getEnderecoCompleto() {
        return enderecoCompleto;
    }

    public void setEnderecoCompleto(String enderecoCompleto) {
        this.enderecoCompleto = enderecoCompleto;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstadoUF() {
        return estadoUF;
    }

    public void setEstadoUF(String estadoUF) {
        this.estadoUF = estadoUF;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public Boolean getAtivo() {
        return isAtivo;
    }

    public void setAtivo(Boolean ativo) {
        isAtivo = ativo;
    }
}
