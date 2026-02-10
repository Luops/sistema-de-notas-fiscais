package dev.ellyon.sistemanotas.nfe.dto;

public class NFeRetornoDTO {
    private String chaveAcesso;
    private String codigoStatus;
    private String mensagem;
    private String protocolo;
    private String dataHoraAutorizacao;
    private String xmlResposta;

    // Getters e Setters
    public String getChaveAcesso() {
        return chaveAcesso;
    }

    public void setChaveAcesso(String chaveAcesso) {
        this.chaveAcesso = chaveAcesso;
    }

    public String getCodigoStatus() {
        return codigoStatus;
    }

    public void setCodigoStatus(String codigoStatus) {
        this.codigoStatus = codigoStatus;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public String getDataHoraAutorizacao() {
        return dataHoraAutorizacao;
    }

    public void setDataHoraAutorizacao(String dataHoraAutorizacao) {
        this.dataHoraAutorizacao = dataHoraAutorizacao;
    }

    public String getXmlResposta() {
        return xmlResposta;
    }

    public void setXmlResposta(String xmlResposta) {
        this.xmlResposta = xmlResposta;
    }
}