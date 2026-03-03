package dev.ellyon.sistemanotas.dto.empresa;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class EmpresaRequestDTO {
    @NotBlank(message = "Razão Social é obrigatória.")
    @Size(min = 3, max = 255, message = "Razão Social deve ter no mínimo 3 e no máximo 255 caracteres.")
    private String razaoSocial;

    @NotBlank(message = "Nome Fantasia é obrigatório.")
    @Size(min = 3, max = 255, message = "Nome Fantasia deve ter no mínimo 3 e no máximo 255 caracteres.")
    private String nomeFantasia;

    @NotBlank(message = "CNPJ é obrigatório.")
    @Size(min = 14, max = 14, message = "CNPJ deve ter 14 caracteres.")
    private String cnpj;

    private String inscricaoEstadual;

    @NotBlank(message = "Endereço Completo é obrigatório.")
    @Size(min = 10, max = 255, message = "Endereço Completo deve ter no mínimo 10 e no máximo 255 caracteres.")
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

    @NotBlank(message = "Telefone é obrigatório.")
    @Size(min = 10, max = 15, message = "Telefone deve ter entre 10 e 15 caracteres.")
    private String telefone;

    @NotBlank(message = "Email é obrigatório.")
    @Email(message = "Email deve ser válido")
    private String email;

    private String logoUrl;

    private Boolean isAtivo;

    private byte[] certificadoDigital;

    private String certificadoSenhaCriptografada;  // Senha criptografada

    private String certificadoTipo = "A1";  // A1 ou A3

    private LocalDateTime certificadoValidade;  // Data de vencimento

    private String certificadoCnpj;  // CNPJ do certificado

    private Boolean certificadoAtivo = false;  // Se está configurado

    private LocalDateTime certificadoUploadDate;  // Data do upload

    public EmpresaRequestDTO(){

    }

    public EmpresaRequestDTO(String razaoSocial, String nomeFantasia, String cnpj, String inscricaoEstadual, String enderecoCompleto, String cidade, String estadoUF, String cep, String telefone, String email, String logoUrl, Boolean isAtivo, byte[] certificadoDigital, String certificadoSenhaCriptografada, String certificadoTipo, LocalDateTime certificadoValidade, String certificadoCnpj, Boolean certificadoAtivo, LocalDateTime certificadoUploadDate) {
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.inscricaoEstadual = inscricaoEstadual;
        this.enderecoCompleto = enderecoCompleto;
        this.cidade = cidade;
        this.estadoUF = estadoUF;
        this.cep = cep;
        this.telefone = telefone;
        this.email = email;
        this.logoUrl = logoUrl;
        this.isAtivo = isAtivo;
        this.certificadoDigital = certificadoDigital;
        this.certificadoSenhaCriptografada = certificadoSenhaCriptografada;
        this.certificadoTipo = certificadoTipo;
        this.certificadoValidade = certificadoValidade;
        this.certificadoCnpj = certificadoCnpj;
        this.certificadoAtivo = certificadoAtivo;
        this.certificadoUploadDate = certificadoUploadDate;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Boolean getAtivo() {
        return isAtivo;
    }

    public void setAtivo(Boolean ativo) {
        isAtivo = ativo;
    }

    public byte[] getCertificadoDigital() {
        return certificadoDigital;
    }

    public void setCertificadoDigital(byte[] certificadoDigital) {
        this.certificadoDigital = certificadoDigital;
    }

    public String getCertificadoSenhaCriptografada() {
        return certificadoSenhaCriptografada;
    }

    public void setCertificadoSenhaCriptografada(String certificadoSenhaCriptografada) {
        this.certificadoSenhaCriptografada = certificadoSenhaCriptografada;
    }

    public String getCertificadoTipo() {
        return certificadoTipo;
    }

    public void setCertificadoTipo(String certificadoTipo) {
        this.certificadoTipo = certificadoTipo;
    }

    public LocalDateTime getCertificadoValidade() {
        return certificadoValidade;
    }

    public void setCertificadoValidade(LocalDateTime certificadoValidade) {
        this.certificadoValidade = certificadoValidade;
    }

    public String getCertificadoCnpj() {
        return certificadoCnpj;
    }

    public void setCertificadoCnpj(String certificadoCnpj) {
        this.certificadoCnpj = certificadoCnpj;
    }

    public Boolean getCertificadoAtivo() {
        return certificadoAtivo;
    }

    public void setCertificadoAtivo(Boolean certificadoAtivo) {
        this.certificadoAtivo = certificadoAtivo;
    }

    public LocalDateTime getCertificadoUploadDate() {
        return certificadoUploadDate;
    }

    public void setCertificadoUploadDate(LocalDateTime certificadoUploadDate) {
        this.certificadoUploadDate = certificadoUploadDate;
    }
}
