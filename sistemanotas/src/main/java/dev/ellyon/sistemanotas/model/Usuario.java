package dev.ellyon.sistemanotas.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_usuario")
@Data
public class Usuario extends Entidade {
  @Column(name = "nome", nullable = false, length = 255)
  private String nome;

  @Column(name = "email", nullable = false, unique = true, length = 255)
  private String email;

  @Column(name = "senha", nullable = false, length = 255)
  private String senha;

  @Column(name = "telefone", nullable = false, length = 20)
  private String telefone;

  @Column(name="cidade", nullable = false, length = 100)
  private String cidade;

  @Column(name = "endereco", nullable = false, length = 255)
  private String endereco;

  @Column(name = "cep", nullable = false, length = 20)
  private  String cep;

  @Column(name = "numero_endereco", nullable = false, length = 20)
  private  String numeroEndereco;

  @Column(name = "is_ativo", nullable = false)
  private Boolean isAtivo;

  // Construtor padrao
  public Usuario() {
    super();
  }

  // Construtor com todos os atributos
  public Usuario(Long id, String nome, String email, String senha, String telefone, String cidade, String endereco, String cep, String numeroEndereco, Boolean isAtivo, LocalDateTime createdAt,
                 LocalDateTime updatedAt) {
    this.id = id;
    this.nome = nome;
    this.email = email;
    this.senha = senha;
    this.telefone = telefone;
    this.cidade = cidade;
    this.endereco = endereco;
    this.cep = cep;
    this.numeroEndereco = numeroEndereco;
    this.isAtivo = isAtivo;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // Construtor sem id e timestamps
  public Usuario(String nome, String email, String senha, String telefone, String cidade, String endereco, String cep, String numeroEndereco, Boolean isAtivo) {
    this.nome = nome;
    this.email = email;
    this.senha = senha;
    this.telefone = telefone;
    this.cidade = cidade;
    this.endereco = endereco;
    this.cep = cep;
    this.numeroEndereco = numeroEndereco;
    this.isAtivo = isAtivo;
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

  public Boolean getIsAtivo() {
    return isAtivo;
  }

  public void setIsAtivo(Boolean ativo) {
    isAtivo = ativo;
  }

  @Override
  public String toString() {
    return "Usuario{" +
            "id=" + id +
            ", nome='" + nome + '\'' +
            ", email='" + email + '\'' +
            ", senha='[PROTECTED]'" +
            ", isAtivo=" + isAtivo +
            ", createdAt=" + createdAt +
            '}';
  }

}
