package dev.ellyon.sistemanotas.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table

public final class Usuario extends Entidade {
  private String nome;
  private String email;
  private String senha;
  private Boolean isAtivo;

  public Usuario(Long id, String nome, String email, String senha, Boolean isAtivo, Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.nome = nome;
    this.email = email;
    this.senha = senha;
    this.isAtivo = isAtivo;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
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
      " nome='" + getNome() + "'" +
      ", email='" + getEmail() + "'" +
      ", senha='" + getSenha() + "'" +
      ", isAtivo='" + isIsAtivo() + "'" +
      "}";
  }
  

}
