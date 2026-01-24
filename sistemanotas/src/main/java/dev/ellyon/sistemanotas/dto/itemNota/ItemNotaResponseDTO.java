package dev.ellyon.sistemanotas.dto.itemNota;

import dev.ellyon.sistemanotas.dto.produto.ProdutoSimpleResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ItemNotaResponseDTO {
    private Long id;
    private Long produtoId;
    private String codigoProduto;
    private String descricaoProduto;
    private BigDecimal quantidade;
    private String unidade;
    private BigDecimal precoUnitario;
    private BigDecimal subTotal;
    private String ncm;
    private String cfop;
    private BigDecimal aliquotaIcms;
    private BigDecimal valorIcms;
    private BigDecimal aliquotaPis;
    private BigDecimal valorPis;
    private BigDecimal aliquotaCofins;
    private BigDecimal valorCofins;
    private BigDecimal valorTotalItem;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // construtor padrao
    public ItemNotaResponseDTO() {

    }

    // construtor completo
    public ItemNotaResponseDTO(Long id, Long produtoId, String codigoProduto, String descricaoProduto, BigDecimal quantidade, String unidade, BigDecimal precoUnitario, BigDecimal subTotal, String ncm, String cfop, BigDecimal aliquotaIcms, BigDecimal valorIcms, BigDecimal aliquotaPis, BigDecimal valorPis, BigDecimal aliquotaCofins, BigDecimal valorCofins, BigDecimal valorTotalItem, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
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
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
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

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
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
}
