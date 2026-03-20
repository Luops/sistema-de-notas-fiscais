package dev.ellyon.sistemanotas.dto.produto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoSimpleDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProdutoResponseDTO {
    private Long id;
    private String codigoProduto;
    private String nome;
    private String descricao;
    private TipoProdutoSimpleDTO tipoProduto;
    private String unidade;
    private BigDecimal precoVenda;
    private String ncm;
    private String cfopPadrao;
    private BigDecimal aliquotaIcmsPadrao;
    private BigDecimal aliquotaPisPadrao;
    private BigDecimal aliquotaCofinsPadrao;
    private Boolean isAtivo;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

    // Construtor padrao
    public ProdutoResponseDTO() {
    }

    // Construtor completo
    public ProdutoResponseDTO(Long id, String codigoProduto, String nome, String descricao, TipoProdutoSimpleDTO tipoProduto, String unidade, BigDecimal precoVenda, String ncm, String cfopPadrao, BigDecimal aliquotaIcmsPadrao, BigDecimal aliquotaPisPadrao, BigDecimal aliquotaCofinsPadrao, Boolean isAtivo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
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
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public TipoProdutoSimpleDTO getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(TipoProdutoSimpleDTO tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
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
