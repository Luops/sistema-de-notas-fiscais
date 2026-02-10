package dev.ellyon.sistemanotas.nfe.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SefazMockService {

    /**
     * Simula resposta de autorização do SEFAZ
     */
    public String simularAutorizacao(String chaveAcesso) {
        // Gerar protocolo de 15 dígitos (adicionar zeros à esquerda se necessário)
        String timestamp = String.valueOf(System.currentTimeMillis());
        String protocolo = String.format("%015d", Long.parseLong(timestamp));

        String dhRecbto = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">" +
                        "<soap:Body>" +
                        "<nfeResultMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeAutorizacao4\">" +
                        "<retEnviNFe xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"4.00\">" +
                        "<tpAmb>2</tpAmb>" +
                        "<cUF>43</cUF>" +
                        "<verAplic>SVRS202301171338</verAplic>" +
                        "<cStat>100</cStat>" +
                        "<xMotivo>Autorizado o uso da NF-e</xMotivo>" +
                        "<dhRecbto>%s</dhRecbto>" +
                        "<protNFe versao=\"4.00\">" +
                        "<infProt>" +
                        "<tpAmb>2</tpAmb>" +
                        "<verAplic>SVRS202301171338</verAplic>" +
                        "<chNFe>%s</chNFe>" +
                        "<dhRecbto>%s</dhRecbto>" +
                        "<nProt>%s</nProt>" +
                        "<digVal>abcd1234efgh5678ijkl9012mnop3456qrst7890=</digVal>" +
                        "<cStat>100</cStat>" +
                        "<xMotivo>Autorizado o uso da NF-e</xMotivo>" +
                        "</infProt>" +
                        "</protNFe>" +
                        "</retEnviNFe>" +
                        "</nfeResultMsg>" +
                        "</soap:Body>" +
                        "</soap:Envelope>",
                dhRecbto,
                chaveAcesso,
                dhRecbto,
                protocolo
        );
    }

    /**
     * Simula resposta de cancelamento do SEFAZ
     */
    public String simularCancelamento(String chaveAcesso) {
        String protocolo = String.format("%015d", System.currentTimeMillis());
        String dhRecbto = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">" +
                        "<soap:Body>" +
                        "<nfeResultMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeRecepcaoEvento4\">" +
                        "<retEnvEvento xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"1.00\">" +
                        "<idLote>%s</idLote>" +
                        "<tpAmb>2</tpAmb>" +
                        "<verAplic>SVRS202301171338</verAplic>" +
                        "<cOrgao>43</cOrgao>" +
                        "<cStat>128</cStat>" +
                        "<xMotivo>Lote de Evento Processado</xMotivo>" +
                        "<retEvento versao=\"1.00\">" +
                        "<infEvento>" +
                        "<tpAmb>2</tpAmb>" +
                        "<verAplic>SVRS202301171338</verAplic>" +
                        "<cOrgao>43</cOrgao>" +
                        "<cStat>135</cStat>" +
                        "<xMotivo>Evento registrado e vinculado a NF-e</xMotivo>" +
                        "<chNFe>%s</chNFe>" +
                        "<tpEvento>110111</tpEvento>" +
                        "<xEvento>Cancelamento</xEvento>" +
                        "<nSeqEvento>1</nSeqEvento>" +
                        "<dhRegEvento>%s</dhRegEvento>" +
                        "<nProt>%s</nProt>" +
                        "</infEvento>" +
                        "</retEvento>" +
                        "</retEnvEvento>" +
                        "</nfeResultMsg>" +
                        "</soap:Body>" +
                        "</soap:Envelope>",
                System.currentTimeMillis(),
                chaveAcesso,
                dhRecbto,
                protocolo
        );
    }

    /**
     * Simula resposta de status do serviço
     */
    public String simularStatusServico() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<soap:Body>" +
                "<nfeResultMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeStatusServico4\">" +
                "<retConsStatServ xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"4.00\">" +
                "<tpAmb>2</tpAmb>" +
                "<verAplic>SVRS202301171338</verAplic>" +
                "<cUF>43</cUF>" +
                "<dhRecbto>" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "</dhRecbto>" +
                "<tMed>1</tMed>" +
                "<cStat>107</cStat>" +
                "<xMotivo>Serviço em Operação</xMotivo>" +
                "</retConsStatServ>" +
                "</nfeResultMsg>" +
                "</soap:Body>" +
                "</soap:Envelope>";
    }

    /**
     * Simula erro de rejeição (para testes)
     */
    public String simularRejeicao(String chaveAcesso, String codigoRejeicao, String motivo) {
        String dhRecbto = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">" +
                        "<soap:Body>" +
                        "<nfeResultMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeAutorizacao4\">" +
                        "<retEnviNFe xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"4.00\">" +
                        "<tpAmb>2</tpAmb>" +
                        "<cUF>43</cUF>" +
                        "<verAplic>SVRS202301171338</verAplic>" +
                        "<cStat>%s</cStat>" +
                        "<xMotivo>%s</xMotivo>" +
                        "<dhRecbto>%s</dhRecbto>" +
                        "</retEnviNFe>" +
                        "</nfeResultMsg>" +
                        "</soap:Body>" +
                        "</soap:Envelope>",
                codigoRejeicao,
                motivo,
                dhRecbto
        );
    }
}