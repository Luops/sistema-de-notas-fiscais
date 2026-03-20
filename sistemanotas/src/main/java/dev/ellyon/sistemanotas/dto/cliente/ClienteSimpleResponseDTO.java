package dev.ellyon.sistemanotas.dto.cliente;

public class ClienteSimpleResponseDTO {
    private Long id;
    private String nome;
    private String cpfCnpj;

    // Construtor padrão
    public ClienteSimpleResponseDTO() {

    }

    // Construtor completo
    public ClienteSimpleResponseDTO(Long id, String nome, String cpfCnpj) {
        this.id = id;
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
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

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }
}
