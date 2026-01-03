package dev.ellyon.sistemanotas.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario extends Entidade {
  @Column(name = "nome", nullable = false, length = 255)
  private String nome;

  @Column(name = "email", nullable = false, unique = true, length = 255)
  private String email;

  @Column(name = "senha", nullable = false, length = 255)
  private String senha;

  @Column(name = "is_ativo", nullable = false)
  private Boolean isAtivo;

  // Construtor padrao
  protected Usuario() {
    super();
  }

  // Construtor com todos os atributos
  public Usuario(Long id, String nome, String email, String senha, Boolean isAtivo, LocalDateTime createdAt,
                 LocalDateTime updatedAt) {
    this.id = id;
    this.nome = nome;
    this.email = email;
    this.senha = senha;
    this.isAtivo = isAtivo;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // Construtor sem id e timestamps
  public Usuario(String nome, String email, String senha, Boolean isAtivo) {
    this.nome = nome;
    this.email = email;
    this.senha = senha;
    this.isAtivo = isAtivo;
  }

  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getSenha() {
    return this.senha;
  }

  public void setSenha(String senha) {
    this.senha = senha;
  }

  public Boolean getIsAtivo() {
    return this.isAtivo;
  }

  public void setIsAtivo(Boolean isAtivo) {
    this.isAtivo = isAtivo;
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
