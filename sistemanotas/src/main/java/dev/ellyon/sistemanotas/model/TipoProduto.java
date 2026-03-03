package dev.ellyon.sistemanotas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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

    // Construtor padrão
    public TipoProduto(){
        super();
    }

    // Construtor com todos atributos
    public TipoProduto(Long id, String nome, Boolean isAtivo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nome = nome;
        this.isAtivo = isAtivo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor sem id e timestamps
    public TipoProduto(String nome, Boolean isAtivo) {
        this.nome = nome;
        this.isAtivo = isAtivo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAtivo() {
        return isAtivo;
    }

    public void setAtivo(Boolean ativo) {
        isAtivo = ativo;
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
