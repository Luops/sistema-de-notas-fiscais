package dev.ellyon.sistemanotas.model;

import java.time.Instant;

import dev.ellyon.sistemanotas.model.enums.Perfil;

public class EmpresaUsuario extends Entidade{
  private Long empresaId;
  private Long usuarioId;
  private Perfil perfil;

  public EmpresaUsuario(Long id, Long empresaId, Long usuarioId, Perfil perfil, Instant createdAt, Instant updatedAt) {
    this.id = id;
    this.empresaId = empresaId;
    this.usuarioId = usuarioId;
    this.perfil = perfil;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Long getEmpresaId() {
    return this.empresaId;
  }

  public void setEmpresaId(Long empresaId) {
    this.empresaId = empresaId;
  }

  public Long getUsuarioId() {
    return this.usuarioId;
  }

  public void setUsuarioId(Long usuarioId) {
    this.usuarioId = usuarioId;
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
      " empresaId='" + getEmpresaId() + "'" +
      ", usuarioId='" + getUsuarioId() + "'" +
      ", perfil='" + getPerfil() + "'" +
      "}";
  }

}
