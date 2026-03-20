package dev.ellyon.sistemanotas.dto.produto;

public class ProdutoSimpleResponseDTO {
    private Long id;
    private String codigoProduto;
    private String nome;

    // Construtor padrao
    public ProdutoSimpleResponseDTO() {

    }

    // Construtor completo
    public ProdutoSimpleResponseDTO(Long id, String codigoProduto, String nome) {
        this.id = id;
        this.codigoProduto = codigoProduto;
        this.nome = nome;
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
}
