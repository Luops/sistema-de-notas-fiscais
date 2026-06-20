package dev.ellyon.sistemanotas.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_tipo_produto")
@Data
public class TipoProduto extends Entidade{
    @Column(name = "nome", nullable = false, length = 200, unique = true)
    private String nome;

    @Column(name = "is_ativo", nullable = false)
    private Boolean isAtivo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    // Construtor padrão
    public TipoProduto(){
        super();
    }

    // Construtor com todos atributos
    public TipoProduto(Long id, String nome, Boolean isAtivo, LocalDateTime createdAt, LocalDateTime updatedAt, Empresa empresa) {
        this.id = id;
        this.nome = nome;
        this.isAtivo = isAtivo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.empresa = empresa;
    }

    // Construtor sem id e timestamps
    public TipoProduto(String nome, Boolean isAtivo, Empresa empresa) {
        this.nome = nome;
        this.isAtivo = isAtivo;
        this.empresa = empresa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getIsAtivo() {
        return isAtivo;
    }

    public void setIsAtivo(Boolean ativo) {
        isAtivo = ativo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    @Override
    public String toString() {
        return "TipoProduto{" +
                "nome='" + nome + '\'' +
                ", isAtivo=" + isAtivo +
                ", updatedAt=" + updatedAt +
                ", createdAt=" + createdAt +
                ", id=" + id +
                '}';
    }
}
