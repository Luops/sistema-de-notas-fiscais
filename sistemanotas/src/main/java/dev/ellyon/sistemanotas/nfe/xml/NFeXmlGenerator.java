package dev.ellyon.sistemanotas.nfe.xml;

import dev.ellyon.sistemanotas.model.*;
import dev.ellyon.sistemanotas.nfe.config.NFeConfig;
import dev.ellyon.sistemanotas.nfe.service.ChaveAcessoService;
import dev.ellyon.sistemanotas.nfe.util.NFeConstants;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Component
public class NFeXmlGenerator {

    private final NFeConfig nfeConfig;
    private final ChaveAcessoService chaveAcessoService;

    public NFeXmlGenerator(NFeConfig nfeConfig, ChaveAcessoService chaveAcessoService) {
        this.nfeConfig = nfeConfig;
        this.chaveAcessoService = chaveAcessoService;
    }

    /**
     * Gera o XML completo da NF-e a partir de uma Nota
     */
    public String gerar(Nota nota, String chaveAcesso) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // Elemento raiz <NFe>
        Element nfeElement = doc.createElement("NFe");
        nfeElement.setAttribute("xmlns", "http://www.portalfiscal.inf.br/nfe");
        doc.appendChild(nfeElement);

        // <infNFe> - Informações da NF-e
        Element infNFe = doc.createElement("infNFe");
        infNFe.setAttribute("Id", "NFe" + chaveAcesso);
        infNFe.setAttribute("versao", NFeConstants.VERSAO_LAYOUT);
        nfeElement.appendChild(infNFe);

        // <ide> - Identificação da NF-e
        adicionarIdentificacao(doc, infNFe, nota, chaveAcesso);

        // <emit> - Dados do Emitente
        adicionarEmitente(doc, infNFe, nota.getEmpresa());

        // <dest> - Dados do Destinatário
        if (nota.getCliente() != null) {
            adicionarDestinatario(doc, infNFe, nota.getCliente());
        }

        // <det> - Detalhamento dos Produtos/Serviços
        int nItem = 1;
        for (ItemNota item : nota.getItens()) {
            adicionarItem(doc, infNFe, item, nItem++);
        }

        // <total> - Totais da NF-e
        adicionarTotais(doc, infNFe, nota);

        // <transp> - Informações de Transporte
        adicionarTransporte(doc, infNFe, nota);

        // <pag> - Informações de Pagamento
        adicionarPagamento(doc, infNFe, nota);

        // <infAdic> - Informações Adicionais
        if (nota.getObservacoes() != null && !nota.getObservacoes().isBlank()) {
            adicionarInformacoesAdicionais(doc, infNFe, nota);
        }

