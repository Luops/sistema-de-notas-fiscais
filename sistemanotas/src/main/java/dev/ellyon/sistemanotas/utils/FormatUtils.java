package dev.ellyon.sistemanotas.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;



public class FormatUtils {

    private FormatUtils() {}

    // ================= FORMATADORES DE DATA =================
    private static final DateTimeFormatter FORMATTER_DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATTER_DATA_HORA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATTER_DATA_HORA_CURTA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATTER_DATA_EXTENSO_BR = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
    private static final DateTimeFormatter FORMATTER_HORA_BR = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FORMATTER_HORA_CURTA_BR = DateTimeFormatter.ofPattern("HH:mm");

    private static final ZoneId ZONE_BR = ZoneId.of("America/Sao_Paulo");

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

    /* ================= DATAS PT-BR ================= */

    /**
     * Formata LocalDate para formato brasileiro (dd/MM/yyyy)
     * Exemplo: 2026-01-10 -> "10/01/2026"
     */
    public static String formatDataBR(LocalDate data) {
        if (data == null) return null;
        return data.format(FORMATTER_DATA_BR);
    }

    /**
     * Formata LocalDateTime para formato brasileiro (dd/MM/yyyy)
     * Exemplo: 2026-01-10T14:30:00 -> "10/01/2026"
     */
    public static String formatDataBR(LocalDateTime dataHora) {
        if (dataHora == null) return null;
        return dataHora.format(FORMATTER_DATA_BR);
    }

    /**
     * Formata LocalDateTime para formato brasileiro com hora completa (dd/MM/yyyy HH:mm:ss)
     * Exemplo: 2026-01-10T14:30:45 -> "10/01/2026 14:30:45"
     */
    public static String formatDataHoraBR(LocalDateTime dataHora) {
        if (dataHora == null) return null;
        return dataHora
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZONE_BR)
                .format(FORMATTER_DATA_HORA_BR);
    }

    /**
     * Formata LocalDateTime para formato brasileiro com hora curta (dd/MM/yyyy HH:mm)
     * Exemplo: 2026-01-10T14:30:45 -> "10/01/2026 14:30"
     */
    public static String formatDataHoraCurtaBR(LocalDateTime dataHora) {
        if (dataHora == null) return null;
        return dataHora.format(FORMATTER_DATA_HORA_CURTA_BR);
    }

    /**
     * Formata LocalDate para formato extenso em português
     * Exemplo: 2026-01-10 -> "10 de janeiro de 2026"
     */
    public static String formatDataExtensoBR(LocalDate data) {
        if (data == null) return null;
        return data.format(FORMATTER_DATA_EXTENSO_BR);
    }

    /**
     * Formata LocalDateTime para formato extenso em português
     * Exemplo: 2026-01-10T14:30:00 -> "10 de janeiro de 2026"
     */
    public static String formatDataExtensoBR(LocalDateTime dataHora) {
        if (dataHora == null) return null;
        return dataHora.format(FORMATTER_DATA_EXTENSO_BR);
    }

    /**
     * Formata LocalDateTime para hora completa (HH:mm:ss)
     * Exemplo: 2026-01-10T14:30:45 -> "14:30:45"
     */
    public static String formatHoraBR(LocalDateTime dataHora) {
        if (dataHora == null) return null;
        return dataHora.format(FORMATTER_HORA_BR);
    }

    /**
     * Formata LocalDateTime para hora curta (HH:mm)
     * Exemplo: 2026-01-10T14:30:45 -> "14:30"
     */
    public static String formatHoraCurtaBR(LocalDateTime dataHora) {
        if (dataHora == null) return null;
        return dataHora.format(FORMATTER_HORA_CURTA_BR);
    }
}
