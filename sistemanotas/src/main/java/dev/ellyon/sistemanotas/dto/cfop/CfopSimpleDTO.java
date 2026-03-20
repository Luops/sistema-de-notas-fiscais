package dev.ellyon.sistemanotas.dto.cfop;

public class CfopSimpleDTO {
    private Long id;
    private String codigo;
    private String descricao;
    private String tipo;
    private String natureza;
    private String operacao;

    public CfopSimpleDTO() {}

    public CfopSimpleDTO(Long id, String codigo, String descricao, String tipo, String natureza, String operacao) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.tipo = tipo;
        this.natureza = natureza;
        this.operacao = operacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNatureza() {
        return natureza;
    }

    public void setNatureza(String natureza) {
        this.natureza = natureza;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }
}
