package dev.ellyon.sistemanotas.model;

import dev.ellyon.sistemanotas.model.enums.Unidade;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "item_nota")
public class ItemNota extends Entidade{
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @Column(name = "id_nota", nullable = false)
    private Nota notaId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @Column(name = "id_produto", nullable = false)
    private Produto produtoId;

    @Column(name = "codigo_produto", nullable = false, length = 100)
    private String codigoProduto;

    @Column(name = "descricao_produto", nullable = false, length = 500)
    private String descricaoProduto;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade", nullable = false, length = 50)
    private Unidade unidade;

    @Column(name = "preco_unitario", nullable = false, precision = 15, scale = 2)
    private BigDecimal precoUnitario;

    @Column(name = "sub_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "ncm", nullable = false, length = 20)
    private String ncm;

    @Column(name = "cfop", nullable = false, length = 20)
    private String cfop;

    @Column(name = "aliquota_icms", nullable = false, precision = 5, scale = 2)
    private BigDecimal aliquotaIcms;

    @Column(name = "valor_icms", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorIcms;

    @Column(name = "aliquota_pis", nullable = false, precision = 5, scale = 2)
    private BigDecimal aliquotaPis;

    @Column(name = "valor_pis", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorPis;

    @Column(name = "aliquota_cofins", nullable = false, precision = 5, scale = 2)
    private BigDecimal aliquotaCofins;

    @Column(name = "valor_cofins", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorCofins;

    @Column(name = "valor_total_item", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalItem;

    // Construtor padrao
    protected ItemNota(){
        super();
    }

    // Construtor com todos os atributos
    public ItemNota(Long id, Nota notaId, Produto produtoId, String codigoProduto, String descricaoProduto, Integer quantidade,
                    Unidade unidade, BigDecimal precoUnitario, BigDecimal subTotal, String ncm, String cfop,
                    BigDecimal aliquotaIcms, BigDecimal valorIcms, BigDecimal aliquotaPis, BigDecimal valorPis,
                    BigDecimal aliquotaCofins, BigDecimal valorCofins, BigDecimal valorTotalItem, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.notaId = notaId;
        this.produtoId = produtoId;
        this.codigoProduto = codigoProduto;
        this.descricaoProduto = descricaoProduto;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.precoUnitario = precoUnitario;
        this.subTotal = subTotal;
        this.ncm = ncm;
        this.cfop = cfop;
        this.aliquotaIcms = aliquotaIcms;
        this.valorIcms = valorIcms;
        this.aliquotaPis = aliquotaPis;
        this.valorPis = valorPis;
        this.aliquotaCofins = aliquotaCofins;
        this.valorCofins = valorCofins;
        this.valorTotalItem = valorTotalItem;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;}

    // Construtor sem id e timestamps
    public ItemNota(Nota notaId, Produto produtoId, String codigoProduto, String descricaoProduto, Integer quantidade,
                    Unidade unidade, BigDecimal precoUnitario, BigDecimal subTotal, String ncm, String cfop,
                    BigDecimal aliquotaIcms, BigDecimal valorIcms, BigDecimal aliquotaPis, BigDecimal valorPis,
                    BigDecimal aliquotaCofins, BigDecimal valorCofins, BigDecimal valorTotalItem) {
        this.notaId = notaId;
        this.produtoId = produtoId;
        this.codigoProduto = codigoProduto;
        this.descricaoProduto = descricaoProduto;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.precoUnitario = precoUnitario;
        this.subTotal = subTotal;
        this.ncm = ncm;
        this.cfop = cfop;
        this.aliquotaIcms = aliquotaIcms;
        this.valorIcms = valorIcms;
        this.aliquotaPis = aliquotaPis;
        this.valorPis = valorPis;
        this.aliquotaCofins = aliquotaCofins;
        this.valorCofins = valorCofins;
        this.valorTotalItem = valorTotalItem;
    }

    public Nota getNotaId() {
        return notaId;
    }

    public void setNotaId(Nota notaId) {
        this.notaId = notaId;
    }

    public Produto getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Produto produtoId) {
        this.produtoId = produtoId;
    }

    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public String getNcm() {
        return ncm;
    }

    public void setNcm(String ncm) {
        this.ncm = ncm;
    }

    public String getCfop() {
        return cfop;
    }

    public void setCfop(String cfop) {
        this.cfop = cfop;
    }

    public BigDecimal getAliquotaIcms() {
        return aliquotaIcms;
    }

    public void setAliquotaIcms(BigDecimal aliquotaIcms) {
        this.aliquotaIcms = aliquotaIcms;
    }

    public BigDecimal getValorIcms() {
        return valorIcms;
    }

    public void setValorIcms(BigDecimal valorIcms) {
        this.valorIcms = valorIcms;
    }

    public BigDecimal getAliquotaPis() {
        return aliquotaPis;
    }

    public void setAliquotaPis(BigDecimal aliquotaPis) {
        this.aliquotaPis = aliquotaPis;
    }

    public BigDecimal getValorPis() {
        return valorPis;
    }

    public void setValorPis(BigDecimal valorPis) {
        this.valorPis = valorPis;
    }

    public BigDecimal getAliquotaCofins() {
        return aliquotaCofins;
    }

    public void setAliquotaCofins(BigDecimal aliquotaCofins) {
        this.aliquotaCofins = aliquotaCofins;
    }

    public BigDecimal getValorCofins() {
        return valorCofins;
    }

    public void setValorCofins(BigDecimal valorCofins) {
        this.valorCofins = valorCofins;
    }

    public BigDecimal getValorTotalItem() {
        return valorTotalItem;
    }

    public void setValorTotalItem(BigDecimal valorTotalItem) {
        this.valorTotalItem = valorTotalItem;
    }

    @Override
    public String toString() {
        return "ItemNota{" +
                "notaId=" + notaId +
                ", produtoId=" + produtoId +
                ", codigoProduto='" + codigoProduto + '\'' +
                ", descricaoProduto='" + descricaoProduto + '\'' +
                ", quantidade=" + quantidade +
                ", unidade=" + unidade +
                ", precoUnitario=" + precoUnitario +
                ", subTotal=" + subTotal +
                ", ncm='" + ncm + '\'' +
                ", cfop='" + cfop + '\'' +
                ", aliquotaIcms=" + aliquotaIcms +
                ", valorIcms=" + valorIcms +
                ", aliquotaPis=" + aliquotaPis +
                ", valorPis=" + valorPis +
                ", aliquotaCofins=" + aliquotaCofins +
                ", valorCofins=" + valorCofins +
                ", valorTotalItem=" + valorTotalItem +
                ", updatedAt=" + updatedAt +
                ", createdAt=" + createdAt +
                ", id=" + id +
                '}';
    }
}
