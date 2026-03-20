package dev.ellyon.sistemanotas.nfe.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.model.*;
import dev.ellyon.sistemanotas.model.enums.StatusNota;
import dev.ellyon.sistemanotas.repository.NotaRepository;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class DanfeService {

    private final NotaRepository notaRepository;
    private final ChaveAcessoService chaveAcessoService;

    // Cores
    private static final BaseColor COR_CINZA_CLARO = new BaseColor(240, 240, 240);
    private static final BaseColor COR_BORDA = BaseColor.BLACK;

    // Fontes
    private static Font FONT_TITULO;
    private static Font FONT_LABEL;
    private static Font FONT_VALOR;
    private static Font FONT_PEQUENA;

    static {
        try {
            FONT_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            FONT_LABEL = FontFactory.getFont(FontFactory.HELVETICA, 6);
            FONT_VALOR = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7);
            FONT_PEQUENA = FontFactory.getFont(FontFactory.HELVETICA, 5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DanfeService(NotaRepository notaRepository, ChaveAcessoService chaveAcessoService) {
        this.notaRepository = notaRepository;
        this.chaveAcessoService = chaveAcessoService;
    }

    /**
     * Gera DANFE em PDF
     */
    public byte[] gerar(Long notaId) throws Exception {
        // Buscar nota
        Nota nota = notaRepository.findById(notaId)
                .orElseThrow(() -> new BusinessException("Nota não encontrada"));

        // Validar se foi emitida
        if (nota.getStatus() != StatusNota.EMITIDA) {
            throw new BusinessException("Apenas notas emitidas podem gerar DANFE");
        }

        if (nota.getChaveAcesso() == null || nota.getChaveAcesso().isBlank()) {
            throw new BusinessException("Nota não possui chave de acesso");
        }

        // Criar documento PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        document.open();

        // Adicionar conteúdo
        adicionarCabecalho(document, nota);
        adicionarDestinatario(document, nota);
        adicionarProdutos(document, nota);
        adicionarTotais(document, nota);
        adicionarTransportadora(document, nota);
        adicionarDadosAdicionais(document, nota);

        document.close();

        return baos.toByteArray();
    }

    /**
     * Adiciona cabeçalho do DANFE
     */
    private void adicionarCabecalho(Document document, Nota nota) throws DocumentException, IOException, WriterException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{30, 40, 30});

        // ========================================
        // COLUNA 1: Logo e Dados do Emitente
        // ========================================
        PdfPCell cellEmitente = new PdfPCell();
        cellEmitente.setBorder(Rectangle.BOX);

        Paragraph emitente = new Paragraph();
        emitente.add(new Chunk(nota.getEmpresa().getRazaoSocial() + "\n", FONT_TITULO));
        emitente.add(new Chunk(nota.getEmpresa().getEnderecoCompleto() + "\n", FONT_LABEL));
        emitente.add(new Chunk(nota.getEmpresa().getCidade() + " - " + nota.getEmpresa().getEstadoUF() + "\n", FONT_LABEL));
        emitente.add(new Chunk("CEP: " + nota.getEmpresa().getCep() + "\n", FONT_LABEL));
        if (nota.getEmpresa().getTelefone() != null) {
            emitente.add(new Chunk("Fone: " + nota.getEmpresa().getTelefone() + "\n", FONT_LABEL));
        }

        cellEmitente.addElement(emitente);
        table.addCell(cellEmitente);

        // ========================================
        // COLUNA 2: Título DANFE
        // ========================================
        PdfPCell cellDanfe = new PdfPCell();
        cellDanfe.setBorder(Rectangle.BOX);
        cellDanfe.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellDanfe.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph danfeTitle = new Paragraph();
        danfeTitle.setAlignment(Element.ALIGN_CENTER);
        danfeTitle.add(new Chunk("DANFE\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        danfeTitle.add(new Chunk("Documento Auxiliar da\nNota Fiscal Eletrônica\n", FONT_LABEL));
        danfeTitle.add(new Chunk("\n0 - ENTRADA\n1 - SAÍDA\n\n", FONT_PEQUENA));
        danfeTitle.add(new Chunk("1", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
        danfeTitle.add(new Chunk("\n\nNº " + nota.getNumero() + "\n", FONT_TITULO));
        danfeTitle.add(new Chunk("SÉRIE " + nota.getSerie(), FONT_TITULO));

        cellDanfe.addElement(danfeTitle);
        table.addCell(cellDanfe);

        // ========================================
        // COLUNA 3: Código de barras da chave
        // ========================================
        PdfPCell cellBarcode = new PdfPCell();
        cellBarcode.setBorder(Rectangle.BOX);
        cellBarcode.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Gerar QR Code da chave de acesso
        byte[] qrCodeBytes = gerarQRCode(nota.getChaveAcesso(), 100, 100);
        Image qrCode = Image.getInstance(qrCodeBytes);
        qrCode.scaleAbsolute(80, 80);

        cellBarcode.addElement(qrCode);
        cellBarcode.addElement(new Paragraph(chaveAcessoService.formatar(nota.getChaveAcesso()), FONT_PEQUENA));

        table.addCell(cellBarcode);

        document.add(table);

        // ========================================
        // Linha 2: Chave de Acesso e Protocolo
        // ========================================
        PdfPTable tableChave = new PdfPTable(1);
        tableChave.setWidthPercentage(100);
        tableChave.setSpacingBefore(2);

        PdfPCell cellChave = new PdfPCell();
        cellChave.setBorder(Rectangle.BOX);
        cellChave.setBackgroundColor(COR_CINZA_CLARO);

        Paragraph chaveInfo = new Paragraph();
        chaveInfo.add(new Chunk("CHAVE DE ACESSO\n", FONT_LABEL));
        chaveInfo.add(new Chunk(chaveAcessoService.formatar(nota.getChaveAcesso()) + "\n", FONT_VALOR));

        if (nota.getProtocoloAutorizacao() != null) {
            chaveInfo.add(new Chunk("\nPROTOCOLO DE AUTORIZAÇÃO: " + nota.getProtocoloAutorizacao(), FONT_LABEL));
            chaveInfo.add(new Chunk(" - " + nota.getDataEmissao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), FONT_LABEL));
        }

        cellChave.addElement(chaveInfo);
        tableChave.addCell(cellChave);

        document.add(tableChave);
    }

    /**
     * Adiciona dados do destinatário/remetente
     */
    private void adicionarDestinatario(Document document, Nota nota) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(2);
        table.setWidths(new float[]{70, 30});

        Cliente cliente = nota.getCliente();

        // Nome/Razão Social
        PdfPCell cellNome = criarCelula("DESTINATÁRIO / REMETENTE", cliente.getNome(), 1);
        table.addCell(cellNome);

        // CNPJ/CPF
        String cpfCnpj = cliente.getCpfCnpj().length() == 11 ? "CPF" : "CNPJ";
        PdfPCell cellDoc = criarCelula(cpfCnpj, formatarCpfCnpj(cliente.getCpfCnpj()), 1);
        table.addCell(cellDoc);

        // Endereço
        PdfPCell cellEnd = criarCelula("ENDEREÇO", cliente.getEnderecoCompleto(), 1);
        table.addCell(cellEnd);

        // Bairro
        PdfPCell cellBairro = criarCelula("BAIRRO", cliente.getBairro(), 1);
        table.addCell(cellBairro);

        // CEP
        PdfPCell cellCep = criarCelula("CEP", cliente.getCep(), 1);
        table.addCell(cellCep);

        // Município
        PdfPCell cellMun = criarCelula("MUNICÍPIO", cliente.getCidade(), 1);
        table.addCell(cellMun);

        // UF
        PdfPCell cellUf = criarCelula("UF", cliente.getEstadoUF(), 1);
        table.addCell(cellUf);

        // Telefone
        PdfPCell cellFone = criarCelula("FONE", cliente.getTelefone(), 1);
        table.addCell(cellFone);

        document.add(table);
    }

    /**
     * Adiciona produtos/serviços
     */
    private void adicionarProdutos(Document document, Nota nota) throws DocumentException {
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(2);
        table.setWidths(new float[]{10, 30, 10, 10, 15, 15, 10});

        // Cabeçalho
        table.addCell(criarCelulaCabecalho("CÓDIGO"));
        table.addCell(criarCelulaCabecalho("DESCRIÇÃO"));
        table.addCell(criarCelulaCabecalho("NCM"));
        table.addCell(criarCelulaCabecalho("QTDE"));
        table.addCell(criarCelulaCabecalho("VL. UNIT"));
        table.addCell(criarCelulaCabecalho("VL. TOTAL"));
        table.addCell(criarCelulaCabecalho("UN"));

        // Itens
        NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        NumberFormat numero = NumberFormat.getNumberInstance(new Locale("pt", "BR"));

        for (ItemNota item : nota.getItens()) {
            table.addCell(criarCelulaItem(item.getCodigoProduto()));
            table.addCell(criarCelulaItem(item.getDescricaoProduto()));
            table.addCell(criarCelulaItem(item.getNcm()));
            table.addCell(criarCelulaItem(numero.format(item.getQuantidade())));
            table.addCell(criarCelulaItem(moeda.format(item.getPrecoUnitario())));
            table.addCell(criarCelulaItem(moeda.format(item.getSubtotal())));
            table.addCell(criarCelulaItem(item.getUnidade().name()));
        }

        document.add(table);
    }

    /**
     * Adiciona totais da nota
     */
    private void adicionarTotais(Document document, Nota nota) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(2);
        table.setWidths(new float[]{33, 33, 34});

        NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        // Base de Cálculo ICMS
        table.addCell(criarCelula("BASE CÁLC. ICMS", moeda.format(nota.getValorProdutos()), 1));

        // Valor ICMS
        table.addCell(criarCelula("VALOR ICMS", moeda.format(BigDecimal.ZERO), 1));

        // Valor Total Produtos
        table.addCell(criarCelula("VALOR TOTAL PRODUTOS", moeda.format(nota.getValorProdutos()), 1));

        // Frete
        BigDecimal frete = nota.getFrete() != null ? nota.getFrete() : BigDecimal.ZERO;
        table.addCell(criarCelula("VALOR FRETE", moeda.format(frete), 1));

        // Seguro
        table.addCell(criarCelula("VALOR SEGURO", moeda.format(BigDecimal.ZERO), 1));

        // Desconto
        table.addCell(criarCelula("DESCONTO", moeda.format(BigDecimal.ZERO), 1));

        // Outras Despesas
        table.addCell(criarCelula("OUTRAS DESPESAS", moeda.format(BigDecimal.ZERO), 1));

        // Valor Total IPI
        table.addCell(criarCelula("VALOR TOTAL IPI", moeda.format(BigDecimal.ZERO), 1));

        // Valor Total Nota
        PdfPCell cellTotal = criarCelula("VALOR TOTAL DA NOTA", moeda.format(nota.getValorTotal()), 1);
        cellTotal.setBackgroundColor(COR_CINZA_CLARO);
        table.addCell(cellTotal);

        document.add(table);
    }

    /**
     * Adiciona dados da transportadora
     */
    private void adicionarTransportadora(Document document, Nota nota) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(2);

        table.addCell(criarCelula("TRANSPORTADOR / VOLUMES TRANSPORTADOS", "SEM TRANSPORTE", 1));

        document.add(table);
    }

    /**
     * Adiciona dados adicionais
     */
    private void adicionarDadosAdicionais(Document document, Nota nota) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(2);

        String observacoes = nota.getObservacoes() != null ? nota.getObservacoes() : "";
        table.addCell(criarCelula("DADOS ADICIONAIS", observacoes, 1));

        document.add(table);
    }

    /**
     * Cria célula com label e valor
     */
    private PdfPCell criarCelula(String label, String valor, int colspan) {
        PdfPCell cell = new PdfPCell();
        cell.setColspan(colspan);
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(3);

        Paragraph p = new Paragraph();
        p.add(new Chunk(label + "\n", FONT_LABEL));
        p.add(new Chunk(valor != null ? valor : "", FONT_VALOR));

        cell.addElement(p);
        return cell;
    }

    /**
     * Cria célula de cabeçalho de tabela
     */
    private PdfPCell criarCelulaCabecalho(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, FONT_TITULO));
        cell.setBackgroundColor(COR_CINZA_CLARO);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(3);
        cell.setBorder(Rectangle.BOX);
        return cell;
    }

    /**
     * Cria célula de item
     */
    private PdfPCell criarCelulaItem(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, FONT_LABEL));
        cell.setPadding(3);
        cell.setBorder(Rectangle.BOX);
        return cell;
    }

    /**
     * Gera QR Code
     */
    private byte[] gerarQRCode(String texto, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, width, height);

        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);

        return baos.toByteArray();
    }

    /**
     * Formata CPF/CNPJ
     */
    private String formatarCpfCnpj(String cpfCnpj) {
        if (cpfCnpj == null) return "";

        String numeros = cpfCnpj.replaceAll("\\D", "");

        if (numeros.length() == 11) {
            // CPF: 000.000.000-00
            return numeros.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        } else if (numeros.length() == 14) {
            // CNPJ: 00.000.000/0000-00
            return numeros.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        }

        return cpfCnpj;
    }
}