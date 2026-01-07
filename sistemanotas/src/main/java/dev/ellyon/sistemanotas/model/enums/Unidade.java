package dev.ellyon.sistemanotas.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Unidade {
  UN("UN", "Unidade"),
  KG("KG", "Quilograma"),
  L("L", "Litro"),
  M("M", "Metro"),
  CX("CX", "Caixa"),
  PC("PC", "Peça");

  private final String codigo;
  private final String descricao;

  Unidade(String codigo, String descricao) {
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
  public static Unidade fromCodigo(String codigo) {
    if (codigo == null) {
      return null;
    }
    for (Unidade unidade : Unidade.values()) {
      if (unidade.codigo.equalsIgnoreCase(codigo)) {
        return unidade;
      }
    }
    throw new IllegalArgumentException("Unidade inválida: " + codigo + ". Valores aceitos: UN, KG, L, M, CX, PC");
  }

  @Override
  public String toString() {
    return codigo;
  }
}
