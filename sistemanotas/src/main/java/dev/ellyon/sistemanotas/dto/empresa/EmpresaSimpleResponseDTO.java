package dev.ellyon.sistemanotas.dto.empresa;

public class EmpresaSimpleResponseDTO {
    private Long id;
    private String nomeFantasia;
    private String cnpj;

    // Construtor padrao
    public EmpresaSimpleResponseDTO() {

    }

    // Construtor completo
    public EmpresaSimpleResponseDTO(Long id, String nomeFantasia, String cnpj) {
        this.id = id;
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
}
