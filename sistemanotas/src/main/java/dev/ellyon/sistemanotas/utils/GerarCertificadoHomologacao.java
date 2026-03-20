package dev.ellyon.sistemanotas.utils;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

public class GerarCertificadoHomologacao {
    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        // Gerar chaves
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        // Dados do certificado
        long now = System.currentTimeMillis();
        Date startDate = new Date(now);
        Date endDate = new Date(now + 365L * 24 * 60 * 60 * 1000); // 1 ano

        // Subject com CNPJ de teste SEFAZ
        // CNPJ: 11222333000181 (CNPJ de exemplo da SEFAZ para homologação)
        X500Name subject = new X500Name("CN=NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL:11222333000181");

        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());

        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
                subject,
                BigInteger.valueOf(now),
                startDate,
                endDate,
                subject,
                publicKeyInfo
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                .setProvider("BC")
                .build(keyPair.getPrivate());

        X509CertificateHolder certHolder = certBuilder.build(signer);
        X509Certificate cert = new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certHolder);

        // Criar KeyStore
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        keyStore.setKeyEntry(
                "certificado-homologacao",
                keyPair.getPrivate(),
                "123456".toCharArray(),
                new Certificate[]{cert}
        );

        // Salvar
        String caminho = "src/main/resources/certificados/certificado-homologacao.pfx";
        new java.io.File("src/main/resources/certificados/").mkdirs();

        FileOutputStream fos = new FileOutputStream(caminho);
        keyStore.store(fos, "123456".toCharArray());
        fos.close();

        System.out.println("✅ Certificado de homologação criado!");
        System.out.println("📁 Local: " + caminho);
        System.out.println("🔐 Senha: Fabrios12361236");
        System.out.println("🏢 CNPJ: 04642472096");
        System.out.println("\n⚠️ IMPORTANTE: Use o CNPJ 11222333000181 em todas as notas de teste!");
    }
}
