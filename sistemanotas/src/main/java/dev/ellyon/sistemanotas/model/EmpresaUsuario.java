package dev.ellyon.sistemanotas.model;

import java.time.LocalDateTime;

import dev.ellyon.sistemanotas.model.enums.Perfil;
import jakarta.persistence.*;

@Entity
@Table(name = "empresa_usuario",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_empresa", "id_usuario"}))
public class EmpresaUsuario extends Entidade{
  @ManyToOne
  @JoinColumn(name = "id_empresa", nullable = false)
  private Empresa empresa;

  @ManyToOne
  @JoinColumn(name = "id_usuario", nullable = false)
  private Usuario usuario;

  @Enumerated(EnumType.STRING)
  @Column(name = "perfil")
  private Perfil perfil;

  // Construtor padrao
  protected EmpresaUsuario() {
    super();
  }

  // Construtor com todos os atributos
  public EmpresaUsuario(Long id, Empresa empresa, Usuario usuario, Perfil perfil, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.empresa = empresa;
    this.usuario = usuario;
    this.perfil = perfil;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // Construtor sem id e timestamps
  public EmpresaUsuario(Empresa empresa, Usuario usuario, Perfil perfil) {
    this.empresa = empresa;
    this.usuario = usuario;
    this.perfil = perfil;
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
    return "EmpresaUsuario{" +
            "id=" + id +
            ", empresa=" + (empresa != null ? empresa.getId() : null) +
            ", usuario=" + (usuario != null ? usuario.getId() : null) +
            ", perfil=" + perfil +
            ", createdAt=" + createdAt +
            '}';
  }

}
