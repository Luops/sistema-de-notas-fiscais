package dev.ellyon.sistemanotas.dto.cliente;

public class ClienteListResponseDTO {
    private Long id;
    private String nome;
    private String telefone;
    private String enderecoCompleto;
    private String numeroEndereco;
    private String cidade;
    private Boolean isAtivo;

    // Construtor padrao
    public ClienteListResponseDTO() {
    }

    // Construtor completo
        public ClienteListResponseDTO(Long id, String nome, String telefone, String enderecoCompleto, String numeroEndereco, String cidade, Boolean isAtivo) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.enderecoCompleto = enderecoCompleto;
        this.numeroEndereco = numeroEndereco;
        this.cidade = cidade;
        this.isAtivo = isAtivo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEnderecoCompleto() {
        return enderecoCompleto;
    }

    public void setEnderecoCompleto(String enderecoCompleto) {
        this.enderecoCompleto = enderecoCompleto;
    }

    public String getNumeroEndereco() {
        return numeroEndereco;
    }

    public void setNumeroEndereco(String numeroEndereco) {
        this.numeroEndereco = numeroEndereco;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public Boolean getIsAtivo() {
        return isAtivo;
    }

    public void setIsAtivo(Boolean ativo) {
        isAtivo = ativo;
    }
}