        // Converter Document para String XML
        return documentToString(doc);
    }

    /**
     * <ide> - Identificação da NF-e
     */
    private void adicionarIdentificacao(Document doc, Element infNFe, Nota nota, String chaveAcesso) {
        Element ide = doc.createElement("ide");
        infNFe.appendChild(ide);

        // Código da UF
        appendChild(doc, ide, "cUF", chaveAcesso.substring(0, 2));

        // Código Numérico (8 dígitos da chave)
        appendChild(doc, ide, "cNF", chaveAcesso.substring(35, 43));

        // Natureza da Operação
        appendChild(doc, ide, "natOp", "VENDA DE MERCADORIA");

        // Modelo do Documento Fiscal
        appendChild(doc, ide, "mod", NFeConstants.MODELO_NFE);

        // Série do Documento Fiscal
        appendChild(doc, ide, "serie", nota.getSerie());

        // Número do Documento Fiscal
        appendChild(doc, ide, "nNF", nota.getNumero());

        // Data e Hora de Emissão
        appendChild(doc, ide, "dhEmi",
                nota.getDataEmissao().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Data e Hora de Saída/Entrada
        appendChild(doc, ide, "dhSaiEnt",
                nota.getDataEmissao().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Tipo de Operação (1=Saída)
        appendChild(doc, ide, "tpNF", NFeConstants.TIPO_OPERACAO_SAIDA);

        // Identificador de local de destino (1=Interna, 2=Interestadual, 3=Exterior)
        appendChild(doc, ide, "idDest", "1");

        // Código do Município de Ocorrência do Fato Gerador
        appendChild(doc, ide, "cMunFG", nfeConfig.getEmitente().getCodigoMunicipio());

        // Formato de Impressão do DANFE
        appendChild(doc, ide, "tpImp", NFeConstants.FORMATO_DANFE_RETRATO);

        // Tipo de Emissão da NF-e
        appendChild(doc, ide, "tpEmis", NFeConstants.TIPO_EMISSAO_NORMAL);

        // Dígito Verificador da Chave de Acesso
        appendChild(doc, ide, "cDV", chaveAcesso.substring(43));

        // Identificação do Ambiente (1=Produção, 2=Homologação)
        appendChild(doc, ide, "tpAmb", nfeConfig.getAmbiente().toString());

        // Finalidade de emissão da NF-e
        appendChild(doc, ide, "finNFe", NFeConstants.FINALIDADE_NORMAL);

        // Indica operação com Consumidor final
        appendChild(doc, ide, "indFinal", "1");

        // Indicador de presença do comprador
        appendChild(doc, ide, "indPres", NFeConstants.PRESENCA_OPERACAO_PRESENCIAL);

        // Processo de emissão da NF-e (0=aplicação do contribuinte)
        appendChild(doc, ide, "procEmi", "0");

        // Versão do Processo de emissão da NF-e
        appendChild(doc, ide, "verProc", "1.0.0");
    }

    /**
     * <emit> - Dados do Emitente
     */
    private void adicionarEmitente(Document doc, Element infNFe, Empresa empresa) {
        Element emit = doc.createElement("emit");
        infNFe.appendChild(emit);

        // CNPJ do emitente
        appendChild(doc, emit, "CNPJ", empresa.getCnpj().replaceAll("\\D", ""));

        // Razão Social
        appendChild(doc, emit, "xNome", empresa.getRazaoSocial());

        // Nome fantasia
        appendChild(doc, emit, "xFant", empresa.getNomeFantasia());

        // Endereço do emitente
        Element enderEmit = doc.createElement("enderEmit");
        emit.appendChild(enderEmit);

        appendChild(doc, enderEmit, "xLgr", empresa.getEnderecoCompleto());
        appendChild(doc, enderEmit, "nro", "S/N");
        appendChild(doc, enderEmit, "xBairro", "Centro");
        appendChild(doc, enderEmit, "cMun", nfeConfig.getEmitente().getCodigoMunicipio());
        appendChild(doc, enderEmit, "xMun", empresa.getCidade());
        appendChild(doc, enderEmit, "UF", empresa.getEstadoUF());
        appendChild(doc, enderEmit, "CEP", empresa.getCep().replaceAll("\\D", ""));
        appendChild(doc, enderEmit, "cPais", "1058");
        appendChild(doc, enderEmit, "xPais", "Brasil");

        if (empresa.getTelefone() != null) {
            appendChild(doc, enderEmit, "fone", empresa.getTelefone().replaceAll("\\D", ""));
        }

        // Inscrição Estadual
        appendChild(doc, emit, "IE", empresa.getInscricaoEstadual());

        // Código de Regime Tributário (1=Simples Nacional)
        appendChild(doc, emit, "CRT", "1");
    }

    /**
     * <dest> - Dados do Destinatário
     */
    private void adicionarDestinatario(Document doc, Element infNFe, Cliente cliente) {
        Element dest = doc.createElement("dest");
        infNFe.appendChild(dest);

        // CPF ou CNPJ
        String cpfCnpj = cliente.getCpfCnpj().replaceAll("\\D", "");
        if (cpfCnpj.length() == 11) {
            appendChild(doc, dest, "CPF", cpfCnpj);
        } else {
            appendChild(doc, dest, "CNPJ", cpfCnpj);
        }

        // Nome/Razão Social
        appendChild(doc, dest, "xNome", cliente.getNome());

        // Endereço do destinatário
        Element enderDest = doc.createElement("enderDest");
        dest.appendChild(enderDest);

        appendChild(doc, enderDest, "xLgr", cliente.getEnderecoCompleto());
        appendChild(doc, enderDest, "nro", "S/N");
        appendChild(doc, enderDest, "xBairro", cliente.getBairro());
        appendChild(doc, enderDest, "cMun", "4305108"); // Código do município
        appendChild(doc, enderDest, "xMun", cliente.getCidade());
        appendChild(doc, enderDest, "UF", cliente.getEstadoUF());
        appendChild(doc, enderDest, "CEP", cliente.getCep().replaceAll("\\D", ""));
        appendChild(doc, enderDest, "cPais", "1058");
        appendChild(doc, enderDest, "xPais", "Brasil");

        if (cliente.getTelefone() != null) {
            appendChild(doc, enderDest, "fone", cliente.getTelefone().replaceAll("\\D", ""));
        }

        // Indicador da IE do Destinatário (9=Não Contribuinte)
        appendChild(doc, dest, "indIEDest", "9");

        if (cliente.getEmail() != null) {
            appendChild(doc, dest, "email", cliente.getEmail());
        }
    }

    /**
     * <det> - Detalhamento do Produto/Serviço
     */
    private void adicionarItem(Document doc, Element infNFe, ItemNota item, int nItem) {
        Element det = doc.createElement("det");
        det.setAttribute("nItem", String.valueOf(nItem));
        infNFe.appendChild(det);

        // <prod> - Dados do Produto
        Element prod = doc.createElement("prod");
        det.appendChild(prod);

        appendChild(doc, prod, "cProd", item.getCodigoProduto());
        appendChild(doc, prod, "cEAN", "SEM GTIN");
        appendChild(doc, prod, "xProd", item.getDescricaoProduto());
        appendChild(doc, prod, "NCM", item.getNcm());
        appendChild(doc, prod, "CFOP", item.getCfop());
        appendChild(doc, prod, "uCom", item.getUnidade().name());
        appendChild(doc, prod, "qCom", formatarDecimal(item.getQuantidade(), 4));
        appendChild(doc, prod, "vUnCom", formatarDecimal(item.getPrecoUnitario(), 10));
        appendChild(doc, prod, "vProd", formatarDecimal(item.getSubtotal(), 2));
        appendChild(doc, prod, "cEANTrib", "SEM GTIN");
        appendChild(doc, prod, "uTrib", item.getUnidade().name());
        appendChild(doc, prod, "qTrib", formatarDecimal(item.getQuantidade(), 4));
        appendChild(doc, prod, "vUnTrib", formatarDecimal(item.getPrecoUnitario(), 10));
        appendChild(doc, prod, "indTot", "1");

        // <imposto> - Tributos incidentes no Produto
        Element imposto = doc.createElement("imposto");
        det.appendChild(imposto);

        appendChild(doc, imposto, "vTotTrib", formatarDecimal(
                item.getValorIcms().add(item.getValorPis()).add(item.getValorCofins()), 2));

        // <ICMS>
        Element icms = doc.createElement("ICMS");
        imposto.appendChild(icms);

        Element icmsSN101 = doc.createElement("ICMSSN101");
        icms.appendChild(icmsSN101);

        appendChild(doc, icmsSN101, "orig", "0");
        appendChild(doc, icmsSN101, "CSOSN", "101");
        appendChild(doc, icmsSN101, "pCredSN", formatarDecimal(item.getAliquotaIcms(), 4));
        appendChild(doc, icmsSN101, "vCredICMSSN", formatarDecimal(item.getValorIcms(), 2));

        // <PIS>
        Element pis = doc.createElement("PIS");
        imposto.appendChild(pis);

        Element pisAliq = doc.createElement("PISAliq");
        pis.appendChild(pisAliq);

        appendChild(doc, pisAliq, "CST", "01");
        appendChild(doc, pisAliq, "vBC", formatarDecimal(item.getSubtotal(), 2));
        appendChild(doc, pisAliq, "pPIS", formatarDecimal(item.getAliquotaPis(), 4));
        appendChild(doc, pisAliq, "vPIS", formatarDecimal(item.getValorPis(), 2));

        // <COFINS>
        Element cofins = doc.createElement("COFINS");
        imposto.appendChild(cofins);

        Element cofinsAliq = doc.createElement("COFINSAliq");
        cofins.appendChild(cofinsAliq);

        appendChild(doc, cofinsAliq, "CST", "01");
        appendChild(doc, cofinsAliq, "vBC", formatarDecimal(item.getSubtotal(), 2));
        appendChild(doc, cofinsAliq, "pCOFINS", formatarDecimal(item.getAliquotaCofins(), 4));
        appendChild(doc, cofinsAliq, "vCOFINS", formatarDecimal(item.getValorCofins(), 2));
    }

    /**
     * <total> - Totais da NF-e
     */
    private void adicionarTotais(Document doc, Element infNFe, Nota nota) {
        Element total = doc.createElement("total");
        infNFe.appendChild(total);

        Element ICMSTot = doc.createElement("ICMSTot");
        total.appendChild(ICMSTot);

        appendChild(doc, ICMSTot, "vBC", formatarDecimal(nota.getValorProdutos(), 2));
        appendChild(doc, ICMSTot, "vICMS", formatarDecimal(BigDecimal.ZERO, 2));
        appendChild(doc, ICMSTot, "vICMSDeson", formatarDecimal(BigDecimal.ZERO, 2));
        appendChild(doc, ICMSTot, "vFCP", formatarDecimal(BigDecimal.ZERO, 2));
        appendChild(doc, ICMSTot, "vBCST", formatarDecimal(BigDecimal.ZERO, 2));
        appendChild(doc, ICMSTot, "vST", formatarDecimal(BigDecimal.ZERO, 2));
        appendChild(doc, ICMSTot, "vFCPST", formatarDecimal(BigDecimal.ZERO, 2));
        appendChild(doc, ICMSTot, "vFCPSTRet", formatarDecimal(BigDecimal.ZERO, 2));
        appendChild(doc, ICMSTot, "vProd", formatarDecimal(nota.getValorProdutos(), 2));
        appendChild(doc, ICMSTot, "vFrete", formatarDecimal(nota.getFrete() != null ? nota.getFrete() : BigDecimal.ZERO, 2));
        appendChild(doc, ICMSTot, "vSeg", formatarDecimal(BigDecimal.ZERO, 2));
        appendChild(doc, ICMSTot, "vDesc", formatarDecimal(BigDecimal.ZERO, 2));
        appendChild(doc, ICMSTot, "vII", formatarDecimal(BigDecimal.ZERO, 2));
        appendChild(doc, ICMSTot, "vIPI", formatarDecimal(BigDecimal.ZERO, 2));
        appendChild(doc, ICMSTot, "vIPIDevol", formatarDecimal(BigDecimal.ZERO, 2));
        appendChild(doc, ICMSTot, "vPIS", formatarDecimal(BigDecimal.ZERO, 2));
        appendChild(doc, ICMSTot, "vCOFINS", formatarDecimal(BigDecimal.ZERO, 2));
        appendChild(doc, ICMSTot, "vOutro", formatarDecimal(BigDecimal.ZERO, 2));
        appendChild(doc, ICMSTot, "vNF", formatarDecimal(nota.getValorTotal(), 2));
    }

    /**
     * <transp> - Informações de Transporte
     */
    private void adicionarTransporte(Document doc, Element infNFe, Nota nota) {
        Element transp = doc.createElement("transp");
        infNFe.appendChild(transp);

        // Modalidade do frete (9=Sem Ocorrência de Transporte)
        appendChild(doc, transp, "modFrete", "9");
    }

    /**
     * <pag> - Informações de Pagamento
     */
    private void adicionarPagamento(Document doc, Element infNFe, Nota nota) {
        Element pag = doc.createElement("pag");
        infNFe.appendChild(pag);

        Element detPag = doc.createElement("detPag");
        pag.appendChild(detPag);

        // Forma de pagamento (01=Dinheiro)
        appendChild(doc, detPag, "tPag", "01");

        // Valor do pagamento
        appendChild(doc, detPag, "vPag", formatarDecimal(nota.getValorTotal(), 2));
    }

    /**
     * <infAdic> - Informações Adicionais
     */
    private void adicionarInformacoesAdicionais(Document doc, Element infNFe, Nota nota) {
        Element infAdic = doc.createElement("infAdic");
        infNFe.appendChild(infAdic);

        appendChild(doc, infAdic, "infCpl", nota.getObservacoes());
    }

    /**
     * Adiciona um elemento filho com texto
     */
    private void appendChild(Document doc, Element parent, String tagName, String textContent) {
        Element child = doc.createElement(tagName);
        child.setTextContent(textContent);
        parent.appendChild(child);
    }

    /**
     * Formata BigDecimal para XML
     */
    private String formatarDecimal(BigDecimal valor, int casasDecimais) {
        return String.format("%." + casasDecimais + "f", valor);
    }

    /**
     * Converte Document para String XML
     */
    private String documentToString(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.getBuffer().toString();
    }
}