package dev.ellyon.sistemanotas.nfe.service;

import dev.ellyon.sistemanotas.nfe.config.NFeConfig;
import dev.ellyon.sistemanotas.service.EmpresaService;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

@Service
public class SefazWebService {

    private final NFeConfig nfeConfig;
    private final SefazMockService mockService;
    private final EmpresaService empresaService;
    private final CertificadoService certificadoService;
    public SefazWebService(NFeConfig nfeConfig, SefazMockService mockService, EmpresaService empresaService, CertificadoService certificadoService) {
        this.nfeConfig = nfeConfig;
        this.mockService = mockService;
        this.empresaService = empresaService;
        this.certificadoService = certificadoService;
    }

    /**
     * Envia NF-e para autorização na SEFAZ
     */
    public String autorizarNFe(String xmlAssinado, String chaveAcesso) throws Exception {
        // Se modo mock ativo, retorna simulacao
        if (Boolean.TRUE.equals(nfeConfig.getMockSefaz())) {
            return mockService.simularAutorizacao(chaveAcesso);
        }

        String url = nfeConfig.getSefaz().getUrlAutorizacao();
        String soapEnvelope = criarEnvelopeAutorizacao(xmlAssinado, chaveAcesso);
        return enviarSoap(url, soapEnvelope, "http://www.portalfiscal.inf.br/nfe/wsdl/NFeAutorizacao4/nfeAutorizacaoLote");
    }

    /**
     * Consulta o retorno da autorização
     */
    public String consultarRetornoAutorizacao(String recibo) throws Exception {
        String url = nfeConfig.getSefaz().getUrlRetornoAutorizacao();
        String soapEnvelope = criarEnvelopeConsultaRetorno(recibo);
        return enviarSoap(url, soapEnvelope, "http://www.portalfiscal.inf.br/nfe/wsdl/NFeRetAutorizacao4/nfeRetAutorizacaoLote");
    }

    /**
     * Consulta o status do serviço da SEFAZ
     */
    public String consultarStatusServico() throws Exception {
        // Se modo mock ativo, retorna simulação
        if (Boolean.TRUE.equals(nfeConfig.getMockSefaz())) {
            return mockService.simularStatusServico();
        }

        String url = nfeConfig.getSefaz().getUrlStatusServico();

        String soapEnvelope = String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" " +
                        "xmlns:nfe=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeStatusServico4\">" +
                        "<soap:Header/>" +
                        "<soap:Body>" +
                        "<nfe:nfeStatusServicoNF>" +
                        "<nfeDadosMsg>" +
                        "<consStatServ xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"4.00\">" +
                        "<tpAmb>%s</tpAmb>" +
                        "<cUF>43</cUF>" +
                        "<xServ>STATUS</xServ>" +
                        "</consStatServ>" +
                        "</nfeDadosMsg>" +
                        "</nfe:nfeStatusServicoNF>" +
                        "</soap:Body>" +
                        "</soap:Envelope>",
                nfeConfig.getAmbiente()
        );

