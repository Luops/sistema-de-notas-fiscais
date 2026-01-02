package dev.ellyon.sistemanotas.model;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class Entidade implements Serializable {
  protected Long id;
  protected Instant createdAt;
  protected Instant updatedAt;


  protected Long getId() {
    return this.id;
  }

  protected void setId(Long id) {
    this.id = id;
  }

  protected Instant getCreatedAt() {
    return this.createdAt;
  }

  protected void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  protected Instant getUpdatedAt() {
    return this.updatedAt;
  }

  protected void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

}
