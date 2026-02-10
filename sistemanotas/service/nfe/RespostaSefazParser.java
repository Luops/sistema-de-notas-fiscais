package dev.ellyon.sistemanotas.service.nfe;

import dev.ellyon.sistemanotas.dto.nfe.RespostaSefazDTO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class RespostaSefazParser {

    /**
     * Processa a resposta XML da SEFAZ
     */
    public static RespostaSefazDTO processar(String respostaSoap) throws Exception {
        RespostaSefazDTO resposta = new RespostaSefazDTO();

        try {
            // Parse do XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            Document doc = builder.parse(new InputSource(new StringReader(respostaSoap)));

            // Extrair informações da resposta
            String cStat = extrairTexto(doc, "cStat");
            String xMotivo = extrairTexto(doc, "xMotivo");
            String nProt = extrairTexto(doc, "nProt");
            String nfeProc = extrairTexto(doc, "procNFe");

            resposta.setStatus(cStat);
            resposta.setStatusDescricao(xMotivo);
            resposta.setProtocolo(nProt);
            resposta.setXmlRetorno(nfeProc);

            // Verificar se foi autorizado (cStat = 100)
            if ("100".equals(cStat)) {
                resposta.setAutorizado(true);
            } else {
                resposta.setAutorizado(false);
                resposta.setErro(xMotivo);
            }

            return resposta;
        } catch (Exception e) {
            resposta.setAutorizado(false);
            resposta.setErro("Erro ao processar resposta: " + e.getMessage());
            return resposta;
        }
    }

    /**
     * Extrai texto de um elemento XML pelo tag name
     */
    private static String extrairTexto(Document doc, String tagName) {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }

        // Tentar com namespace
        nodeList = doc.getElementsByTagNameNS("*", tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }

        return null;
    }

    /**
     * Extrai atributo de um elemento XML
     */
    private static String extrairAtributo(Document doc, String tagName, String atributo) {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Element element = (Element) nodeList.item(0);
            return element.getAttribute(atributo);
        }
        return null;
    }
}
