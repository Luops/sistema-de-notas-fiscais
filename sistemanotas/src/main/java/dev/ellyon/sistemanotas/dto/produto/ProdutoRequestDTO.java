package dev.ellyon.sistemanotas.dto.produto;

import dev.ellyon.sistemanotas.model.enums.Unidade;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ProdutoRequestDTO {
    @NotBlank(message = "Código do produto é obrigatório")
    @Size(min = 10, max = 18, message = "Código do produto deve ter entre 10 e 18 caracteres")
    private String codigoProduto;

    @NotBlank(message = "Nome do produto é obrigatório")
    @Size(min = 3, max = 255, message = "Nome do produto deve ter entre 3 e 255 caracteres")
    private String nome;

    @NotBlank
    @Size(min = 10, max = 255, message = "Descrição deve ter entre 10 e 255 caracteres")
    private String descricao;

    @NotNull(message = "Tipo de produto é obrigatório")
    private Long tipoProduto;

    @NotNull(message = "Unidade é obrigatória")
    private Unidade unidade;

    @NotNull(message = "Preço de venda é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço de venda deve ser maior que zero")
    private BigDecimal precoVenda;

    @Pattern(regexp = "\\d{8}", message = "NCM deve ter exatamente 8 dígitos")
    private String ncm;

    @Pattern(regexp = "\\d{4}", message = "CFOP deve ter exatamente 4 dígitos")
    private String cfopPadrao;

    @NotNull(message = "Alíquota de ICMS é obrigatória")
    @DecimalMin(value = "0.1", inclusive = true, message = "Alíquota de ICMS deve ser maior que zero")
    @DecimalMax(value = "100.0", inclusive = true, message = "Alíquota de ICMS deve ser menor ou igual a 100")
    private BigDecimal aliquotaIcmsPadrao;

    @NotNull(message = "Alíquota de PIS é obrigatória")
    @DecimalMin(value = "0.1", inclusive = true, message = "Alíquota de PIS deve ser maior que zero")
    @DecimalMax(value = "100.0", inclusive = true, message = "Alíquota de PIS deve ser menor ou igual a 100")
    private BigDecimal aliquotaPisPadrao;

    @NotNull(message = "Alíquota de COFINS é obrigatória")
    @DecimalMin(value = "0.1", inclusive = true, message = "Alíquota de COFINS deve ser maior que zero")
    @DecimalMax(value = "100.0", inclusive = true, message = "Alíquota de COFINS deve ser menor ou igual a 100")
    private BigDecimal aliquotaCofinsPadrao;

    public ProdutoRequestDTO(String codigoProduto, String nome, String descricao, Long tipoProduto, Unidade unidade, BigDecimal precoVenda, String ncm, String cfopPadrao, BigDecimal aliquotaIcmsPadrao, BigDecimal aliquotaPisPadrao, BigDecimal aliquotaCofinsPadrao) {
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

}
