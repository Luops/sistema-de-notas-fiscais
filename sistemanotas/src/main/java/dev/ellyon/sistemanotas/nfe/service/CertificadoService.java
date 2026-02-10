package dev.ellyon.sistemanotas.nfe.service;

import dev.ellyon.sistemanotas.nfe.config.NFeConfig;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

@Service
public class CertificadoService {

    private final NFeConfig nfeConfig;
    private final ResourceLoader resourceLoader;

    private KeyStore keyStore;
    private PrivateKey privateKey;
    private X509Certificate certificate;

    public CertificadoService(NFeConfig nfeConfig, ResourceLoader resourceLoader) {
        this.nfeConfig = nfeConfig;
        this.resourceLoader = resourceLoader;

        // Adicionar provedor Bouncy Castle
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Carrega o certificado digital A1 (.pfx)
     */
    public void carregarCertificado() throws Exception {
        String caminho = nfeConfig.getCertificado().getCaminho();
        String senha = nfeConfig.getCertificado().getSenha();

        // Carregar KeyStore
        keyStore = KeyStore.getInstance("PKCS12");

        InputStream inputStream;
        if (caminho.startsWith("classpath:")) {
            Resource resource = resourceLoader.getResource(caminho);
            inputStream = resource.getInputStream();
        } else {
            inputStream = new FileInputStream(caminho);
        }

        keyStore.load(inputStream, senha.toCharArray());
        inputStream.close();

        // Obter alias do certificado
        Enumeration<String> aliases = keyStore.aliases();
        String alias = null;
        while (aliases.hasMoreElements()) {
            alias = aliases.nextElement();
            if (keyStore.isKeyEntry(alias)) {
                break;
            }
        }

        if (alias == null) {
            throw new Exception("Nenhum certificado encontrado no arquivo");
        }

        // Obter chave privada
        privateKey = (PrivateKey) keyStore.getKey(alias, senha.toCharArray());

        // Obter certificado
        certificate = (X509Certificate) keyStore.getCertificate(alias);
    }

    /**
     * Retorna a chave privada
     */
    public PrivateKey getPrivateKey() throws Exception {
        if (privateKey == null) {
            carregarCertificado();
        }
        return privateKey;
    }

    /**
     * Retorna o certificado X509
     */
    public X509Certificate getCertificate() throws Exception {
        if (certificate == null) {
            carregarCertificado();
        }
        return certificate;
    }

    /**
     * Retorna o CNPJ do certificado
     */
    public String getCNPJ() throws Exception {
        X509Certificate cert = getCertificate();
        String subject = cert.getSubjectDN().getName();

        // Extrair CNPJ do subject (formato: CN=Nome:CNPJ)
        String[] parts = subject.split(",");
        for (String part : parts) {
            if (part.trim().startsWith("CN=")) {
                String cn = part.substring(3).trim();
                if (cn.contains(":")) {
                    return cn.substring(cn.lastIndexOf(":") + 1).replaceAll("\\D", "");
                }
            }
        }

        throw new Exception("CNPJ não encontrado no certificado");
    }
}