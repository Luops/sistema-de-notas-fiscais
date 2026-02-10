package dev.ellyon.sistemanotas.nfe.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "nfe")
public class NFeConfig {

    private Integer ambiente; // 1=Produção, 2=Homologação
    private Boolean mockSefaz = false; // Teste de desenvolvimento
    private Certificado certificado = new Certificado();
    private Emitente emitente = new Emitente();
    private Sefaz sefaz = new Sefaz();

    // Getters e Setters
    public Boolean getMockSefaz() {
        return mockSefaz;
    }

    public void setMockSefaz(Boolean mockSefaz) {
        this.mockSefaz = mockSefaz;
    }

    public Integer getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(Integer ambiente) {
        this.ambiente = ambiente;
    }

    public Certificado getCertificado() {
        return certificado;
    }

    public void setCertificado(Certificado certificado) {
        this.certificado = certificado;
    }

    public Emitente getEmitente() {
        return emitente;
    }

    public void setEmitente(Emitente emitente) {
        this.emitente = emitente;
    }

    public Sefaz getSefaz() {
        return sefaz;
    }

    public void setSefaz(Sefaz sefaz) {
        this.sefaz = sefaz;
    }

    // Classes internas
    public static class Certificado {
        private String tipo;
        private String caminho;
        private String senha;

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getCaminho() {
            return caminho;
        }

        public void setCaminho(String caminho) {
            this.caminho = caminho;
        }

        public String getSenha() {
            return senha;
        }

        public void setSenha(String senha) {
            this.senha = senha;
        }
    }

    public static class Emitente {
        private String uf;
        private String codigoMunicipio;

        public String getUf() {
            return uf;
        }

        public void setUf(String uf) {
            this.uf = uf;
        }

        public String getCodigoMunicipio() {
            return codigoMunicipio;
        }

        public void setCodigoMunicipio(String codigoMunicipio) {
            this.codigoMunicipio = codigoMunicipio;
        }
    }

    public static class Sefaz {
        private String urlAutorizacao;
        private String urlRetornoAutorizacao;
        private String urlConsultaProtocolo;
        private String urlStatusServico;
        private String urlInutilizacao;
        private String urlCancelamento;

        public String getUrlAutorizacao() {
            return urlAutorizacao;
        }

        public void setUrlAutorizacao(String urlAutorizacao) {
            this.urlAutorizacao = urlAutorizacao;
        }

        public String getUrlRetornoAutorizacao() {
            return urlRetornoAutorizacao;
        }

        public void setUrlRetornoAutorizacao(String urlRetornoAutorizacao) {
            this.urlRetornoAutorizacao = urlRetornoAutorizacao;
        }

        public String getUrlConsultaProtocolo() {
            return urlConsultaProtocolo;
        }

        public void setUrlConsultaProtocolo(String urlConsultaProtocolo) {
            this.urlConsultaProtocolo = urlConsultaProtocolo;
        }

        public String getUrlStatusServico() {
            return urlStatusServico;
        }

        public void setUrlStatusServico(String urlStatusServico) {
            this.urlStatusServico = urlStatusServico;
        }

        public String getUrlInutilizacao() {
            return urlInutilizacao;
        }

        public void setUrlInutilizacao(String urlInutilizacao) {
            this.urlInutilizacao = urlInutilizacao;
        }

        public String getUrlCancelamento() {
            return urlCancelamento;
        }

        public void setUrlCancelamento(String urlCancelamento) {
            this.urlCancelamento = urlCancelamento;
        }
    }
}