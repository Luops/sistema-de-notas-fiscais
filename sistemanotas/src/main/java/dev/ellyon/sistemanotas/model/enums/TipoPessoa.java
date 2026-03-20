package dev.ellyon.sistemanotas.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoPessoa {
  FISICA("F", "Pessoa Física"),
  JURIDICA("J", "Pessoa Jurídica"),
  CONSUMIDOR_FINAL("CF", "Consumidor Final"),
  CONSUMIDOR_NAO_IDENTIFICADO("CNI", "Consumidor Não Identificado");

  private final String codigo;
  private final String descricao;

  TipoPessoa(String codigo, String descricao) {
    this.codigo = codigo;
    this.descricao = descricao;
  }

  @JsonValue
  public String getCodigo() {
    return codigo;
  }

  public String getDescricao() {
    return descricao;
  }

  @JsonCreator
  public static TipoPessoa fromCodigo(String codigo) {
    // Retorna CONSUMIDOR_FINAL se o campo vier vazio ou null
    if (codigo == null || codigo.trim().isEmpty()) {
      return CONSUMIDOR_FINAL;
    }

    for (TipoPessoa tipo : TipoPessoa.values()) {
      if (tipo.codigo.equalsIgnoreCase(codigo)) {
        return tipo;
      }
    }

    throw new IllegalArgumentException(
            "Tipo de pessoa inválido: " + codigo + ". Valores aceitos: F (Física), J (Jurídica), CF (Consumidor Final), CNI (Consumidor Não Identificado)"
    );
  }

  @Override
  public String toString() {
    return descricao; // Retorna: FISICA, JURIDICA, CONSUMIDOR_FINAL, CONSUMIDOR_NAO_IDENTIFICADO
  }
}