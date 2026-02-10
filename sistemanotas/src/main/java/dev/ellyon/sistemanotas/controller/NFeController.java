package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.Nota;
import dev.ellyon.sistemanotas.nfe.config.NFeConfig;
import dev.ellyon.sistemanotas.nfe.dto.CancelamentoNFeDTORequest;
import dev.ellyon.sistemanotas.nfe.dto.NFeRetornoDTO;
import dev.ellyon.sistemanotas.nfe.dto.NFeStatusDTO;
import dev.ellyon.sistemanotas.nfe.service.*;
import dev.ellyon.sistemanotas.nfe.xml.NFeXmlGenerator;
import dev.ellyon.sistemanotas.repository.NotaRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/nfe")
public class NFeController {

    private final ChaveAcessoService chaveAcessoService;
    private final CertificadoService certificadoService;
    private final DanfeService danfeService;
    private final NFeService nfeService;
    private final NFeXmlGenerator xmlGenerator;
    private final AssinaturaDigitalService assinaturaService;
    private final NotaRepository notaRepository;
    private final NFeConfig nfeConfig;
    public NFeController(ChaveAcessoService chaveAcessoService, CertificadoService certificadoService, DanfeService danfeService, NFeService nfeService, NFeXmlGenerator xmlGenerator, AssinaturaDigitalService assinaturaService, NotaRepository notaRepository, NFeConfig nfeConfig) {
        this.chaveAcessoService = chaveAcessoService;
        this.certificadoService = certificadoService;
        this.danfeService = danfeService;
        this.nfeService = nfeService;
        this.xmlGenerator = xmlGenerator;
        this.assinaturaService = assinaturaService;
        this.notaRepository = notaRepository;
        this.nfeConfig = nfeConfig;
    }

    /**
     * Consultar status do serviço SEFAZ
     */
    @GetMapping("/status-servico")
    public ResponseEntity<SuccessResponseDTO> consultarStatusServico() {
        try {
            NFeStatusDTO status = nfeService.consultarStatusServico();

            // ✅ Verificação segura contra null
            Boolean online = status.getOnline();
            String mensagem = (online != null && online)
                    ? "Serviço SEFAZ online"
                    : "Serviço SEFAZ offline";

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    mensagem,
                    status
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // ✅ Log completo do erro
            e.printStackTrace();

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro ao consultar status: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Emitir NF-e
     */
    @PostMapping("/emitir/{notaId}")
    public ResponseEntity<SuccessResponseDTO> emitir(@PathVariable Long notaId) {
        try {
            NFeRetornoDTO retorno = nfeService.emitir(notaId);

            String mensagem = "100".equals(retorno.getCodigoStatus())
                    ? "NF-e autorizada com sucesso!"
                    : "Erro ao autorizar NF-e";

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    mensagem,
                    retorno
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro ao emitir NF-e: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Cancelar NF-e autorizada
     */
    @PostMapping("/cancelar/{notaId}")
    public ResponseEntity<SuccessResponseDTO> cancelar(
            @PathVariable Long notaId,
            @RequestBody @Valid CancelamentoNFeDTORequest dto) {
        try {
            NFeRetornoDTO retorno = nfeService.cancelar(notaId, dto.getJustificativa());

            String mensagem = "135".equals(retorno.getCodigoStatus()) || "101".equals(retorno.getCodigoStatus())
                    ? "NF-e cancelada com sucesso!"
                    : "Erro ao cancelar NF-e";

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    mensagem,
                    retorno
            );
            return ResponseEntity.ok(response);
        } catch (ValidationException e) {
            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    e.getErrors()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (BusinessException e) {
            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro ao cancelar NF-e: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Gerar e baixar DANFE em PDF
     */
    @GetMapping("/{notaId}/danfe")
    public ResponseEntity<byte[]> downloadDanfe(@PathVariable Long notaId) {
        try {
            byte[] pdf = danfeService.gerar(notaId);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=DANFE_" + notaId + ".pdf")
                    .body(pdf);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erro ao gerar DANFE: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Visualizar DANFE (inline no navegador)
     */
    @GetMapping("/{notaId}/danfe/visualizar")
    public ResponseEntity<byte[]> visualizarDanfe(@PathVariable Long notaId) {
        try {
            byte[] pdf = danfeService.gerar(notaId);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "inline; filename=DANFE_" + notaId + ".pdf")
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(("Erro: " + e.getMessage()).getBytes());
        }
    }

}