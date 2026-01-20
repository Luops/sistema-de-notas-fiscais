package dev.ellyon.sistemanotas.utils;

public class FormatUtils {

    private FormatUtils() {}

    /* ================= CPF / CNPJ ================= */

    public static String formatCpfCnpj(String valor) {
        if (valor == null || valor.isBlank()) return null;

        String digits = valor.replaceAll("\\D", "");

        if (digits.length() == 11) {
            return digits.replaceFirst(
                    "(\\d{3})(\\d{3})(\\d{3})(\\d{2})",
                    "$1.$2.$3-$4"
            );
        }

        if (digits.length() == 14) {
            return digits.replaceFirst(
                    "(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})",
                    "$1.$2.$3/$4-$5"
            );
        }

        return valor;
    }

    /* ================= CEP ================= */

    public static String formatCep(String cep) {
        if (cep == null || cep.isBlank()) return null;

        String digits = cep.replaceAll("\\D", "");

        if (digits.length() == 8) {
            return digits.replaceFirst("(\\d{5})(\\d{3})", "$1-$2");
        }

        return cep;
    }

    /* ================= TELEFONE ================= */

    public static String formatTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) return null;

        String digits = telefone.replaceAll("\\D", "");

        if (digits.length() == 11) {
            return digits.replaceFirst(
                    "(\\d{2})(\\d{5})(\\d{4})",
                    "($1) $2-$3"
            );
        }

        if (digits.length() == 10) {
            return digits.replaceFirst(
                    "(\\d{2})(\\d{4})(\\d{4})",
                    "($1) $2-$3"
            );
        }

        return telefone;
    }

    /* ================= CAPITALIZE NOME ================= */

    public static String capitalizeNome(String nome){
        if (nome == null || nome.trim().isEmpty()) {
            return nome;
        }

        String[] palavras = nome.trim().toLowerCase().split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (String palavra : palavras) {
            if (palavra.length() > 0) {
                resultado.append(Character.toUpperCase(palavra.charAt(0)))
                        .append(palavra.substring(1))
                        .append(" ");
            }
        }

        return resultado.toString().trim();
    }
}
