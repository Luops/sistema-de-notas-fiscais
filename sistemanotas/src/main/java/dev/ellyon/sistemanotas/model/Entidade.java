package dev.ellyon.sistemanotas.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@SuperBuilder
@MappedSuperclass
public abstract class Entidade implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  protected LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  protected LocalDateTime updatedAt;

  // Construtor padrão
  public Entidade() {}

  protected Long getId() {
    return this.id;
  }

  protected void setId(Long id) {
    this.id = id;
  }

  protected LocalDateTime getCreatedAt() {
    return this.createdAt;
  }

  protected void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  protected LocalDateTime getUpdatedAt() {
    return this.updatedAt;
  }

  protected void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

}
