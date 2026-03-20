package dev.ellyon.sistemanotas.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Converter
@Component
public class CpfCnpjEncryptor implements AttributeConverter<String, String> {

    private final AESEncryptionService encryptionService;

    @Autowired
    public CpfCnpjEncryptor(AESEncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    @Override
    public String convertToDatabaseColumn(String cpfCnpj) {
        if (cpfCnpj == null || cpfCnpj.isEmpty()) {
            return null;
        }
        try {
            // Criptografa antes de salvar no banco
            return encryptionService.encrypt(cpfCnpj);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar CPF/CNPJ", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String encryptedCpfCnpj) {
        if (encryptedCpfCnpj == null || encryptedCpfCnpj.isEmpty()) {
            return null;
        }
        try {
            // Descriptografa ao ler do banco
            return encryptionService.decrypt(encryptedCpfCnpj);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar CPF/CNPJ", e);
        }
    }
}