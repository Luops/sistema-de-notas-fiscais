package dev.ellyon.sistemanotas.nfe.dto;

public class NFeStatusDTO {
    private Boolean online;
    private String codigoStatus;
    private String mensagem;

    // Construtor padrão
    public NFeStatusDTO() {
    }

    // Construtor completo
    public NFeStatusDTO(Boolean online, String codigoStatus, String mensagem) {
        this.online = online;
        this.codigoStatus = codigoStatus;
        this.mensagem = mensagem;
    }

    // Getters e Setters
    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
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
}