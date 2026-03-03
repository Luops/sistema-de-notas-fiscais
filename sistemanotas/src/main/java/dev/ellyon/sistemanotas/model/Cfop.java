package dev.ellyon.sistemanotas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_cfop")
@Data
public class Cfop extends Entidade {
    @Column(name = "codigo", nullable = false, length = 4, unique = true)
    private String codigo;

    @Column(name = "descricao", nullable = false, length = 500)
    private String descricao;

    @Column(name = "aplicacao", columnDefinition = "TEXT")
    private String aplicacao;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo; // ENTRADA, SAIDA

    @Column(name = "natureza", nullable = false, length = 50)
    private String natureza; // VENDA, COMPRA, TRANSFERENCIA, DEVOLUCAO, etc

    @Column(name = "operacao", nullable = false, length = 50)
    private String operacao; // DENTRO_ESTADO, FORA_ESTADO, IMPORTACAO, EXPORTACAO

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    // Construtor padrão
    public Cfop() {
        super();
    }

    // Construtor com todos atributos
    public Cfop(Long id, String codigo, String descricao, String aplicacao, String tipo, String natureza, String operacao, Boolean ativo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.aplicacao = aplicacao;
        this.tipo = tipo;
        this.natureza = natureza;
        this.operacao = operacao;
        this.ativo = ativo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor sem id e timestamps
    public Cfop(String codigo, String descricao, String aplicacao, String tipo, String natureza, String operacao, Boolean ativo) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.aplicacao = aplicacao;
        this.tipo = tipo;
        this.natureza = natureza;
        this.operacao = operacao;
        this.ativo = ativo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getAplicacao() {
        return aplicacao;
    }

    public void setAplicacao(String aplicacao) {
        this.aplicacao = aplicacao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNatureza() {
        return natureza;
    }

    public void setNatureza(String natureza) {
        this.natureza = natureza;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Cfop{" +
                "codigo='" + codigo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", tipo='" + tipo + '\'' +
                ", natureza='" + natureza + '\'' +
                ", operacao='" + operacao + '\'' +
                ", ativo=" + ativo +
                ", id=" + id +
                '}';
    }
}
