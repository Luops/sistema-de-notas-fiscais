package dev.ellyon.sistemanotas.model;

import java.time.Instant;

import dev.ellyon.sistemanotas.model.enums.Perfil;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table
public class EmpresaUsuario extends Entidade{

  @OneToOne
  @JoinColumn(name = "empresa_id")
  private Empresa empresa;

  @JoinColumn(name = "usuario_id")
  private Usuario usuario;
  private Perfil perfil;

  public EmpresaUsuario(Long id, Empresa empresa, Usuario usuario, Perfil perfil, Instant createdAt, Instant updatedAt) {
    this.id = id;
    this.empresa = empresa;
    this.usuario = usuario;
    this.perfil = perfil;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Empresa getEmpresa() {
    return this.empresa;
  }

  public void setEmpresa(Empresa empresa) {
    this.empresa = empresa;
  }

  public Usuario getUsuario() {
    return this.usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public Perfil getPerfil() {
    return this.perfil;
  }

  public void setPerfil(Perfil perfil) {
    this.perfil = perfil;
  }

  @Override
  public String toString() {
    return "{" +
      " empresa='" + getEmpresa() + "'" +
      ", usuario='" + getUsuario() + "'" +
      ", perfil='" + getPerfil() + "'" +
      "}";
  }

}
