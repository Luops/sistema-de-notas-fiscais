package dev.ellyon.sistemanotas.nfe.util;

public class NFeConstants {

    // Versão do layout
    public static final String VERSAO_LAYOUT = "4.00";

    // Modelo da NF-e
    public static final String MODELO_NFE = "55";

    // Série padrão
    public static final String SERIE_PADRAO = "1";

    // Tipo de Emissão
    public static final String TIPO_EMISSAO_NORMAL = "1"; // Normal
    public static final String TIPO_EMISSAO_CONTINGENCIA = "9"; // Contingência

    // Finalidade
    public static final String FINALIDADE_NORMAL = "1"; // Normal
    public static final String FINALIDADE_COMPLEMENTAR = "2";
    public static final String FINALIDADE_AJUSTE = "3";
    public static final String FINALIDADE_DEVOLUCAO = "4";

    // Tipo de Operação
    public static final String TIPO_OPERACAO_SAIDA = "1";
    public static final String TIPO_OPERACAO_ENTRADA = "0";

    // Indicador de Presença
    public static final String PRESENCA_NAO_SE_APLICA = "0";
    public static final String PRESENCA_OPERACAO_PRESENCIAL = "1";
    public static final String PRESENCA_INTERNET = "2";
    public static final String PRESENCA_TELEATENDIMENTO = "3";
    public static final String PRESENCA_ENTREGA_DOMICILIO = "4";

    // Formato de Impressão DANFE
    public static final String FORMATO_DANFE_RETRATO = "1";
    public static final String FORMATO_DANFE_PAISAGEM = "2";

    // Forma de Pagamento
    public static final String PAGAMENTO_A_VISTA = "0";
    public static final String PAGAMENTO_A_PRAZO = "1";

    // Status da Nota
    public static final String STATUS_AUTORIZADA = "100";
    public static final String STATUS_CANCELADA = "101";
    public static final String STATUS_DENEGADA = "110";

    private NFeConstants() {
        // Classe de constantes não deve ser instanciada
    }
}