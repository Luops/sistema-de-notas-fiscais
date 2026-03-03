package dev.ellyon.sistemanotas.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table (name = "tb_empresa")
@Data
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
  private String estadoUF;

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

  @Column(name = "certificado_digital")
  private byte[] certificadoDigital;

  @Column(name = "certificado_senha_criptografada", length = 255)
  private String certificadoSenhaCriptografada;  // Senha criptografada

  @Column(name = "certificado_tipo", length = 10)
  private String certificadoTipo = "A1";  // A1 ou A3

  @Column(name = "certificado_validade")
  private LocalDateTime certificadoValidade;  // Data de vencimento

  @Column(name = "certificado_cnpj", length = 14)
  private String certificadoCnpj;  // CNPJ do certificado

  @Column(name = "certificado_ativo")
  private Boolean certificadoAtivo = false;  // Se está configurado

  @Column(name = "certificado_upload_date")
  private LocalDateTime certificadoUploadDate;  // Data do upload

  // Construtor padrão
  public Empresa(){
    super();
  }

  // Construtor com todos os atributos
  public Empresa(Long id, String razaoSocial, String nomeFantasia, String cnpj, String inscricaoEstadual, String enderecoCompleto, String cidade, String estadoUF, String cep, String telefone, String email, String logoUrl, Boolean isAtivo, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
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
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // Constructor sem Id e timestamps
  public Empresa(String razaoSocial, String nomeFantasia, String cnpj, String inscricaoEstadual, String enderecoCompleto, String cidade, String estadoUF, String cep, String telefone, String email, String logoUrl, Boolean isAtivo) {
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

  /**
   * Verifica se certificado está válido
   */
  @Transient
  public boolean isCertificadoValido() {
    if (certificadoDigital == null || certificadoValidade == null) {
      return false;
    }
    return LocalDateTime.now().isBefore(certificadoValidade) || LocalDateTime.now().isEqual(certificadoValidade);
  }

  /**
   * Dias restantes até vencer
   */
  @Transient
  public long diasParaVencer() {
    if (certificadoValidade == null) {
      return 0;
    }
    return java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), certificadoValidade);
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
            ", estado='" + estadoUF + '\'' +
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
