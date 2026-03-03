package dev.ellyon.sistemanotas.dto.cfop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CfopRequestDTO {
    @NotBlank(message = "Código é obrigatório")
    @Pattern(regexp = "\\d{4}", message = "Código deve ter exatamente 4 dígitos")
    private String codigo;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
    private String descricao;

    @Size(max = 1000, message = "Aplicação não pode ter mais de 1000 caracteres")
    private String aplicacao;

    @NotBlank(message = "Tipo é obrigatório")
    @Pattern(regexp = "ENTRADA|SAIDA", message = "Tipo deve ser ENTRADA ou SAIDA")
    private String tipo;

    @NotBlank(message = "Natureza é obrigatória")
    private String natureza;

    @NotBlank(message = "Operação é obrigatória")
    private String operacao;

    public CfopRequestDTO() {}

    public CfopRequestDTO(String codigo, String descricao, String aplicacao, String tipo, String natureza, String operacao) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.aplicacao = aplicacao;
        this.tipo = tipo;
        this.natureza = natureza;
        this.operacao = operacao;
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

    public String getAplicacao() {
        return aplicacao;
    }

    public void setAplicacao(String aplicacao) {
        this.aplicacao = aplicacao;
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
