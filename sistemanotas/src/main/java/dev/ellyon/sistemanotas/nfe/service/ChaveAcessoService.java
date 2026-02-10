package dev.ellyon.sistemanotas.nfe.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ChaveAcessoService {

    /**
     * Gera a chave de acesso da NF-e (44 dígitos)
     * Formato: cUF + AAMM + CNPJ + mod + serie + nNF + tpEmis + cNF + DV
     */
    public String gerar(String uf, LocalDateTime dataEmissao, String cnpj,
                        String modelo, String serie, String numero,
                        String tipoEmissao, String codigoNumerico) {

        StringBuilder chave = new StringBuilder();

        // 1. Código da UF (2 dígitos)
        chave.append(obterCodigoUF(uf));

        // 2. Ano e Mês de emissão (4 dígitos: AAMM)
        chave.append(dataEmissao.format(DateTimeFormatter.ofPattern("yyMM")));

        // 3. CNPJ do emitente (14 dígitos)
        chave.append(String.format("%014d", Long.parseLong(cnpj.replaceAll("\\D", ""))));

        // 4. Modelo da NF-e (2 dígitos) - sempre 55 para NF-e
        chave.append(String.format("%02d", Integer.parseInt(modelo)));

        // 5. Série (3 dígitos)
        chave.append(String.format("%03d", Integer.parseInt(serie)));

        // 6. Número da NF-e (9 dígitos)
        chave.append(String.format("%09d", Integer.parseInt(numero)));

        // 7. Tipo de Emissão (1 dígito)
        chave.append(tipoEmissao);

        // 8. Código Numérico (8 dígitos) - gerado aleatoriamente
        chave.append(String.format("%08d", Integer.parseInt(codigoNumerico)));

        // 9. Dígito Verificador (1 dígito)
        chave.append(calcularDigitoVerificador(chave.toString()));

        return chave.toString();
    }

    /**
     * Gera código numérico aleatório de 8 dígitos
     */
    public String gerarCodigoNumerico() {
        return String.format("%08d", (int) (Math.random() * 100000000));
    }

    /**
     * Calcula o dígito verificador usando módulo 11
     */
    private int calcularDigitoVerificador(String chave) {
        int soma = 0;
        int peso = 2;

        // Percorre a chave de trás para frente
        for (int i = chave.length() - 1; i >= 0; i--) {
            soma += Character.getNumericValue(chave.charAt(i)) * peso;
            peso++;
            if (peso > 9) {
                peso = 2;
            }
        }

        int resto = soma % 11;
        int dv = 11 - resto;

        return (dv == 0 || dv == 1 || dv > 9) ? 0 : dv;
    }

    /**
     * Retorna o código da UF conforme tabela do IBGE
     */
    private String obterCodigoUF(String uf) {
        return switch (uf.toUpperCase()) {
            case "AC" -> "12";
            case "AL" -> "27";
            case "AP" -> "16";
            case "AM" -> "13";
            case "BA" -> "29";
            case "CE" -> "23";
            case "DF" -> "53";
            case "ES" -> "32";
            case "GO" -> "52";
            case "MA" -> "21";
            case "MT" -> "51";
            case "MS" -> "50";
            case "MG" -> "31";
            case "PA" -> "15";
            case "PB" -> "25";
            case "PR" -> "41";
            case "PE" -> "26";
            case "PI" -> "22";
            case "RJ" -> "33";
            case "RN" -> "24";
            case "RS" -> "43";
            case "RO" -> "11";
            case "RR" -> "14";
            case "SC" -> "42";
            case "SP" -> "35";
            case "SE" -> "28";
            case "TO" -> "17";
            default -> throw new IllegalArgumentException("UF inválida: " + uf);
        };
    }

    /**
     * Formata a chave de acesso para exibição
     * Formato: 9999 9999 9999 9999 9999 9999 9999 9999 9999 9999 9999
     */
    public String formatar(String chave) {
        if (chave == null || chave.length() != 44) {
            throw new IllegalArgumentException("Chave de acesso inválida");
        }

        StringBuilder formatada = new StringBuilder();
        for (int i = 0; i < chave.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatada.append(" ");
            }
            formatada.append(chave.charAt(i));
        }
        return formatada.toString();
    }
}