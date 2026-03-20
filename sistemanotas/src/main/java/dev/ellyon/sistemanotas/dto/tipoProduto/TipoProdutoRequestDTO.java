package dev.ellyon.sistemanotas.dto.tipoProduto;

import dev.ellyon.sistemanotas.model.Empresa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TipoProdutoRequestDTO {
    @NotBlank(message = "Nome do Tipo de Produto é obrigatório.")
    @Size(min = 3, max = 100, message = "Nome do Tipo de Produto deve ter no mínimo 3 e no máximo 100 caracteres.")
    private String nome;

    private Empresa empresa;

    public TipoProdutoRequestDTO(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }
}
