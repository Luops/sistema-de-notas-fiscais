package dev.ellyon.sistemanotas.nfe.service;

import org.apache.xml.security.Init;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Service
public class AssinaturaDigitalService {

    private final CertificadoService certificadoService;

    public AssinaturaDigitalService(CertificadoService certificadoService) {
        this.certificadoService = certificadoService;
        Init.init(); // Inicializar Apache Santuario
    }

    /**
     * Assina o XML da NF-e com certificado digital
     */
    public String assinar(String xmlSemAssinatura, String idElemento) throws Exception {
        // Carregar certificado
        PrivateKey privateKey = certificadoService.getPrivateKey();
        X509Certificate certificate = certificadoService.getCertificate();

        // Parsear XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlSemAssinatura.getBytes("UTF-8")));

        // ✅ Encontrar elemento <infNFe> por atributo Id
        Element elementToSign = findElementByAttributeValue(doc, "infNFe", "Id", idElemento);

        if (elementToSign == null) {
            throw new Exception("Elemento <infNFe> com Id='" + idElemento + "' não encontrado no XML");
        }

        // ✅ Definir ID para o elemento (necessário para referência na assinatura)
        elementToSign.setIdAttribute("Id", true);

        // Encontrar elemento pai <NFe> para inserir a assinatura
        Element nfeElement = (Element) elementToSign.getParentNode();

        // Criar assinatura XML
        XMLSignature signature = new XMLSignature(doc, "", XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);

        // ✅ Adicionar assinatura DEPOIS do <infNFe>
        nfeElement.appendChild(signature.getElement());

        // Adicionar transformações
        Transforms transforms = new Transforms(doc);
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        transforms.addTransform(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);

        // Adicionar referência usando URI com #
        signature.addDocument("#" + idElemento, transforms, Constants.ALGO_ID_DIGEST_SHA1);

        // Adicionar certificado
        signature.addKeyInfo(certificate);

        // Assinar
        signature.sign(privateKey);

        // Converter de volta para String
        return documentToString(doc);
    }

    /**
     * Encontra elemento por nome de tag e valor de atributo
     */
    private Element findElementByAttributeValue(Document doc, String tagName, String attrName, String attrValue) {
        NodeList elements = doc.getElementsByTagNameNS("*", tagName);

        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            String id = element.getAttribute(attrName);

            if (attrValue.equals(id)) {
                return element;
            }
        }

        // Se não encontrar com namespace, tenta sem namespace
        elements = doc.getElementsByTagName(tagName);
        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            String id = element.getAttribute(attrName);

            if (attrValue.equals(id)) {
                return element;
            }
        }

        return null;
    }

    /**
     * Converte Document para String
     */
    private String documentToString(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "no");
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }
}