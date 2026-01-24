package dev.ellyon.sistemanotas.dto.itemNota;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ItemNotaRequestDTO {
    @NotNull(message = "ID do produto é obrigatório")
    private Long produtoId;

    @NotBlank(message = "Quantidade é obrigatória")
    private Integer quantidade;

    private BigDecimal precoUnitario;
    private BigDecimal aliquotaIcms;
    private BigDecimal aliquotaPis;
    private BigDecimal aliquotaCofins;

    public ItemNotaRequestDTO(Long produtoId, Integer quantidade, BigDecimal precoUnitario, BigDecimal aliquotaIcms, BigDecimal aliquotaPis, BigDecimal aliquotaCofins) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.aliquotaIcms = aliquotaIcms;
        this.aliquotaPis = aliquotaPis;
        this.aliquotaCofins = aliquotaCofins;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getAliquotaIcms() {
        return aliquotaIcms;
    }

    public void setAliquotaIcms(BigDecimal aliquotaIcms) {
        this.aliquotaIcms = aliquotaIcms;
    }

    public BigDecimal getAliquotaPis() {
        return aliquotaPis;
    }

    public void setAliquotaPis(BigDecimal aliquotaPis) {
        this.aliquotaPis = aliquotaPis;
    }

    public BigDecimal getAliquotaCofins() {
        return aliquotaCofins;
    }

    public void setAliquotaCofins(BigDecimal aliquotaCofins) {
        this.aliquotaCofins = aliquotaCofins;
    }
}
