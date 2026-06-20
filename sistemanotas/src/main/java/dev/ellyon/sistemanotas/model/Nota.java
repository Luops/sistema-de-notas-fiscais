package dev.ellyon.sistemanotas.model;

import dev.ellyon.sistemanotas.model.enums.StatusNota;
import dev.ellyon.sistemanotas.model.enums.TipoNota;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_nota")
@Data
public class Nota extends Entidade{
    @Column(name = "numero", nullable = false, length = 255)
    private String numero;

    @Column(name = "serie", nullable = false, length = 50)
    private String serie;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 50)
    private TipoNota tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private StatusNota status;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "data_emissao")
    private LocalDateTime dataEmissao;

    @Column(name = "data_cancelamento")
    private LocalDateTime dataCancelamento;

    // Relacionamento com ItemNota
    @OneToMany(mappedBy = "nota", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemNota> itens = new ArrayList<>();

    @Column(name = "valor_produtos", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorProdutos;

    @Column(name = "valor_impostos_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorImpostosTotal;

    @Column(name = "valor_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    @Column(name = "chave_acesso", length = 255)
    private String chaveAcesso;

    @Column(name = "protocolo_autorizacao", length = 255)
    private String protocoloAutorizacao;

    @Column(name = "protocolo_cancelamento", length = 15)
    private String protocoloCancelamento;

    @Column(name = "justificativa_cancelamento", length = 255)
    private String justificativaCancelamento;

    @Lob
    @Column(name = "xml_nfe", columnDefinition = "TEXT")
    private String xmlNfe;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private Usuario createdBy;

    @Column(name = "frete", precision = 15, scale = 2)
    private BigDecimal frete;

    // Construtor padrão
    public Nota() {
        super();
    }

    // Construtor com todos os atributos
    public Nota(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String numero, String serie, TipoNota tipo, StatusNota status, Empresa empresa, Cliente cliente, LocalDateTime dataEmissao, LocalDateTime dataCancelamento, List<ItemNota> itens, BigDecimal valorProdutos, BigDecimal valorImpostosTotal, BigDecimal valorTotal, String observacoes, String chaveAcesso, String protocoloAutorizacao, String protocoloCancelamento, String justificativaCancelamento, String xmlNfe, Usuario createdBy, BigDecimal frete) {
        super(id, createdAt, updatedAt);
        this.numero = numero;//
        this.serie = serie;//
        this.tipo = tipo;//
        this.status = status;//
        this.empresa = empresa;//
        this.cliente = cliente;//
        this.dataEmissao = dataEmissao;//
        this.dataCancelamento = dataCancelamento;//
        this.itens = itens;//
        this.valorProdutos = valorProdutos;//
        this.valorImpostosTotal = valorImpostosTotal;//
        this.valorTotal = valorTotal;//
        this.observacoes = observacoes;//
        this.chaveAcesso = chaveAcesso;//
        this.protocoloAutorizacao = protocoloAutorizacao;//
        this.protocoloCancelamento = protocoloCancelamento;//
        this.justificativaCancelamento = justificativaCancelamento;//
        this.xmlNfe = xmlNfe;
        this.createdBy = createdBy;//
        this.frete = frete;//
    }

    // Construtor sem id e timestamps
    public Nota(String numero, String serie, TipoNota tipo, StatusNota status, Empresa empresa, Cliente cliente, LocalDateTime dataEmissao, LocalDateTime dataCancelamento, List<ItemNota> itens, BigDecimal valorProdutos, BigDecimal valorImpostosTotal, BigDecimal valorTotal, String observacoes, String chaveAcesso, String protocoloAutorizacao, String protocoloCancelamento, String justificativaCancelamento, String xmlNfe, Usuario createdBy, BigDecimal frete) {
        this.numero = numero;
        this.serie = serie;
        this.tipo = tipo;
        this.status = status;
        this.empresa = empresa;
        this.cliente = cliente;
        this.dataEmissao = dataEmissao;
        this.dataCancelamento = dataCancelamento;
        this.itens = itens;
        this.valorProdutos = valorProdutos;
        this.valorImpostosTotal = valorImpostosTotal;
        this.valorTotal = valorTotal;
        this.observacoes = observacoes;
        this.chaveAcesso = chaveAcesso;
        this.protocoloAutorizacao = protocoloAutorizacao;
        this.protocoloCancelamento = protocoloCancelamento;
        this.justificativaCancelamento = justificativaCancelamento;
        this.xmlNfe = xmlNfe;
        this.createdBy = createdBy;
        this.frete = frete;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public TipoNota getTipo() {
        return tipo;
    }

    public void setTipo(TipoNota tipo) {
        this.tipo = tipo;
    }

    public StatusNota getStatus() {
        return status;
    }

    public void setStatus(StatusNota status) {
        this.status = status;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDateTime dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public LocalDateTime getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(LocalDateTime dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public List<ItemNota> getItens() {
        return itens;
    }

    public void setItens(List<ItemNota> itens) {
        this.itens = itens;
    }

    public BigDecimal getValorProdutos() {
        return valorProdutos;
    }

    public void setValorProdutos(BigDecimal valorProdutos) {
        this.valorProdutos = valorProdutos;
    }

    public BigDecimal getValorImpostosTotal() {
        return valorImpostosTotal;
    }

    public void setValorImpostosTotal(BigDecimal valorImpostosTotal) {
        this.valorImpostosTotal = valorImpostosTotal;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getChaveAcesso() {
        return chaveAcesso;
    }

    public void setChaveAcesso(String chaveAcesso) {
        this.chaveAcesso = chaveAcesso;
    }

    public String getProtocoloAutorizacao() {
        return protocoloAutorizacao;
    }

    public void setProtocoloAutorizacao(String protocoloAutorizacao) {
        this.protocoloAutorizacao = protocoloAutorizacao;
    }

    public String getProtocoloCancelamento() {
        return protocoloCancelamento;
    }

    public void setProtocoloCancelamento(String protocoloCancelamento) {
        this.protocoloCancelamento = protocoloCancelamento;
    }

    public String getJustificativaCancelamento() {
        return justificativaCancelamento;
    }

    public void setJustificativaCancelamento(String justificativaCancelamento) {
        this.justificativaCancelamento = justificativaCancelamento;
    }

    public String getXmlNfe() {
        return xmlNfe;
    }

    public void setXmlNfe(String xmlNfe) {
        this.xmlNfe = xmlNfe;
    }

    public Usuario getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Usuario createdBy) {
        this.createdBy = createdBy;
    }

    public BigDecimal getFrete() {
        return frete;
    }

    public void setFrete(BigDecimal frete) {
        this.frete = frete;
    }

    @Override
    public String toString() {
        return "Nota{" +
                "numero='" + numero + '\'' +
                ", serie='" + serie + '\'' +
                ", tipo=" + tipo +
                ", status=" + status +
                ", empresa=" + empresa +
                ", cliente=" + cliente +
                ", dataEmissao=" + dataEmissao +
                ", dataCancelamento=" + dataCancelamento +
                ", itens=" + itens +
                ", valorProdutos=" + valorProdutos +
                ", valorImpostosTotal=" + valorImpostosTotal +
                ", valorTotal=" + valorTotal +
                ", observacoes='" + observacoes + '\'' +
                ", chaveAcesso='" + chaveAcesso + '\'' +
                ", protocoloAutorizacao='" + protocoloAutorizacao + '\'' +
                ", protocoloCancelamento='" + protocoloCancelamento + '\'' +
                ", justificativaCancelamento='" + justificativaCancelamento + '\'' +
                ", xmlNfe='" + xmlNfe + '\'' +
                ", createdBy=" + createdBy +
                ", frete=" + frete +
                '}';
    }
}


