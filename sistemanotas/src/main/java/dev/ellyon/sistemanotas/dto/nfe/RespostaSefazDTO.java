package dev.ellyon.sistemanotas.dto.nfe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RespostaSefazDTO {
    private String protocolo;
    private String status;
    private String statusDescricao;
    private String xmlRetorno;
    private String erro;
    private boolean autorizado;

    // Construtor padrao
    public RespostaSefazDTO(){}

    // Construtor completo
    public RespostaSefazDTO(String protocolo, String status, String statusDescricao, String xmlRetorno, String erro, boolean autorizado) {
        this.protocolo = protocolo;
        this.status = status;
        this.statusDescricao = statusDescricao;
        this.xmlRetorno = xmlRetorno;
        this.erro = erro;
        this.autorizado = autorizado;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDescricao() {
        return statusDescricao;
    }

    public void setStatusDescricao(String statusDescricao) {
        this.statusDescricao = statusDescricao;
    }

    public String getXmlRetorno() {
        return xmlRetorno;
    }

    public void setXmlRetorno(String xmlRetorno) {
        this.xmlRetorno = xmlRetorno;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public boolean isAutorizado() {
        return autorizado;
    }

    public void setAutorizado(boolean autorizado) {
        this.autorizado = autorizado;
    }
}
