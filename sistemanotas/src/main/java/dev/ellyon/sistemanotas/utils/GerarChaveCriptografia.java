package dev.ellyon.sistemanotas.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class GerarChaveCriptografia {

    public static void main(String[] args) {
        System.out.println("=== GERADOR DE CHAVE AES-256 ===\n");

        // Gerar chave aleatória de 32 bytes (256 bits)
        SecureRandom random = new SecureRandom();
        byte[] chaveBytes = new byte[32];
        random.nextBytes(chaveBytes);

        // Converter para string Base64
        String chaveBase64 = Base64.getEncoder().encodeToString(chaveBytes);

        // Pegar apenas os primeiros 32 caracteres
        String chave32 = chaveBase64.substring(0, 32);

        System.out.println("✅ Chave gerada com sucesso!");
        System.out.println("\nChave (32 caracteres):");
        System.out.println(chave32);
        System.out.println("\n⚠️ GUARDE ESTA CHAVE EM LOCAL SEGURO!");
        System.out.println("⚠️ ADICIONE NO .env: ENCRYPTION_KEY=" + chave32);
        System.out.println("⚠️ NUNCA COMMITE ESTA CHAVE NO GIT!");

        // Validar tamanho
        System.out.println("\nTamanho: " + chave32.length() + " caracteres");
        System.out.println("Encoding: UTF-8");
        System.out.println("Algoritmo: AES-256-CBC");
    }
}