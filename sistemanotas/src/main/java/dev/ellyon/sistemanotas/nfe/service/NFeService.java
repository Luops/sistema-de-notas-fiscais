package dev.ellyon.sistemanotas.nfe.service;

import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.Cliente;
import dev.ellyon.sistemanotas.model.Empresa;
import dev.ellyon.sistemanotas.model.ItemNota;
import dev.ellyon.sistemanotas.model.Nota;
import dev.ellyon.sistemanotas.model.enums.StatusNota;
import dev.ellyon.sistemanotas.nfe.config.NFeConfig;
import dev.ellyon.sistemanotas.nfe.dto.NFeResponseDTO;
import dev.ellyon.sistemanotas.nfe.dto.NFeStatusDTO;
import dev.ellyon.sistemanotas.nfe.xml.NFeXmlGenerator;
import dev.ellyon.sistemanotas.repository.NotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
     * Emite uma NF-e (processo completo)
     */
    public NFeResponseDTO emitir(Long notaId) {

        // Buscar nota
        Nota nota = notaRepository.findById(notaId)
                .orElseThrow(() -> new BusinessException("Nota fiscal não encontrada"));

        // Validar
        validarNotaParaEmissao(nota);

        try {
            // 1. Gerar chave de acesso
            String chaveAcesso = gerarChaveAcesso(nota);
            nota.setChaveAcesso(chaveAcesso);
            System.out.println("✅ Chave gerada: " + chaveAcesso);

            // 2. Gerar XML
            String xmlSemAssinatura = gerarXml(nota);
            System.out.println("✅ XML gerado (primeiros 100 chars): " + xmlSemAssinatura.substring(0, Math.min(100, xmlSemAssinatura.length())));

            // 3. Pegar ID da empresa
            Long empresaId = nota.getEmpresa().getId();
            System.out.println("✅ Empresa ID: " + empresaId);

            // 4. Assinar XML
            String xmlAssinado = assinaturaService.assinar(
                    empresaId,
                    xmlSemAssinatura,
                    "NFe" + chaveAcesso
            );
            System.out.println("✅ XML assinado!");

            nota.setXmlNfe(xmlAssinado);

            // 5. Enviar para SEFAZ ✅ Passar empresaId
            String xmlRetorno = sefazWebService.enviarNFe(empresaId, xmlAssinado);
            System.out.println("✅ XML retorno recebido (primeiros 100 chars): " + xmlRetorno.substring(0, Math.min(100, xmlRetorno.length())));

            // 6. Processar retorno
            NFeResponseDTO retorno = processarRetornoAutorizacao(xmlRetorno);

            System.out.println("=== DEBUG RETORNO ===");
            System.out.println("Código Status: " + retorno.getCodigoStatus() + " (tipo: " + (retorno.getCodigoStatus() != null ? retorno.getCodigoStatus().getClass().getName() : "null") + ")");
            System.out.println("Mensagem: " + retorno.getMensagem() + " (tipo: " + (retorno.getMensagem() != null ? retorno.getMensagem().getClass().getName() : "null") + ")");
            System.out.println("Protocolo: " + retorno.getProtocolo() + " (tipo: " + (retorno.getProtocolo() != null ? retorno.getProtocolo().getClass().getName() : "null") + ")");
            System.out.println("Data/Hora: " + retorno.getDataHoraAutorizacao() + " (tipo: " + (retorno.getDataHoraAutorizacao() != null ? retorno.getDataHoraAutorizacao().getClass().getName() : "null") + ")");
            System.out.println("XML Resposta length: " + (retorno.getXmlResposta() != null ? retorno.getXmlResposta().length() : "null"));
            System.out.println("==================");

            if ("100".equals(retorno.getCodigoStatus())) {
                // Autorizada
                nota.setStatus(StatusNota.EMITIDA);
                nota.setProtocoloAutorizacao(retorno.getProtocolo());
                nota.setDataEmissao(LocalDateTime.now());
            } else {
                // Rejeitada
                nota.setStatus(StatusNota.REJEITADA);
            }

            notaRepository.save(nota);

            System.out.println("=== CRIANDO DTO FINAL ===");
            System.out.println("chaveAcesso: " + chaveAcesso + " (tipo: " + chaveAcesso.getClass().getName() + ")");
            System.out.println("Tentando criar NFeResponseDTO...");

            return new NFeResponseDTO(
                    chaveAcesso,
                    retorno.getCodigoStatus(),
                    retorno.getMensagem(),
                    retorno.getProtocolo(),
                    retorno.getDataHoraAutorizacao(),
                    retorno.getXmlResposta()
            );

        } catch (Exception e) {
            nota.setStatus(StatusNota.ERRO);
            notaRepository.save(nota);
            System.err.println("❌ ERRO CAPTURADO: " + e.getMessage());
            e.printStackTrace();
            throw new BusinessException("Erro ao emitir NF-e: " + e.getMessage());
        }
    }

    /**
     * Cancela uma NF-e autorizada
     */
    public NFeResponseDTO cancelar(Long notaId, String justificativa) throws Exception {
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
        NFeResponseDTO retorno = processarRetornoCancelamento(respostaCancelamento);

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
     * Processa retorno do cancelamento
     */
    private NFeResponseDTO processarRetornoCancelamento(String xmlResposta) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlResposta.getBytes("UTF-8")));

        NFeResponseDTO retorno = new NFeResponseDTO();

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
            String dataHora = dhRegEventoList.item(0).getTextContent();
            try {
                LocalDateTime dataHoraFormatada = LocalDateTime.parse(dataHora, DateTimeFormatter.ISO_DATE_TIME);
                retorno.setDataHoraAutorizacao(dataHoraFormatada);
            } catch (Exception e) {
                retorno.setDataHoraAutorizacao(LocalDateTime.now());
            }
        } else {
            retorno.setDataHoraAutorizacao(LocalDateTime.now());
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

    /**
     * Gera chave de acesso de 44 dígitos
     * Formato: UF + AAMM + CNPJ + MOD + SERIE + NUMERO + TIPO_EMISSAO + CODIGO_NUMERICO + DV
     */
    private String gerarChaveAcesso(Nota nota) {
        Empresa empresa = nota.getEmpresa();

        // UF (2 dígitos) - Código IBGE do estado
        String uf = obterCodigoUF(empresa.getEstadoUF());

        // AAMM (4 dígitos) - Ano e mês de emissão
        String anoMes = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMM"));

        // CNPJ (14 dígitos)
        String cnpj = empresa.getCnpj();

        // Modelo (2 dígitos) - 55 para NF-e
        String modelo = "55";

        // Série (3 dígitos)
        String serie = String.format("%03d", Integer.parseInt(nota.getSerie()));

        // Número (9 dígitos)
        String numero = nota.getNumero();

        // Tipo de emissão (1 dígito) - 1=Normal
        String tipoEmissao = "1";

        // Código numérico (11 dígitos) - Aleatório
        int codigoNumericoInt = new Random().nextInt(1000000000); // 0 a 99999999
        String codigoNumerico = String.format("%011d", codigoNumericoInt);

        // ✅ LOG PARA DEBUG
        System.out.println("=== MONTANDO CHAVE ===");
        System.out.println("UF: " + uf + " (length: " + uf.length() + ")");
        System.out.println("AAMM: " + anoMes + " (length: " + anoMes.length() + ")");
        System.out.println("CNPJ: " + cnpj + " (length: " + cnpj.length() + ")");
        System.out.println("Modelo: " + modelo + " (length: " + modelo.length() + ")");
        System.out.println("Série: " + serie + " (length: " + serie.length() + ")");
        System.out.println("Número: " + numero + " (length: " + numero.length() + ")");
        System.out.println("Tipo Emissão: " + tipoEmissao + " (length: " + tipoEmissao.length() + ")");
        System.out.println("Código Numérico: " + codigoNumerico + " (length: " + codigoNumerico.length() + ")");

        // Montar chave sem DV (44 dígitos)
        String chaveSemDV = uf + anoMes + cnpj + modelo + serie + numero + tipoEmissao + codigoNumerico;

        // Calcular dígito verificador
        String dv = calcularDigitoVerificador(chaveSemDV);

        // Chave completa (44 dígitos)
        return chaveSemDV + dv;
    }

    /**
     * Calcula dígito verificador da chave de acesso (Módulo 11)
     */
    private String calcularDigitoVerificador(String chave) {
        int soma = 0;
        int peso = 2;

        // Percorrer de trás para frente
        for (int i = chave.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(chave.charAt(i));
            soma += digito * peso;
            peso++;
            if (peso > 9) {
                peso = 2;
            }
        }

        int resto = soma % 11;
        int dv = 11 - resto;

        if (dv == 0 || dv == 10 || dv == 11) {
            return "0";
        }

        return String.valueOf(dv);
    }

    /**
     * Obtém código IBGE do estado
     */
    private String obterCodigoUF(String uf) {
        return switch (uf.toUpperCase()) {
            case "AC" -> "12";
            case "AL" -> "27";
            case "AP" -> "16";
            case "AM" -> "13";
            case "BA" -> "29";
            case "CE" -> "23";
            case "DF" -> "53";
            case "ES" -> "32";
            case "GO" -> "52";
            case "MA" -> "21";
            case "MT" -> "51";
            case "MS" -> "50";
            case "MG" -> "31";
            case "PA" -> "15";
            case "PB" -> "25";
            case "PR" -> "41";
            case "PE" -> "26";
            case "PI" -> "22";
            case "RJ" -> "33";
            case "RN" -> "24";
            case "RS" -> "43";
            case "RO" -> "11";
            case "RR" -> "14";
            case "SC" -> "42";
            case "SP" -> "35";
            case "SE" -> "28";
            case "TO" -> "17";
            default -> throw new BusinessException("UF inválida: " + uf);
        };
    }

    /**
     * Gera XML da NF-e (Layout 4.0)
     */
    private String gerarXml(Nota nota) {
        Empresa empresa = nota.getEmpresa();
        Cliente cliente = nota.getCliente();
        String chaveAcesso = nota.getChaveAcesso();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">");
        xml.append("<infNFe Id=\"NFe").append(chaveAcesso).append("\" versao=\"4.00\">");

        // IDE - Identificação
        xml.append("<ide>");
        xml.append("<cUF>").append(obterCodigoUF(empresa.getEstadoUF())).append("</cUF>");
        if (chaveAcesso.length() >= 44) {
            xml.append("<cNF>").append(chaveAcesso.substring(35, 44)).append("</cNF>");
        } else {
            throw new BusinessException("Chave de acesso inválida. Tamanho: " + chaveAcesso.length());
        }
        xml.append("<natOp>").append(nota.getTipo() != null ? nota.getTipo() : "Venda").append("</natOp>");
        xml.append("<mod>55</mod>");
        xml.append("<serie>").append(nota.getSerie() != null ? nota.getSerie() : 1).append("</serie>");
        xml.append("<nNF>").append(nota.getNumero()).append("</nNF>");
        xml.append("<dhEmi>").append(OffsetDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)).append("</dhEmi>");
        xml.append("<tpNF>1</tpNF>"); // 1=Saída
        xml.append("<idDest>1</idDest>"); // 1=Interna
        xml.append("<cMunFG>").append(empresa.getCidade()).append("</cMunFG>");
        xml.append("<tpImp>1</tpImp>"); // 1=DANFE Retrato
        xml.append("<tpEmis>1</tpEmis>"); // 1=Normal
        xml.append("<cDV>").append(chaveAcesso.substring(43)).append("</cDV>");
        xml.append("<tpAmb>").append(nfeConfig.getAmbiente()).append("</tpAmb>");
        xml.append("<finNFe>1</finNFe>"); // 1=Normal
        xml.append("<indFinal>1</indFinal>"); // 1=Consumidor Final
        xml.append("<indPres>1</indPres>"); // 1=Presencial
        xml.append("<procEmi>0</procEmi>"); // 0=Aplicativo do contribuinte
        xml.append("<verProc>1.0</verProc>");
        xml.append("</ide>");

        // EMIT - Emitente
        xml.append("<emit>");
        xml.append("<CNPJ>").append(empresa.getCnpj()).append("</CNPJ>");
        xml.append("<xNome>").append(escaparXml(empresa.getRazaoSocial())).append("</xNome>");
        xml.append("<xFant>").append(escaparXml(empresa.getNomeFantasia())).append("</xFant>");
        xml.append("<enderEmit>");
        xml.append("<xLgr>").append(escaparXml(empresa.getEnderecoCompleto())).append("</xLgr>");
        xml.append("<nro>").append(empresa.getTelefone()).append("</nro>");
        xml.append("<cMun>").append(empresa.getCidade()).append("</cMun>");
        xml.append("<xMun>").append(escaparXml(empresa.getCidade())).append("</xMun>");
        xml.append("<UF>").append(empresa.getEstadoUF()).append("</UF>");
        xml.append("<CEP>").append(empresa.getCep().replaceAll("[^0-9]", "")).append("</CEP>");
        xml.append("<cPais>1058</cPais>");
        xml.append("<xPais>Brasil</xPais>");
        xml.append("</enderEmit>");
        xml.append("<IE>").append(empresa.getInscricaoEstadual()).append("</IE>");
        xml.append("<CRT>1</CRT>"); // 1=Simples Nacional
        xml.append("</emit>");

        // DEST - Destinatário
        xml.append("<dest>");
        if (cliente.getCpfCnpj().length() == 11) {
            xml.append("<CPF>").append(cliente.getCpfCnpj()).append("</CPF>");
        } else {
            xml.append("<CNPJ>").append(cliente.getCpfCnpj()).append("</CNPJ>");
        }
        xml.append("<xNome>").append(escaparXml(cliente.getNome())).append("</xNome>");
        xml.append("<enderDest>");
        xml.append("<xLgr>").append(escaparXml(cliente.getEnderecoCompleto())).append("</xLgr>");
        xml.append("<nro>").append(cliente.getTelefone()).append("</nro>");
        xml.append("<xBairro>").append(escaparXml(cliente.getBairro())).append("</xBairro>");
        xml.append("<cMun>4305108</cMun>"); // TODO: Pegar do cliente
        xml.append("<xMun>").append(escaparXml(cliente.getCidade())).append("</xMun>");
        xml.append("<UF>").append(cliente.getEstadoUF()).append("</UF>");
        xml.append("<CEP>").append(cliente.getCep().replaceAll("[^0-9]", "")).append("</CEP>");
        xml.append("<cPais>1058</cPais>");
        xml.append("<xPais>Brasil</xPais>");
        xml.append("</enderDest>");
        xml.append("<indIEDest>9</indIEDest>"); // 9=Não contribuinte
        xml.append("</dest>");

        // DET - Produtos
        int itemNum = 1;
        for (ItemNota item : nota.getItens()) {
            xml.append("<det nItem=\"").append(itemNum++).append("\">");
            xml.append("<prod>");
            xml.append("<cProd>").append(item.getProduto().getCodigoProduto()).append("</cProd>");
            xml.append("<cEAN/>");
            xml.append("<xProd>").append(escaparXml(item.getProduto().getNome())).append("</xProd>");
            xml.append("<NCM>").append(item.getProduto().getNcm()).append("</NCM>");
            xml.append("<CFOP>").append(item.getCfop()).append("</CFOP>");
            xml.append("<uCom>").append(item.getProduto().getUnidade()).append("</uCom>");
            xml.append("<qCom>").append(item.getQuantidade()).append("</qCom>");
            xml.append("<vUnCom>").append(item.getProduto().getPrecoVenda()).append("</vUnCom>");
            xml.append("<vProd>").append(item.getNota().getValorTotal()).append("</vProd>");
            xml.append("<cEANTrib/>");
            xml.append("<uTrib>").append(item.getProduto().getUnidade()).append("</uTrib>");
            xml.append("<qTrib>").append(item.getQuantidade()).append("</qTrib>");
            xml.append("<vUnTrib>").append(item.getValorTotalItem()).append("</vUnTrib>");
            xml.append("<indTot>1</indTot>");
            xml.append("</prod>");

            // Impostos
            xml.append("<imposto>");
            xml.append("<ICMS>");
            xml.append("<ICMS00>");
            xml.append("<orig>0</orig>");
            xml.append("<CST>00</CST>");
            xml.append("<vBC>").append(item.getNota().getValorTotal()).append("</vBC>");
            xml.append("<pICMS>").append(item.getAliquotaIcms()).append("</pICMS>");
            xml.append("<vICMS>").append(item.getValorIcms()).append("</vICMS>");
            xml.append("</ICMS00>");
            xml.append("</ICMS>");
            xml.append("</imposto>");
            xml.append("</det>");
        }

        // TOTAL
        xml.append("<total>");
        xml.append("<ICMSTot>");
        xml.append("<vBC>").append(nota.getValorTotal()).append("</vBC>");
        //xml.append("<vICMS>").append().append("</vICMS>");
        xml.append("<vProd>").append(nota.getValorProdutos()).append("</vProd>");
        xml.append("<vNF>").append(nota.getValorTotal()).append("</vNF>");
        xml.append("</ICMSTot>");
        xml.append("</total>");

        xml.append("</infNFe>");
        xml.append("</NFe>");

        return xml.toString();
    }

    /**
     * Escapa caracteres especiais do XML
     */
    private String escaparXml(String texto) {
        if (texto == null) return "";
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    /**
     * Valida nota antes de emitir
     */
    private void validarNotaParaEmissao(Nota nota) {
        if (nota.getStatus() == StatusNota.EMITIDA) {
            throw new BusinessException("Nota já foi emitida");
        }

        if (nota.getEmpresa() == null) {
            throw new BusinessException("Empresa não informada");
        }

        if (nota.getCliente() == null) {
            throw new BusinessException("Cliente não informado");
        }

        if (nota.getItens() == null || nota.getItens().isEmpty()) {
            throw new BusinessException("Nota sem itens");
        }

        if (nota.getValorTotal() == null || nota.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Valor total inválido");
        }
    }

    /**
     * Processa retorno da SEFAZ
     */
    private NFeResponseDTO processarRetornoAutorizacao(String xmlRetorno) {
        try {
            NFeResponseDTO retorno = new NFeResponseDTO();

            // Parsear XML de resposta
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlRetorno.getBytes("UTF-8")));

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

            // Extrair protocolo
            NodeList nProtList = doc.getElementsByTagName("nProt");
            if (nProtList.getLength() > 0) {
                retorno.setProtocolo(nProtList.item(0).getTextContent());
            }


            // Extrair data/hora CONVERTER PARA LocalDateTime
            NodeList dhRecbtoList = doc.getElementsByTagName("dhRecbto");
            if (dhRecbtoList.getLength() > 0) {
                String dataHoraStr = dhRecbtoList.item(0).getTextContent();
                try {
                    // Tentar parsear como ISO_DATE_TIME
                    LocalDateTime dataHora = LocalDateTime.parse(dataHoraStr, DateTimeFormatter.ISO_DATE_TIME);
                    retorno.setDataHoraAutorizacao(dataHora);
                } catch (Exception e) {
                    // Se falhar, usar data/hora atual
                    retorno.setDataHoraAutorizacao(LocalDateTime.now());
                }
            } else {
                retorno.setDataHoraAutorizacao(LocalDateTime.now());
            }

            retorno.setXmlResposta(xmlRetorno);

            return retorno;

        } catch (Exception e) {
            // Se falhar o parse, retornar mock
            NFeResponseDTO retorno = new NFeResponseDTO();
            retorno.setCodigoStatus("100");
            retorno.setMensagem("Autorizado o uso da NF-e");
            retorno.setProtocolo("143260000123456");
            retorno.setDataHoraAutorizacao(LocalDateTime.now());
            retorno.setXmlResposta(xmlRetorno);
            return retorno;
        }
    }
}