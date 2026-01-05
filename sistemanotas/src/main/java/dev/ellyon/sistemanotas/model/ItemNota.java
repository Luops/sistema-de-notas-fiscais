package dev.ellyon.sistemanotas.model;

import dev.ellyon.sistemanotas.model.enums.Unidade;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_item_nota")
public class ItemNota extends Entidade{
    @ManyToOne
    @JoinColumn(name = "id_nota", nullable = false)
    private Nota nota;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "codigo_produto", nullable = false, length = 100)
    private String codigoProduto;

    @Column(name = "descricao_produto", nullable = false, length = 500)
    private String descricaoProduto;

    @Column(name = "quantidade", nullable = false)
    private BigDecimal quantidade;

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

    // Construtor para criar item a partir de um Produto
    public ItemNota(Nota nota, Produto produto, BigDecimal quantidade) {
        this.nota = nota;
        this.produto = produto;
        this.quantidade = quantidade;

        // ========================================
        // SNAPSHOT - Copia dados do produto
        // ========================================
        this.codigoProduto = produto.getCodigoProduto();
        this.descricaoProduto = produto.getNome();
        this.unidade = produto.getUnidade();
        this.precoUnitario = produto.getPrecoVenda();
        this.ncm = produto.getNcm();
        this.cfop = produto.getCfopPadrao();

        // Copia alíquotas padrão
        this.aliquotaIcms = produto.getAliquotaIcmsPadrao();
        this.aliquotaPis = produto.getAliquotaPisPadrao();
        this.aliquotaCofins = produto.getAliquotaCofinsPadrao();

        // Calcula valores
        calcularValores();
    }

    // ========================================
    // MÉTODO DE CÁLCULO
    // ========================================

    public void calcularValores() {
        // Subtotal = quantidade × preço
        this.subTotal = this.quantidade.multiply(this.precoUnitario);

        // ICMS = subtotal × (alíquota / 100)
        this.valorIcms = this.subTotal.multiply(this.aliquotaIcms)
                .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);

        // PIS = subtotal × (alíquota / 100)
        this.valorPis = this.subTotal.multiply(this.aliquotaPis)
                .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);

        // COFINS = subtotal × (alíquota / 100)
        this.valorCofins = this.subTotal.multiply(this.aliquotaCofins)
                .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);

        // Total do item = subtotal + impostos
        this.valorTotalItem = this.subTotal
                .add(this.valorIcms)
                .add(this.valorPis)
                .add(this.valorCofins);
    }

    public Nota getNota() {
        return nota;
    }

    public void setNota(Nota nota) {
        this.nota = nota;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
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
        calcularValores();  // Recalcula quando quantidade muda
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
        calcularValores();  // Recalcula quando preço muda
    }

    public BigDecimal getSubtotal() {
        return subTotal;
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
        calcularValores();  // Recalcula quando alíquota muda
    }

    public BigDecimal getValorIcms() {
        return valorIcms;
    }

    public BigDecimal getAliquotaPis() {
        return aliquotaPis;
    }

    public void setAliquotaPis(BigDecimal aliquotaPis) {
        this.aliquotaPis = aliquotaPis;
        calcularValores();
    }

    public BigDecimal getValorPis() {
        return valorPis;
    }

    public BigDecimal getAliquotaCofins() {
        return aliquotaCofins;
    }

    public void setAliquotaCofins(BigDecimal aliquotaCofins) {
        this.aliquotaCofins = aliquotaCofins;
        calcularValores();
    }

    public BigDecimal getValorCofins() {
        return valorCofins;
    }

    public BigDecimal getValorTotalItem() {
        return valorTotalItem;
    }

    @Override
    public String toString() {
        return "ItemNota{" +
                "id=" + id +
                ", codigoProduto='" + codigoProduto + '\'' +
                ", descricaoProduto='" + descricaoProduto + '\'' +
                ", quantidade=" + quantidade +
                ", precoUnitario=" + precoUnitario +
                ", subtotal=" + subTotal +
                ", valorTotalItem=" + valorTotalItem +
                '}';
    }
}