        return enviarSoap(url, soapEnvelope, "http://www.portalfiscal.inf.br/nfe/wsdl/NFeStatusServico4/nfeStatusServicoNF");
    }

    /**
     * Cancela uma NF-e autorizada
     */
    public String cancelarNFe(String chaveAcesso, String protocolo, String justificativa) throws Exception {
        // Se modo mock ativo, retorna simulação
        if (Boolean.TRUE.equals(nfeConfig.getMockSefaz())) {
            return mockService.simularCancelamento(chaveAcesso);
        }

        String url = nfeConfig.getSefaz().getUrlCancelamento();
        String xmlEvento = criarEventoCancelamento(chaveAcesso, protocolo, justificativa);

        String soapEnvelope = String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" " +
                        "xmlns:nfe=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeRecepcaoEvento4\">" +
                        "<soap:Header/>" +
                        "<soap:Body>" +
                        "<nfe:nfeRecepcaoEvento>" +
                        "<nfeDadosMsg>%s</nfeDadosMsg>" +
                        "</nfe:nfeRecepcaoEvento>" +
                        "</soap:Body>" +
                        "</soap:Envelope>",
                xmlEvento
        );

        return enviarSoap(url, soapEnvelope, "http://www.portalfiscal.inf.br/nfe/wsdl/NFeRecepcaoEvento4/nfeRecepcaoEvento");
    }

    /**
     * Cria envelope SOAP para autorização
     */
    private String criarEnvelopeAutorizacao(String xmlAssinado, String chaveAcesso) {
        return String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" " +
                        "xmlns:nfe=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeAutorizacao4\">" +
                        "<soap:Header/>" +
                        "<soap:Body>" +
                        "<nfe:nfeAutorizacaoLote>" +
                        "<nfeDadosMsg>" +
                        "<enviNFe xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"4.00\">" +
                        "<idLote>%s</idLote>" +
                        "<indSinc>1</indSinc>" +
                        "%s" +
                        "</enviNFe>" +
                        "</nfeDadosMsg>" +
                        "</nfe:nfeAutorizacaoLote>" +
                        "</soap:Body>" +
                        "</soap:Envelope>",
                System.currentTimeMillis(),
                xmlAssinado
        );
    }

    /**
     * Cria envelope SOAP para consulta de retorno
     */
    private String criarEnvelopeConsultaRetorno(String recibo) {
        return String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" " +
                        "xmlns:nfe=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeRetAutorizacao4\">" +
                        "<soap:Header/>" +
                        "<soap:Body>" +
                        "<nfe:nfeRetAutorizacaoLote>" +
                        "<nfeDadosMsg>" +
                        "<consReciNFe xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"4.00\">" +
                        "<tpAmb>%s</tpAmb>" +
                        "<nRec>%s</nRec>" +
                        "</consReciNFe>" +
                        "</nfeDadosMsg>" +
                        "</nfe:nfeRetAutorizacaoLote>" +
                        "</soap:Body>" +
                        "</soap:Envelope>",
                nfeConfig.getAmbiente(),
                recibo
        );
    }

    /**
     * Cria XML de evento de cancelamento
     */
    private String criarEventoCancelamento(String chaveAcesso, String protocolo, String justificativa) {
        return String.format(
                "<envEvento xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"1.00\">" +
                        "<idLote>%s</idLote>" +
                        "<evento versao=\"1.00\">" +
                        "<infEvento Id=\"ID110111%s01\">" +
                        "<cOrgao>43</cOrgao>" +
                        "<tpAmb>%s</tpAmb>" +
                        "<CNPJ>%s</CNPJ>" +
                        "<chNFe>%s</chNFe>" +
                        "<dhEvento>%s</dhEvento>" +
                        "<tpEvento>110111</tpEvento>" +
                        "<nSeqEvento>1</nSeqEvento>" +
                        "<verEvento>1.00</verEvento>" +
                        "<detEvento versao=\"1.00\">" +
                        "<descEvento>Cancelamento</descEvento>" +
                        "<nProt>%s</nProt>" +
                        "<xJust>%s</xJust>" +
                        "</detEvento>" +
                        "</infEvento>" +
                        "</evento>" +
                        "</envEvento>",
                System.currentTimeMillis(),
                chaveAcesso,
                nfeConfig.getAmbiente(),
                "12345678000190",
                chaveAcesso,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                protocolo,
                justificativa
        );
    }

    /**
     * Envia requisição SOAP usando Apache HttpClient
     * ✅ COM SUPORTE A SSL (aceita certificados auto-assinados em homologação)
     */
    private String enviarSoap(String url, String soapEnvelope, String soapAction) throws Exception {
        // Criar SSLContext que aceita todos os certificados (APENAS HOMOLOGAÇÃO!)
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(new TrustAllStrategy()) // ✅ Aceita todos os certificados
                .build();

        // Criar SSLConnectionSocketFactory
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                (hostname, session) -> true // ✅ Aceita qualquer hostname
        );

        // Criar HttpClientConnectionManager com SSL configurado
        HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();

        // Criar HttpClient com SSL configurado
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build()) {

            HttpPost httpPost = new HttpPost(url);

            // Configurar headers
            httpPost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
            httpPost.setHeader("SOAPAction", soapAction);

            // Adicionar corpo da requisição
            StringEntity entity = new StringEntity(soapEnvelope, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            // Executar requisição
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        }
    }

    /**
     * Envia NF-e para SEFAZ
     */
    public String enviarNFe(Long empresaId, String xmlAssinado) {
        try {
            // ✅ Se modo mock ativo, retorna simulação
            if (Boolean.TRUE.equals(nfeConfig.getMockSefaz())) {
                // Extrair chave de acesso do XML para passar para o mock
                int inicio = xmlAssinado.indexOf("<infNFe Id=\"NFe");
                int fim = xmlAssinado.indexOf("\"", inicio + 15);
                String chaveAcesso = xmlAssinado.substring(inicio + 15, fim);
                return mockService.simularAutorizacao(chaveAcesso);
            }

            // ✅ Carregar certificado da empresa usando CertificadoService
            CertificadoService.CertificadoData certificado =
                    certificadoService.carregarCertificadoDaEmpresa(empresaId);

            // Configurar SSL com certificado da empresa
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadKeyMaterial(
                            criarKeyStore(certificado),
                            "".toCharArray()
                    )
                    .loadTrustMaterial(new TrustAllStrategy())
                    .build();

            // Criar SSLConnectionSocketFactory
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                    sslContext,
                    (hostname, session) -> true
            );

            // Criar HttpClientConnectionManager com SSL configurado
            HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslSocketFactory)
                    .build();

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .build();

            // Enviar requisição SOAP
            HttpPost request = new HttpPost(nfeConfig.getSefaz().getUrlAutorizacao());
            request.setHeader("Content-Type", "application/soap+xml; charset=utf-8");
            request.setEntity(new StringEntity(
                    montarEnvelopeSoap(xmlAssinado),
                    StandardCharsets.UTF_8
            ));

            CloseableHttpResponse response = httpClient.execute(request);
            String xmlResposta = EntityUtils.toString(response.getEntity());

            return extrairXmlResposta(xmlResposta);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar NF-e para SEFAZ: " + e.getMessage(), e);
        }
    }

    /**
     * Cria KeyStore temporário a partir do certificado
     */
    private KeyStore criarKeyStore(CertificadoService.CertificadoData certificado) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        keyStore.setKeyEntry(
                "cert",
                certificado.getPrivateKey(),
                "".toCharArray(),
                new java.security.cert.Certificate[]{certificado.getCertificate()}
        );
        return keyStore;
    }

    /**
     * Monta envelope SOAP (você precisa implementar)
     */
    private String montarEnvelopeSoap(String xmlAssinado) {
        return criarEnvelopeAutorizacao(xmlAssinado, "");
    }

    /**
     * Extrai XML de resposta (você precisa implementar)
     */
    private String extrairXmlResposta(String soapResponse) {
        int inicio = soapResponse.indexOf("<retEnviNFe");
        int fim = soapResponse.indexOf("</retEnviNFe>") + 13;

        if (inicio >= 0 && fim > inicio) {
            return soapResponse.substring(inicio, fim);
        }

        return soapResponse;
    }
}