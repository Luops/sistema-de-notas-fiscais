package dev.ellyon.sistemanotas.utils;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import javax.security.auth.x500.X500Principal;

public class CertificadoTestGenerator {

    public static void main(String[] args) throws Exception {
        // Adicionar provedor Bouncy Castle
        Security.addProvider(new BouncyCastleProvider());

        // Gerar par de chaves RSA
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Informações do certificado
        long now = System.currentTimeMillis();
        Date startDate = new Date(now);
        Date endDate = new Date(now + 365L * 24 * 60 * 60 * 1000); // 1 ano

        // Subject e Issuer (mesmo para auto-assinado)
        // Formato: CN=Nome:CNPJ
        X500Name subject = new X500Name("CN=Empresa Teste NF-e:12345678000190");

        // Criar certificado
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());

        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
                subject,                                    // Issuer
                BigInteger.valueOf(now),                   // Serial Number
                startDate,                                  // Not Before
                endDate,                                    // Not After
                subject,                                    // Subject
                publicKeyInfo                              // Public Key
        );

        // Assinar certificado
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                .setProvider("BC")
                .build(keyPair.getPrivate());

        X509CertificateHolder certHolder = certBuilder.build(signer);
        X509Certificate cert = new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certHolder);

        // Criar KeyStore PKCS12
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);

        // Adicionar chave privada e certificado
        keyStore.setKeyEntry(
                "certificado-teste",
                keyPair.getPrivate(),
                "senha123".toCharArray(),
                new Certificate[]{cert}
        );

        // Salvar em arquivo
        String caminho = "src/main/resources/certificados/certificado.pfx";

        // Criar diretório se não existir
        new java.io.File("src/main/resources/certificados/").mkdirs();

        FileOutputStream fos = new FileOutputStream(caminho);
        keyStore.store(fos, "senha123".toCharArray());
        fos.close();

        System.out.println("✅ Certificado criado com sucesso!");
        System.out.println("📁 Local: " + caminho);
        System.out.println("🔐 Senha: Fabrios12361236");
        System.out.println("🏢 CNPJ: 04642472096");
    }
}