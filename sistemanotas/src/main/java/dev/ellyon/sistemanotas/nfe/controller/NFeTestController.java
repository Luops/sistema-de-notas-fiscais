package dev.ellyon.sistemanotas.nfe.controller;

import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.model.Nota;
import dev.ellyon.sistemanotas.nfe.config.NFeConfig;
import dev.ellyon.sistemanotas.nfe.service.*;
import dev.ellyon.sistemanotas.nfe.xml.NFeXmlGenerator;
import dev.ellyon.sistemanotas.repository.NotaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/nfe")
public class NFeTestController {

    private final ChaveAcessoService chaveAcessoService;
    private final CertificadoService certificadoService;
    private final SefazWebService sefazWebService;
    private final NFeXmlGenerator xmlGenerator;
    private final AssinaturaDigitalService assinaturaService;
    private final NotaRepository notaRepository;
    private final NFeConfig nfeConfig;
    public NFeTestController(ChaveAcessoService chaveAcessoService, CertificadoService certificadoService, SefazWebService sefazWebService, NFeXmlGenerator xmlGenerator, AssinaturaDigitalService assinaturaService, NotaRepository notaRepository, NFeConfig nfeConfig) {
        this.chaveAcessoService = chaveAcessoService;
        this.certificadoService = certificadoService;
        this.sefazWebService = sefazWebService;
        this.xmlGenerator = xmlGenerator;
        this.assinaturaService = assinaturaService;
        this.notaRepository = notaRepository;
        this.nfeConfig = nfeConfig;
    }

