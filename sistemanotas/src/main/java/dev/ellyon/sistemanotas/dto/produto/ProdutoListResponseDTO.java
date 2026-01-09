package dev.ellyon.sistemanotas.dto.produto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProdutoListResponseDTO {
    private Long id;
    private String codigoProduto;
    private String nome;
    private String tipoProdutoNome;
    private String unidade;
    private BigDecimal precoVenda;
    private Boolean isAtivo;
    private LocalDateTime createdAt;

    // Construtor padrao
    public ProdutoListResponseDTO() {
    }

    // Construtor completo
    public ProdutoListResponseDTO(Long id, String codigoProduto, String nome, String tipoProdutoNome, String unidade, BigDecimal precoVenda, Boolean isAtivo, LocalDateTime createdAt) {
        this.id = id;
        this.codigoProduto = codigoProduto;
        this.nome = nome;
        this.tipoProdutoNome = tipoProdutoNome;
        this.unidade = unidade;
        this.precoVenda = precoVenda;
        this.isAtivo = isAtivo;
        this.createdAt = createdAt;
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

    public String getTipoProdutoNome() {
        return tipoProdutoNome;
    }

    public void setTipoProdutoNome(String tipoProdutoNome) {
        this.tipoProdutoNome = tipoProdutoNome;
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
}
