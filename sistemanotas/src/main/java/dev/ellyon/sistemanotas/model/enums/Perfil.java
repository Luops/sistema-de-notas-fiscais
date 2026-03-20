package dev.ellyon.sistemanotas.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Perfil {
  ADMIN,
  VENDEDOR,
  VISUALIZADOR;

  @JsonCreator
    public static Perfil fromCodigo(String perfil) {
        for (Perfil p : Perfil.values()) {
        if (p.name().equalsIgnoreCase(perfil)) {
            return p;
        }
        }
        throw new IllegalArgumentException("Perfil inválido: " + perfil + ". Valores aceitos: ADMIN, VENDEDOR, VISUALIZADOR");
    }
}
