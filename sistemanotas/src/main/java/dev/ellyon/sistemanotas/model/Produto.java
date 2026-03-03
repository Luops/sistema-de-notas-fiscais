package dev.ellyon.sistemanotas.model;

import dev.ellyon.sistemanotas.model.enums.Unidade;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_produto")
@Data
public class Produto extends Entidade{
    @Column(name = "codigo", nullable = false, length = 100, unique = true)
    private String codigoProduto;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "descricao", nullable = false, length = 500, columnDefinition = "TEXT")
    private String descricaoProduto;

    @ManyToOne
    @JoinColumn(name = "tipo_produto", nullable = false)
    private TipoProduto tipoProduto;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade", nullable = false)
    private Unidade unidade;

    @Column(name = "preco_venda", nullable = false, precision = 15, scale = 2)
    private BigDecimal precoVenda;

    @Column(name = "ncm", nullable = false, length = 20)
    private String ncm;

    @Column(name = "cfop_padrao", nullable = false, length = 20)
    private String cfopPadrao;

    @Column(name = "aliquota_icms_padrao", nullable = false, precision = 5, scale = 2)
    private BigDecimal aliquotaIcmsPadrao;

    @Column(name = "aliquota_pis_padrao", nullable = false, precision = 5, scale = 2)
    private BigDecimal aliquotaPisPadrao;

    @Column(name = "aliquota_cofins_padrao", nullable = false, precision = 5, scale = 2)
    private BigDecimal aliquotaCofinsPadrao;

    @Column(name = "is_ativo")
    private Boolean isAtivo;

    // Construtor padrão
    public Produto(){
        super();
    }

    // Construtor com todos atributos
    public Produto(Long id, String codigoProduto, String nome, String descricaoProduto, TipoProduto tipoProduto, Unidade unidade, BigDecimal precoVenda, String ncm, String cfopPadrao, BigDecimal aliquotaIcmsPadrao, BigDecimal aliquotaPisPadrao, BigDecimal aliquotaCofinsPadrao, Boolean isAtivo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.codigoProduto = codigoProduto;
        this.nome = nome;
        this.descricaoProduto = descricaoProduto;
        this.tipoProduto = tipoProduto;
        this.unidade = unidade;
        this.precoVenda = precoVenda;
        this.ncm = ncm;
        this.cfopPadrao = cfopPadrao;
        this.aliquotaIcmsPadrao = aliquotaIcmsPadrao;
        this.aliquotaPisPadrao = aliquotaPisPadrao;
        this.aliquotaCofinsPadrao = aliquotaCofinsPadrao;
        this.isAtivo = isAtivo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor sem id e timestamps
    public Produto(String codigoProduto, String nome, String descricaoProduto, TipoProduto tipoProduto, Unidade unidade, BigDecimal precoVenda, String ncm, String cfopPadrao, BigDecimal aliquotaIcmsPadrao, BigDecimal aliquotaPisPadrao, BigDecimal aliquotaCofinsPadrao, Boolean isAtivo) {
        this.codigoProduto = codigoProduto;
        this.nome = nome;
        this.descricaoProduto = descricaoProduto;
        this.tipoProduto = tipoProduto;
        this.unidade = unidade;
        this.precoVenda = precoVenda;
        this.ncm = ncm;
        this.cfopPadrao = cfopPadrao;
        this.aliquotaIcmsPadrao = aliquotaIcmsPadrao;
        this.aliquotaPisPadrao = aliquotaPisPadrao;
        this.aliquotaCofinsPadrao = aliquotaCofinsPadrao;
        this.isAtivo = isAtivo;
    }

    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public TipoProduto getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(TipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }

    public BigDecimal getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(BigDecimal precoVenda) {
        this.precoVenda = precoVenda;
    }

    public String getNcm() {
        return ncm;
    }

    public void setNcm(String ncm) {
        this.ncm = ncm;
    }

    public String getCfopPadrao() {
        return cfopPadrao;
    }

    public void setCfopPadrao(String cfopPadrao) {
        this.cfopPadrao = cfopPadrao;
    }

    public BigDecimal getAliquotaIcmsPadrao() {
        return aliquotaIcmsPadrao;
    }

    public void setAliquotaIcmsPadrao(BigDecimal aliquotaIcmsPadrao) {
        this.aliquotaIcmsPadrao = aliquotaIcmsPadrao;
    }

    public BigDecimal getAliquotaPisPadrao() {
        return aliquotaPisPadrao;
    }

    public void setAliquotaPisPadrao(BigDecimal aliquotaPisPadrao) {
        this.aliquotaPisPadrao = aliquotaPisPadrao;
    }

    public BigDecimal getAliquotaCofinsPadrao() {
        return aliquotaCofinsPadrao;
    }

    public void setAliquotaCofinsPadrao(BigDecimal aliquotaCofinsPadrao) {
        this.aliquotaCofinsPadrao = aliquotaCofinsPadrao;
    }

    public Boolean getAtivo() {
        return isAtivo;
    }

    public void setAtivo(Boolean ativo) {
        isAtivo = ativo;
    }
}
