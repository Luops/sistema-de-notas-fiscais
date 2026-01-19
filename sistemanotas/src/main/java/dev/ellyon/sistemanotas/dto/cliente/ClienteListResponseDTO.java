package dev.ellyon.sistemanotas.dto.cliente;

public class ClienteListResponseDTO {
    private Long id;
    private String nome;
    private String tipoPessoa;
    private String cpfCnpj;
    private String telefone;
    private String cidade;
    private String estadoUF;
    private String email;
    private Boolean isAtivo;

    // Construtor padrao
    public ClienteListResponseDTO() {
    }

    // Construtor completo
    public ClienteListResponseDTO(Long id, String nome, String tipoPessoa, String cpfCnpj, String telefone, String cidade, String estadoUF, String email, Boolean isAtivo) {
        this.id = id;
        this.nome = nome;
        this.tipoPessoa = tipoPessoa;
        this.cpfCnpj = cpfCnpj;
        this.telefone = telefone;
        this.cidade = cidade;
        this.estadoUF = estadoUF;
        this.email = email;
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

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstadoUF() {
        return estadoUF;
    }

    public void setEstadoUF(String estadoUF) {
        this.estadoUF = estadoUF;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAtivo() {
        return isAtivo;
    }

    public void setAtivo(Boolean ativo) {
        isAtivo = ativo;
    }
}