    /**
     * Teste 1: Gerar Chave de Acesso
     */
    @GetMapping("/test/chave-acesso")
    public ResponseEntity<SuccessResponseDTO> testarChaveAcesso() {
        try {
            String codigoNumerico = chaveAcessoService.gerarCodigoNumerico();
            String chave = chaveAcessoService.gerar(
                    "RS",                      // UF
                    LocalDateTime.now(),        // Data emissão
                    "12345678000190",          // CNPJ
                    "55",                      // Modelo NF-e
                    "1",                       // Série
                    "1",                       // Número
                    "1",                       // Tipo emissão
                    codigoNumerico             // Código numérico
            );

            Map<String, String> data = new HashMap<>();
            data.put("chaveAcesso", chave);
            data.put("chaveFormatada", chaveAcessoService.formatar(chave));
            data.put("codigoNumerico", codigoNumerico);
            data.put("tamanho", String.valueOf(chave.length()));

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    "Chave de acesso gerada com sucesso",
                    data
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            error.put("stack", e.getClass().getName());

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro ao gerar chave de acesso",
                    error
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Teste 2: Carregar Certificado Digital
     */
    @GetMapping("/test/certificado/{empresaId}")
    public ResponseEntity<SuccessResponseDTO> testarCertificado(@PathVariable Long empresaId) {
        try {
            // ✅ Carregar certificado da empresa específica
            CertificadoService.CertificadoData certificado =
                    certificadoService.carregarCertificadoDaEmpresa(empresaId);

            Map<String, Object> data = new HashMap<>();
            data.put("cnpj", certificado.getCnpj());
            data.put("status", "Certificado carregado com sucesso");
            data.put("empresaId", empresaId);

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    "Certificado válido",
                    data
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            error.put("stack", e.getClass().getName());

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro ao carregar certificado",
                    error
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Teste 3: Gerar XML da NF-e (sem assinatura)
     */
    @GetMapping(value = "/test/gerar-xml/{notaId}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> testarGeracaoXml(@PathVariable Long notaId) {
        try {
            Nota nota = notaRepository.findById(notaId)
                    .orElseThrow(() -> new Exception("Nota não encontrada com ID: " + notaId));

            String codigoNumerico = chaveAcessoService.gerarCodigoNumerico();
            String chaveAcesso = chaveAcessoService.gerar(
                    nfeConfig.getEmitente().getUf(),
                    nota.getDataEmissao(),
                    nota.getEmpresa().getCnpj(),
                    "55",
                    nota.getSerie(),
                    nota.getNumero(),
                    "1",
                    codigoNumerico
            );

            String xml = xmlGenerator.gerar(nota, chaveAcesso);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/xml; charset=UTF-8")
                    .header("X-Chave-Acesso", chaveAcesso)
                    .body(xml);
        } catch (Exception e) {
            String errorXml = String.format(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<erro>\n" +
                            "  <mensagem>%s</mensagem>\n" +
                            "  <tipo>%s</tipo>\n" +
                            "</erro>",
                    e.getMessage(),
                    e.getClass().getName()
            );
            return ResponseEntity.status(500)
                    .header("Content-Type", "application/xml")
                    .body(errorXml);
        }
    }

    /**
     * Teste 4: Gerar XML e Assinar
     */
    @GetMapping(value = "/test/gerar-xml-assinado/{notaId}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> testarXmlAssinado(@PathVariable Long notaId) {
        try {
            Nota nota = notaRepository.findById(notaId)
                    .orElseThrow(() -> new Exception("Nota não encontrada com ID: " + notaId));

            String codigoNumerico = chaveAcessoService.gerarCodigoNumerico();
            String chaveAcesso = chaveAcessoService.gerar(
                    nfeConfig.getEmitente().getUf(),
                    nota.getDataEmissao(),
                    nota.getEmpresa().getCnpj(),
                    "55",
                    nota.getSerie(),
                    nota.getNumero(),
                    "1",
                    codigoNumerico
            );

            String xmlSemAssinatura = xmlGenerator.gerar(nota, chaveAcesso);
            Long empresaId = nota.getEmpresa().getId();
            String xmlAssinado = assinaturaService.assinar(empresaId, xmlSemAssinatura, "NFe" + chaveAcesso);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/xml; charset=UTF-8")
                    .header("X-Chave-Acesso", chaveAcesso)
                    .header("X-Assinado", "true")
                    .body(xmlAssinado);
        } catch (Exception e) {
            String errorXml = String.format(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<erro>\n" +
                            "  <mensagem>%s</mensagem>\n" +
                            "  <tipo>%s</tipo>\n" +
                            "</erro>",
                    e.getMessage(),
                    e.getClass().getName()
            );
            return ResponseEntity.status(500)
                    .header("Content-Type", "application/xml")
                    .body(errorXml);
        }
    }

    /**
     * Teste 5: Verificar Configurações
     */
    @GetMapping("/test/configuracoes")
    public ResponseEntity<SuccessResponseDTO> testarConfiguracoes() {
        Map<String, Object> data = new HashMap<>();

        // Configurações gerais
        data.put("ambiente", nfeConfig.getAmbiente() == 1 ? "PRODUÇÃO" : "HOMOLOGAÇÃO");
        data.put("uf", nfeConfig.getEmitente().getUf());
        data.put("codigoMunicipio", nfeConfig.getEmitente().getCodigoMunicipio());

        // Certificado
        Map<String, String> cert = new HashMap<>();
        cert.put("tipo", nfeConfig.getCertificado().getTipo());
        cert.put("caminho", nfeConfig.getCertificado().getCaminho());
        cert.put("senhaCofigurada", nfeConfig.getCertificado().getSenha() != null ? "Sim" : "Não");
        data.put("certificado", cert);

        // URLs SEFAZ
        Map<String, String> urls = new HashMap<>();
        urls.put("autorizacao", nfeConfig.getSefaz().getUrlAutorizacao());
        urls.put("retornoAutorizacao", nfeConfig.getSefaz().getUrlRetornoAutorizacao());
        urls.put("statusServico", nfeConfig.getSefaz().getUrlStatusServico());
        urls.put("cancelamento", nfeConfig.getSefaz().getUrlCancelamento());
        data.put("urls", urls);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Configurações carregadas",
                data
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Teste 6: Listar Notas Disponíveis
     */
    @GetMapping("/test/listar-notas")
    public ResponseEntity<SuccessResponseDTO> listarNotas() {
        try {
            var notas = notaRepository.findAll();

            Map<String, Object> data = new HashMap<>();
            data.put("total", notas.size());
            data.put("notas", notas.stream().map(n -> {
                Map<String, Object> notaInfo = new HashMap<>();
                notaInfo.put("id", n.getId());
                notaInfo.put("numero", n.getNumero());
                notaInfo.put("serie", n.getSerie());
                notaInfo.put("status", n.getStatus());
                notaInfo.put("valorTotal", n.getValorTotal());
                notaInfo.put("empresa", n.getEmpresa().getNomeFantasia());
                notaInfo.put("cliente", n.getCliente() != null ? n.getCliente().getNome() : "Sem cliente");
                notaInfo.put("itens", n.getItens().size());
                return notaInfo;
            }).toList());

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    "Notas listadas com sucesso",
                    data
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro ao listar notas",
                    error
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Teste 7: Validar Nota para Emissão
     */
    @GetMapping("/test/validar-nota/{notaId}")
    public ResponseEntity<SuccessResponseDTO> validarNota(@PathVariable Long notaId) {
        try {
            Nota nota = notaRepository.findById(notaId)
                    .orElseThrow(() -> new Exception("Nota não encontrada"));

            Map<String, Object> validacao = new HashMap<>();
            Map<String, Boolean> checks = new HashMap<>();

            // Validações
            checks.put("notaExiste", true);
            checks.put("temEmpresa", nota.getEmpresa() != null);
            checks.put("temCliente", nota.getCliente() != null);
            checks.put("temItens", !nota.getItens().isEmpty());
            checks.put("temNumero", nota.getNumero() != null && !nota.getNumero().isBlank());
            checks.put("temSerie", nota.getSerie() != null && !nota.getSerie().isBlank());
            checks.put("valorPositivo", nota.getValorTotal().compareTo(java.math.BigDecimal.ZERO) > 0);

            boolean valida = checks.values().stream().allMatch(v -> v);

            validacao.put("valida", valida);
            validacao.put("checks", checks);
            validacao.put("nota", Map.of(
                    "id", nota.getId(),
                    "numero", nota.getNumero(),
                    "serie", nota.getSerie(),
                    "status", nota.getStatus(),
                    "valorTotal", nota.getValorTotal()
            ));

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    valida ? "Nota válida para emissão" : "Nota inválida - verifique os checks",
                    validacao
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Erro ao validar nota",
                    error
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Teste: Ver XML antes e depois da assinatura
     */
    @GetMapping("/test/debug-assinatura/{notaId}")
    public ResponseEntity<SuccessResponseDTO> debugAssinatura(@PathVariable Long notaId) {
        try {
            Nota nota = notaRepository.findById(notaId)
                    .orElseThrow(() -> new Exception("Nota não encontrada"));

            if (nota.getDataEmissao() == null) {
                nota.setDataEmissao(java.time.LocalDateTime.now());
            }

            // Gerar chave de acesso
            String codigoNumerico = chaveAcessoService.gerarCodigoNumerico();
            String chaveAcesso = chaveAcessoService.gerar(
                    nfeConfig.getEmitente().getUf(),
                    nota.getDataEmissao(),
                    nota.getEmpresa().getCnpj(),
                    "55",
                    nota.getSerie(),
                    nota.getNumero(),
                    "1",
                    codigoNumerico
            );

            // Gerar XML sem assinatura
            String xmlSemAssinatura = xmlGenerator.gerar(nota, chaveAcesso);

            // Tentar assinar
            String xmlAssinado;
            String erro = null;
            try {
                Long empresaId = nota.getEmpresa().getId();
                xmlAssinado = assinaturaService.assinar(empresaId, xmlSemAssinatura, "NFe" + chaveAcesso);
            } catch (Exception e) {
                xmlAssinado = null;
                erro = e.getMessage();
            }

            Map<String, Object> data = new HashMap<>();
            data.put("chaveAcesso", chaveAcesso);
            data.put("idElemento", "NFe" + chaveAcesso);
            data.put("xmlSemAssinatura", xmlSemAssinatura);
            data.put("xmlAssinado", xmlAssinado);
            data.put("erro", erro);
            data.put("temAssinatura", xmlAssinado != null && xmlAssinado.contains("<Signature"));

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    erro != null ? "Erro ao assinar" : "Assinatura OK",
                    data
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Erro no debug",
                    error
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}