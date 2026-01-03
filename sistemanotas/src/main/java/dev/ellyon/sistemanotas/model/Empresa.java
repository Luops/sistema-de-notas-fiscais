package dev.ellyon.sistemanotas.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table (name = "empresa")
public class Empresa extends Entidade{
  @Column(name = "razao_social", nullable = false, length = 255)
  private String razaoSocial;

  @Column(name = "nome_fantasia", nullable = false, length = 255)
  private String nomeFantasia;

  @Column(name = "cnpj", nullable = false, length = 20, unique = true)
  private String cnpj;

  @Column(name = "inscricao_estadual", nullable = false, length = 20)
  private String inscricaoEstadual;

  @Column(name = "endereco_completo", nullable = false, length = 500)
  private String enderecoCompleto;

  @Column(name = "cidade", nullable = false, length = 100)
  private String cidade;

  @Column(name = "estado", nullable = false, length = 100)
  private String estado;

  @Column(name = "cep", nullable = false, length = 20)
  private String cep;

  @Column(name = "telefone", nullable = false, length = 20)
  private String telefone;

  @Column(name = "email", nullable = false, length = 255, unique = true)
  private String email;

  @Column(name = "logo_url", length = 500)
  private String logoUrl;

  @Column(name = "is_ativo", nullable = false)
  private Boolean isAtivo;

  // Construtor padrão
  protected Empresa(){
    super();
  }

  // Construtor com todos os atributos
  public Empresa(Long id, String razaoSocial, String nomeFantasia, String cnpj, String inscricaoEstadual, String enderecoCompleto, String cidade, String estado, String cep, String telefone, String email, String logoUrl, Boolean isAtivo, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.razaoSocial = razaoSocial;
    this.nomeFantasia = nomeFantasia;
    this.cnpj = cnpj;
    this.inscricaoEstadual = inscricaoEstadual;
    this.enderecoCompleto = enderecoCompleto;
    this.cidade = cidade;
    this.estado = estado;
    this.cep = cep;
    this.telefone = telefone;
    this.email = email;
    this.logoUrl = logoUrl;
    this.isAtivo = isAtivo;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // Constructor sem Id e timestamps
  public Empresa(String razaoSocial, String nomeFantasia, String cnpj, String inscricaoEstadual, String enderecoCompleto, String cidade, String estado, String cep, String telefone, String email, String logoUrl, Boolean isAtivo) {
    this.razaoSocial = razaoSocial;
    this.nomeFantasia = nomeFantasia;
    this.cnpj = cnpj;
    this.inscricaoEstadual = inscricaoEstadual;
    this.enderecoCompleto = enderecoCompleto;
    this.cidade = cidade;
    this.estado = estado;
    this.cep = cep;
    this.telefone = telefone;
    this.email = email;
    this.logoUrl = logoUrl;
    this.isAtivo = isAtivo;
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

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
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

  @Override
  public String toString() {
    return "Empresa{" +
            "razaoSocial='" + razaoSocial + '\'' +
            ", nomeFantasia='" + nomeFantasia + '\'' +
            ", cnpj='" + cnpj + '\'' +
            ", inscricaoEstadual='" + inscricaoEstadual + '\'' +
            ", enderecoCompleto='" + enderecoCompleto + '\'' +
            ", cidade='" + cidade + '\'' +
            ", estado='" + estado + '\'' +
            ", cep='" + cep + '\'' +
            ", telefone='" + telefone + '\'' +
            ", email='" + email + '\'' +
            ", logoUrl='" + logoUrl + '\'' +
            ", isAtivo=" + isAtivo +
            ", updatedAt=" + updatedAt +
            ", createdAt=" + createdAt +
            ", id=" + id +
            '}';
  }
}
