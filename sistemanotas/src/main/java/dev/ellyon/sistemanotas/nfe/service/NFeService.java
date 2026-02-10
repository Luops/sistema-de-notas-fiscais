package dev.ellyon.sistemanotas.nfe.service;

import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.Nota;
import dev.ellyon.sistemanotas.model.enums.StatusNota;
import dev.ellyon.sistemanotas.nfe.config.NFeConfig;
import dev.ellyon.sistemanotas.nfe.dto.NFeRetornoDTO;
import dev.ellyon.sistemanotas.nfe.dto.NFeStatusDTO;
import dev.ellyon.sistemanotas.nfe.util.NFeConstants;
import dev.ellyon.sistemanotas.nfe.xml.NFeXmlGenerator;
import dev.ellyon.sistemanotas.repository.NotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class NFeService {

    private final NotaRepository notaRepository;
    private final NFeConfig nfeConfig;
    private final ChaveAcessoService chaveAcessoService;
    private final NFeXmlGenerator xmlGenerator;
    private final AssinaturaDigitalService assinaturaService;
    private final SefazWebService sefazWebService;

    public NFeService(NotaRepository notaRepository,
                      NFeConfig nfeConfig,
                      ChaveAcessoService chaveAcessoService,
                      NFeXmlGenerator xmlGenerator,
                      AssinaturaDigitalService assinaturaService,
                      SefazWebService sefazWebService) {
        this.notaRepository = notaRepository;
        this.nfeConfig = nfeConfig;
        this.chaveAcessoService = chaveAcessoService;
        this.xmlGenerator = xmlGenerator;
        this.assinaturaService = assinaturaService;
        this.sefazWebService = sefazWebService;
    }

    /**
     * Valida se a nota tem todos os dados necessários para emissão
     */
    private void validarNotaParaEmissao(Nota nota) throws BusinessException {
        StringBuilder erros = new StringBuilder();

        if (nota.getEmpresa() == null) {
            erros.append("- Nota sem empresa vinculada\n");
        }

        if (nota.getCliente() == null) {
            erros.append("- Nota sem cliente/destinatário vinculado\n");
        }

        if (nota.getItens() == null || nota.getItens().isEmpty()) {
            erros.append("- Nota sem itens/produtos\n");
        }

        if (nota.getNumero() == null || nota.getNumero().isBlank()) {
            erros.append("- Nota sem número\n");
        }

        if (nota.getSerie() == null || nota.getSerie().isBlank()) {
            erros.append("- Nota sem série\n");
        }

        if (nota.getValorTotal().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            erros.append("- Valor total deve ser maior que zero\n");
        }

        if (erros.length() > 0) {
            throw new BusinessException("Nota inválida para emissão:\n" + erros.toString());
        }
    }

    /**
     * Emite uma NF-e (processo completo)
     */
    public NFeRetornoDTO emitir(Long notaId) throws Exception {
        // 1. Buscar nota
        Nota nota = notaRepository.findById(notaId)
                .orElseThrow(() -> new BusinessException("Nota não encontrada"));

        // 2. Validar se já foi emitida
        if (nota.getStatus() == StatusNota.EMITIDA) {
            throw new BusinessException("Nota já foi emitida anteriormente. Chave de acesso: " + nota.getChaveAcesso());
        }

        // 3. Validar se está cancelada
        if (nota.getStatus() == StatusNota.CANCELADA) {
            throw new BusinessException("Não é possível emitir nota cancelada");
        }

        // ✅ 4. Se dataEmissao for null, definir agora
        if (nota.getDataEmissao() == null) {
            nota.setDataEmissao(LocalDateTime.now());
        }

        // ✅ 5. Validar dados obrigatórios
        validarNotaParaEmissao(nota);

        // 6. Gerar chave de acesso
        String codigoNumerico = chaveAcessoService.gerarCodigoNumerico();
        String chaveAcesso = chaveAcessoService.gerar(
                nfeConfig.getEmitente().getUf(),
                nota.getDataEmissao(),
                nota.getEmpresa().getCnpj(),
                NFeConstants.MODELO_NFE,
                nota.getSerie(),
                nota.getNumero(),
                NFeConstants.TIPO_EMISSAO_NORMAL,
                codigoNumerico
        );

        // 7. Gerar XML da NF-e
        String xmlSemAssinatura = xmlGenerator.gerar(nota, chaveAcesso);

        // 8. Assinar XML
        String xmlAssinado = assinaturaService.assinar(xmlSemAssinatura, "NFe" + chaveAcesso);

        // 9. Enviar para SEFAZ
        String respostaAutorizacao = sefazWebService.autorizarNFe(xmlAssinado, chaveAcesso);

        // 10. Processar retorno
        NFeRetornoDTO retorno = processarRetornoAutorizacao(respostaAutorizacao);

        // 11. Atualizar nota
        if ("100".equals(retorno.getCodigoStatus())) {
            nota.setStatus(StatusNota.EMITIDA);
            nota.setChaveAcesso(chaveAcesso);
            nota.setProtocoloAutorizacao(retorno.getProtocolo());
            nota.setXmlNfe(xmlAssinado);
            notaRepository.save(nota);
        }

        retorno.setChaveAcesso(chaveAcesso);
        return retorno;
    }

    /**
     * Cancela uma NF-e autorizada
     */
    public NFeRetornoDTO cancelar(Long notaId, String justificativa) throws Exception {
        Map<String, String> errors = new HashMap<>();

        // Validar justificativa
        if (justificativa == null || justificativa.trim().length() < 15) {
            errors.put("justificativa", "Justificativa deve ter no mínimo 15 caracteres");
        }

        if (justificativa != null && justificativa.length() > 255) {
            errors.put("justificativa", "Justificativa deve ter no máximo 255 caracteres");
        }

        // Buscar nota
        Nota nota = notaRepository.findById(notaId)
                .orElseGet(() -> {
                    errors.put("notaId", "Nota não encontrada");
                    return null;
                });

        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação", errors);
        }

        // ========================================
        // 2. VALIDAR STATUS DA NOTA
        // ========================================
        if (nota.getStatus() != StatusNota.EMITIDA) {
            throw new BusinessException("Apenas notas emitidas podem ser canceladas. Status atual: " + nota.getStatus());
        }

        if (nota.getStatus() == StatusNota.CANCELADA) {
            throw new BusinessException("Nota já está cancelada");
        }

        // Validar se tem chave de acesso
        if (nota.getChaveAcesso() == null || nota.getChaveAcesso().isBlank()) {
            throw new BusinessException("Nota não possui chave de acesso. Emita a NF-e primeiro.");
        }

        // Validar se tem protocolo
        if (nota.getProtocoloAutorizacao() == null || nota.getProtocoloAutorizacao().isBlank()) {
            throw new BusinessException("Nota não possui protocolo de autorização");
        }

        // ========================================
        // 3. VALIDAR PRAZO DE CANCELAMENTO (24 horas)
        // ========================================
        if (nota.getDataEmissao() != null) {
            LocalDateTime prazoLimite = nota.getDataEmissao().plusHours(24);
            if (LocalDateTime.now().isAfter(prazoLimite)) {
                throw new BusinessException(
                        String.format("Prazo de cancelamento expirado. Limite era: %s",
                                prazoLimite.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                );
            }
        }

        // ========================================
        // 4. ENVIAR CANCELAMENTO PARA SEFAZ
        // ========================================
        String respostaCancelamento = sefazWebService.cancelarNFe(
                nota.getChaveAcesso(),
                nota.getProtocoloAutorizacao(),
                justificativa
        );

        // ========================================
        // 5. PROCESSAR RETORNO
        // ========================================
        NFeRetornoDTO retorno = processarRetornoCancelamento(respostaCancelamento);

        // ========================================
        // 6. ATUALIZAR NOTA (se cancelamento aprovado)
        // ========================================
        // Código 135 = Evento registrado e vinculado a NF-e
        // Código 101 = Cancelamento homologado (pode aparecer em alguns casos)
        if ("135".equals(retorno.getCodigoStatus()) || "101".equals(retorno.getCodigoStatus())) {
            nota.setStatus(StatusNota.CANCELADA);
            nota.setProtocoloCancelamento(retorno.getProtocolo());
            nota.setJustificativaCancelamento(justificativa);
            notaRepository.save(nota);
        }

        retorno.setChaveAcesso(nota.getChaveAcesso());
        return retorno;
    }

    /**
     * Consulta o status do serviço da SEFAZ
     */
    public NFeStatusDTO consultarStatusServico() throws Exception {
        String resposta = sefazWebService.consultarStatusServico();
        return processarStatusServico(resposta);
    }

    /**
     * Processa retorno da autorização
     */
    private NFeRetornoDTO processarRetornoAutorizacao(String xmlResposta) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlResposta.getBytes("UTF-8")));

        NFeRetornoDTO retorno = new NFeRetornoDTO();

        // Extrair código de status
        NodeList cStatList = doc.getElementsByTagName("cStat");
        if (cStatList.getLength() > 0) {
            retorno.setCodigoStatus(cStatList.item(0).getTextContent());
        }

        // Extrair mensagem
        NodeList xMotivoList = doc.getElementsByTagName("xMotivo");
        if (xMotivoList.getLength() > 0) {
            retorno.setMensagem(xMotivoList.item(0).getTextContent());
        }

        // Extrair protocolo (se autorizada)
        NodeList nProtList = doc.getElementsByTagName("nProt");
        if (nProtList.getLength() > 0) {
            retorno.setProtocolo(nProtList.item(0).getTextContent());
        }

        // Extrair data/hora de autorização
        NodeList dhRecbtoList = doc.getElementsByTagName("dhRecbto");
        if (dhRecbtoList.getLength() > 0) {
            retorno.setDataHoraAutorizacao(dhRecbtoList.item(0).getTextContent());
        }

        retorno.setXmlResposta(xmlResposta);

        return retorno;
    }

    /**
     * Processa retorno do cancelamento
     */
    private NFeRetornoDTO processarRetornoCancelamento(String xmlResposta) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlResposta.getBytes("UTF-8")));

        NFeRetornoDTO retorno = new NFeRetornoDTO();

        // Extrair código de status do evento
        NodeList cStatList = doc.getElementsByTagName("cStat");
        if (cStatList.getLength() > 0) {
            // Pegar o último cStat (normalmente é o do evento)
            retorno.setCodigoStatus(cStatList.item(cStatList.getLength() - 1).getTextContent());
        }

        // Extrair mensagem
        NodeList xMotivoList = doc.getElementsByTagName("xMotivo");
        if (xMotivoList.getLength() > 0) {
            retorno.setMensagem(xMotivoList.item(xMotivoList.getLength() - 1).getTextContent());
        }

        // Extrair protocolo do evento
        NodeList nProtList = doc.getElementsByTagName("nProt");
        if (nProtList.getLength() > 0) {
            retorno.setProtocolo(nProtList.item(nProtList.getLength() - 1).getTextContent());
        }

        // Extrair data/hora do registro do evento
        NodeList dhRegEventoList = doc.getElementsByTagName("dhRegEvento");
        if (dhRegEventoList.getLength() > 0) {
            retorno.setDataHoraAutorizacao(dhRegEventoList.item(0).getTextContent());
        }

        retorno.setXmlResposta(xmlResposta);

        return retorno;
    }

    /**
     * Processa status do serviço
     */
    private NFeStatusDTO processarStatusServico(String xmlResposta) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlResposta.getBytes("UTF-8")));

        NFeStatusDTO status = new NFeStatusDTO();

        // Extrair código de status
        NodeList cStatList = doc.getElementsByTagName("cStat");
        if (cStatList.getLength() > 0) {
            String cStat = cStatList.item(0).getTextContent();
            status.setCodigoStatus(cStat);

            // ✅ Definir se está online baseado no código
            // Código 107 = Serviço em Operação
            status.setOnline("107".equals(cStat));
        } else {
            // ✅ Se não encontrar cStat, marcar como offline
            status.setOnline(false);
            status.setCodigoStatus("000");
        }

        // Extrair mensagem
        NodeList xMotivoList = doc.getElementsByTagName("xMotivo");
        if (xMotivoList.getLength() > 0) {
            status.setMensagem(xMotivoList.item(0).getTextContent());
        } else {
            status.setMensagem("Resposta sem mensagem");
        }

        return status;
    }
}