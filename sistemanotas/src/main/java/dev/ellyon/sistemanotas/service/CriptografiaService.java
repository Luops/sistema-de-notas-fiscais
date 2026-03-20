package dev.ellyon.sistemanotas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CriptografiaService {

    private static final String ALGORITMO = "AES/CBC/PKCS5Padding";
    private static final String ALGORITMO_CHAVE = "AES";
    private static final int TAMANHO_IV = 16; // 128 bits

    @Value("${security.encryption.key}")
    private String chaveSecreta;

    /**
     * Criptografa uma string usando AES-256
     * Formato: IV (16 bytes) + Dados Criptografados
     */
    public String criptografar(String texto) {
        try {
            // Gerar IV aleatório (Initialization Vector)
            byte[] iv = new byte[TAMANHO_IV];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Criar chave a partir da chave secreta
            SecretKeySpec keySpec = criarChave();

            // Configurar cipher
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            // Criptografar
            byte[] textoCriptografado = cipher.doFinal(texto.getBytes(StandardCharsets.UTF_8));

            // Combinar IV + dados criptografados
            byte[] resultado = new byte[TAMANHO_IV + textoCriptografado.length];
            System.arraycopy(iv, 0, resultado, 0, TAMANHO_IV);
            System.arraycopy(textoCriptografado, 0, resultado, TAMANHO_IV, textoCriptografado.length);

            // Retornar em Base64
            return Base64.getEncoder().encodeToString(resultado);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar: " + e.getMessage(), e);
        }
    }

    /**
     * Descriptografa uma string criptografada com AES-256
     */
    public String descriptografar(String textoCriptografado) {
        try {
            // Decodificar Base64
            byte[] dados = Base64.getDecoder().decode(textoCriptografado);

            // Extrair IV (primeiros 16 bytes)
            byte[] iv = new byte[TAMANHO_IV];
            System.arraycopy(dados, 0, iv, 0, TAMANHO_IV);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Extrair dados criptografados (resto)
            byte[] dadosCriptografados = new byte[dados.length - TAMANHO_IV];
            System.arraycopy(dados, TAMANHO_IV, dadosCriptografados, 0, dadosCriptografados.length);

            // Criar chave
            SecretKeySpec keySpec = criarChave();

            // Configurar cipher
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            // Descriptografar
            byte[] textoDescriptografado = cipher.doFinal(dadosCriptografados);

            return new String(textoDescriptografado, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar: " + e.getMessage(), e);
        }
    }

    /**
     * Cria chave AES de 256 bits a partir da chave secreta
     */
    private SecretKeySpec criarChave() {
        try {
            // A chave precisa ter exatamente 32 caracteres (256 bits)
            byte[] chaveBytes = chaveSecreta.getBytes(StandardCharsets.UTF_8);

            if (chaveBytes.length != 32) {
                throw new IllegalArgumentException(
                        "Chave de criptografia deve ter exatamente 32 caracteres (256 bits). " +
                                "Tamanho atual: " + chaveBytes.length
                );
            }

            return new SecretKeySpec(chaveBytes, ALGORITMO_CHAVE);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar chave de criptografia: " + e.getMessage(), e);
        }
    }

    /**
     * Valida se a chave de criptografia está configurada corretamente
     */
    public boolean validarConfiguracao() {
        try {
            if (chaveSecreta == null || chaveSecreta.isEmpty()) {
                System.err.println("⚠️ ERRO: Chave de criptografia não configurada!");
                return false;
            }

            if (chaveSecreta.length() != 32) {
                System.err.println("⚠️ ERRO: Chave de criptografia deve ter 32 caracteres!");
                System.err.println("   Tamanho atual: " + chaveSecreta.length());
                return false;
            }

            // Testar criptografia/descriptografia
            String teste = "teste_criptografia_123";
            String criptografado = criptografar(teste);
            String descriptografado = descriptografar(criptografado);

            if (!teste.equals(descriptografado)) {
                System.err.println("⚠️ ERRO: Falha no teste de criptografia/descriptografia!");
                return false;
            }

            System.out.println("✅ Criptografia AES-256 configurada corretamente");
            return true;

        } catch (Exception e) {
            System.err.println("⚠️ ERRO ao validar criptografia: " + e.getMessage());
            return false;
        }
    }
}