package dev.ellyon.sistemanotas.nfe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para requisição de cancelamento de NF-e
 */
public class CancelamentoNFeDTORequest {

    @NotNull(message = "Justificativa é obrigatória")
    @NotBlank(message = "Justificativa não pode ser vazia")
    @Size(min = 15, max = 255, message = "Justificativa deve ter entre 15 e 255 caracteres")
    @JsonProperty("justificativa")
    private String justificativa;

    // Construtor padrão
    public CancelamentoNFeDTORequest() {
    }

    // Construtor completo
    public CancelamentoNFeDTORequest(String justificativa) {
        this.justificativa = justificativa;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    @Override
    public String toString() {
        return "CancelamentoNFeDTORequest{" +
                "justificativa='" + justificativa + '\'' +
                '}';
    }
}
