package dev.ellyon.sistemanotas.dto.produto;

import dev.ellyon.sistemanotas.model.enums.Unidade;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class ProdutoRequestDTO {
    @NotBlank
    private String codigoProduto;

    @NotBlank
    private String nome;

    private String descricao;

    @NotNull
    private Long tipoProduto;

    @NotNull
    private Unidade unidade;

    @NotNull
    @Positive
    private BigDecimal precoVenda;

    @NotBlank
    private String ncm;

    @NotBlank
    private String cfopPadrao;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal aliquotaIcmsPadrao;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal aliquotaPisPadrao;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal aliquotaCofinsPadrao;

    private Boolean isAtivo;

    public ProdutoRequestDTO(String codigoProduto, String nome, String descricao, Long tipoProduto, Unidade unidade, BigDecimal precoVenda, String ncm, String cfopPadrao, BigDecimal aliquotaIcmsPadrao, BigDecimal aliquotaPisPadrao, BigDecimal aliquotaCofinsPadrao, Boolean isAtivo) {
        this.codigoProduto = codigoProduto;
        this.nome = nome;
        this.descricao = descricao;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(Long tipoProduto) {
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
