package dev.ellyon.sistemanotas.nfe.dto;

/**
 * DTO para resposta de cancelamento de NF-e
 */
public class CancelamentoNFeDTOResponse {
    private String codigoStatus;
    private String mensagem;
    private String protocolo;
    private String chaveAcesso;
    private String xmlResposta;

    public CancelamentoNFeDTOResponse() {
    }

    public CancelamentoNFeDTOResponse(String codigoStatus, String mensagem, String protocolo, String chaveAcesso, String xmlResposta) {
        this.codigoStatus = codigoStatus;
        this.mensagem = mensagem;
        this.protocolo = protocolo;
        this.chaveAcesso = chaveAcesso;
        this.xmlResposta = xmlResposta;
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

    public String getChaveAcesso() {
        return chaveAcesso;
    }

    public void setChaveAcesso(String chaveAcesso) {
        this.chaveAcesso = chaveAcesso;
    }

    public String getXmlResposta() {
        return xmlResposta;
    }

    public void setXmlResposta(String xmlResposta) {
        this.xmlResposta = xmlResposta;
    }
}
