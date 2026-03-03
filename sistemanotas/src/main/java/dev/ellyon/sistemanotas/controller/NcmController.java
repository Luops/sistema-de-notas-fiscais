package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.service.NcmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ncm")
public class NcmController {

    private final NcmService ncmService;

    public NcmController(NcmService ncmService) {
        this.ncmService = ncmService;
    }

    /**
     * Consultar dados completos do NCM
     */
    @GetMapping("/consultar/{ncm}")
    public ResponseEntity<SuccessResponseDTO> consultarNCM(@PathVariable String ncm) {
        try {
            Map<String, Object> dados = ncmService.consultarNCM(ncm);

            if (dados.containsKey("erro")) {
                SuccessResponseDTO response = new SuccessResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        (String) dados.get("erro"),
                        null
                );
                return ResponseEntity.badRequest().body(response);
            }

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    "NCM consultado com sucesso",
                    dados
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro ao consultar NCM: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Buscar alíquotas sugeridas para um NCM
     */
    @GetMapping("/aliquotas/{ncm}")
    public ResponseEntity<SuccessResponseDTO> buscarAliquotas(@PathVariable String ncm) {
        try {
            Map<String, java.math.BigDecimal> aliquotas = ncmService.buscarAliquotasSugeridas(ncm);

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    "Alíquotas sugeridas",
                    aliquotas
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro ao buscar alíquotas: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}