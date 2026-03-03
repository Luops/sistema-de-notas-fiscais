package dev.ellyon.sistemanotas.dto.empresa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CertificadoUploadDTO {

    @NotNull(message = "Arquivo do certificado é obrigatório")
    private MultipartFile arquivo;  // Arquivo .pfx

    @NotBlank(message = "Senha do certificado é obrigatória")
    @Size(min = 4, max = 50, message = "Senha deve ter entre 4 e 50 caracteres")
    private String senha;  // Senha do certificado

    @NotBlank(message = "Tipo do certificado é obrigatório")
    private String tipo = "A1";  // A1 ou A3
}