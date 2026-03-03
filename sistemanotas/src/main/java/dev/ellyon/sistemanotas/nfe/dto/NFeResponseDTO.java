package dev.ellyon.sistemanotas.nfe.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class NFeResponseDTO {
    private String chaveAcesso;
    private String codigoStatus;
    private String mensagem;
    private String protocolo;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dataHoraAutorizacao;
    private String xmlResposta;

    // Construtor padrao
    public NFeResponseDTO() {
    }

    // Construtor completo
    public NFeResponseDTO(String chaveAcesso, String codigoStatus, String mensagem, String protocolo, LocalDateTime dataHoraAutorizacao, String xmlResposta) {
        this.chaveAcesso = chaveAcesso;
        this.codigoStatus = codigoStatus;
        this.mensagem = mensagem;
        this.protocolo = protocolo;
        this.dataHoraAutorizacao = dataHoraAutorizacao;
        this.xmlResposta = xmlResposta;
    }

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

    public LocalDateTime getDataHoraAutorizacao() {
        return dataHoraAutorizacao;
    }

    public void setDataHoraAutorizacao(LocalDateTime dataHoraAutorizacao) {
        this.dataHoraAutorizacao = dataHoraAutorizacao;
    }

    public String getXmlResposta() {
        return xmlResposta;
    }

    public void setXmlResposta(String xmlResposta) {
        this.xmlResposta = xmlResposta;
    }
}