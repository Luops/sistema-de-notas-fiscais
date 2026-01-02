package dev.ellyon.sistemanotas.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table (name = "empresa")

public final class Empresa extends Entidade{
  private String razaoSocial;
  private String nomefantasia;
  private String cnpj;
  private String inscricaoEstadual;
  private String enderecoCompleto;
  private String cidade;
  private String estado;
  private String cep;
  private String telefone;
  private String email;
  private String logoUrl;
  private Boolean isAtivo;

  public Empresa(Long id, String razaoSocial, String nomefantasia, String cnpj, String inscricaoEstadual, String enderecoCompleto, String cidade, String estado, String cep, String telefone, String email, String logoUrl, Boolean isAtivo, Instant createdAt, Instant updatedAt) {
    this.id = id;
    this.razaoSocial = razaoSocial;
    this.nomefantasia = nomefantasia;
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

  public String getRazaoSocial() {
    return this.razaoSocial;
  }

  public void setRazaoSocial(String razaoSocial) {
    this.razaoSocial = razaoSocial;
  }

  public String getNomefantasia() {
    return this.nomefantasia;
  }

  public void setNomefantasia(String nomefantasia) {
    this.nomefantasia = nomefantasia;
  }

  public String getCnpj() {
    return this.cnpj;
  }

  public void setCnpj(String cnpj) {
    this.cnpj = cnpj;
  }

  public String getInscricaoEstadual() {
    return this.inscricaoEstadual;
  }

  public void setInscricaoEstadual(String inscricaoEstadual) {
    this.inscricaoEstadual = inscricaoEstadual;
  }

  public String getEnderecoCompleto() {
    return this.enderecoCompleto;
  }

  public void setEnderecoCompleto(String enderecoCompleto) {
    this.enderecoCompleto = enderecoCompleto;
  }

  public String getCidade() {
    return this.cidade;
  }

  public void setCidade(String cidade) {
    this.cidade = cidade;
  }

  public String getEstado() {
    return this.estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  public String getCep() {
    return this.cep;
  }

  public void setCep(String cep) {
    this.cep = cep;
  }

  public String getTelefone() {
    return this.telefone;
  }

  public void setTelefone(String telefone) {
    this.telefone = telefone;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getLogoUrl() {
    return this.logoUrl;
  }

  public void setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }

  public Boolean isIsAtivo() {
    return this.isAtivo;
  }

  public Boolean getIsAtivo() {
    return this.isAtivo;
  }

  public void setIsAtivo(Boolean isAtivo) {
    this.isAtivo = isAtivo;
  }

  @Override
  public String toString() {
    return "{" +
      " razaoSocial='" + getRazaoSocial() + "'" +
      ", nomefantasia='" + getNomefantasia() + "'" +
      ", cnpj='" + getCnpj() + "'" +
      ", inscricaoEstadual='" + getInscricaoEstadual() + "'" +
      ", enderecoCompleto='" + getEnderecoCompleto() + "'" +
      ", cidade='" + getCidade() + "'" +
      ", estado='" + getEstado() + "'" +
      ", cep='" + getCep() + "'" +
      ", telefone='" + getTelefone() + "'" +
      ", email='" + getEmail() + "'" +
      ", logoUrl='" + getLogoUrl() + "'" +
      ", isAtivo='" + isIsAtivo() + "'" +
      "}";
  }
}
