package dev.ellyon.sistemanotas.dto.nfe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespostaSefazDTO {
    private String protocolo;
    private String status;
    private String statusDescricao;
    private String xmlRetorno;
    private String erro;
    private boolean autorizado;
}
