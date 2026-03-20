package dev.ellyon.sistemanotas.nfe.service;

import dev.ellyon.sistemanotas.model.Empresa;
import dev.ellyon.sistemanotas.nfe.config.NFeConfig;
import dev.ellyon.sistemanotas.repository.EmpresaRepository;
import dev.ellyon.sistemanotas.service.CriptografiaService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

@Service
public class CertificadoService {
    private final EmpresaRepository empresaRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CriptografiaService criptografiaService;
    public CertificadoService(EmpresaRepository empresaRepository, BCryptPasswordEncoder passwordEncoder, CriptografiaService criptografiaService) {
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
        this.criptografiaService = criptografiaService;
    }

    /**
     * Carrega certificado de uma empresa específica
     */
    public CertificadoData carregarCertificadoDaEmpresa(Long empresaId) {

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        if (empresa.getCertificadoDigital() == null) {
            throw new RuntimeException("Empresa não possui certificado configurado");
        }

        if (!empresa.isCertificadoValido()) {
            throw new RuntimeException(
                    "Certificado da empresa está vencido. Validade até: " +
                            empresa.getCertificadoValidade()
            );
        }

        try {
            byte[] certificadoBytes = empresa.getCertificadoDigital();
            String senhaCriptografada = empresa.getCertificadoSenhaCriptografada();

            // ✅ DESCRIPTOGRAFAR SENHA COM AES-256
            String senha = criptografiaService.descriptografar(senhaCriptografada);

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new ByteArrayInputStream(certificadoBytes), senha.toCharArray());

            Enumeration<String> aliases = keyStore.aliases();
            String alias = aliases.nextElement();

            X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, senha.toCharArray());

            System.out.println("✅ Certificado carregado para empresa: " + empresa.getNomeFantasia());
            System.out.println("   CNPJ: " + empresa.getCertificadoCnpj());
            System.out.println("   Validade: " + empresa.getCertificadoValidade());

            return new CertificadoData(certificate, privateKey, empresa.getCertificadoCnpj());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar certificado: " + e.getMessage(), e);
        }
    }

    /**
     * Valida se empresa tem certificado válido
     */
    public boolean empresaTemCertificadoValido(Long empresaId) {
        try {
            Empresa empresa = empresaRepository.findById(empresaId).orElse(null);
            if (empresa == null) {
                return false;
            }
            return empresa.getCertificadoAtivo() && empresa.isCertificadoValido();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Classe interna para retornar dados do certificado
     */
    public static class CertificadoData {
        private final X509Certificate certificate;
        private final PrivateKey privateKey;
        private final String cnpj;

        public CertificadoData(X509Certificate certificate, PrivateKey privateKey, String cnpj) {
            this.certificate = certificate;
            this.privateKey = privateKey;
            this.cnpj = cnpj;
        }

        public X509Certificate getCertificate() { return certificate; }
        public PrivateKey getPrivateKey() { return privateKey; }
        public String getCnpj() { return cnpj; }
    }

    // Método antigo
    /*
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



    //Carrega o certificado digital A1 (.pfx)

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


    // Retorna a chave privada

    public PrivateKey getPrivateKey() throws Exception {
        if (privateKey == null) {
            carregarCertificado();
        }
        return privateKey;
    }


    // Retorna o certificado X509

    public X509Certificate getCertificate() throws Exception {
        if (certificate == null) {
            carregarCertificado();
        }
        return certificate;
    }


    // Retorna o CNPJ do certificado

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
    }*/
}