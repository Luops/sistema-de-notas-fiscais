package dev.ellyon.sistemanotas.model;

import dev.ellyon.sistemanotas.model.enums.StatusNota;
import dev.ellyon.sistemanotas.model.enums.TipoNota;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "nota")
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @Column(name = "id_empresa", nullable = false)
    private Empresa empresaId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @Column(name = "id_cliente", nullable = false)
    private Cliente clienteId;

    @Column(name = "data_emissao", nullable = false)
    private LocalDateTime dataEmissao;

    @Column(name = "data_cancelamento")
    private LocalDateTime dataCancelamento;

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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @Column(name = "created_by_user_id", nullable = false)
    private Usuario createdBy;

    // Construtor padrão
    protected Nota() {
        super();
    }

    // Construtor com todos os atributos
    public Nota(Long id, String numero, String serie, TipoNota tipo, StatusNota status, Empresa empresaId, Cliente clienteId,
                LocalDateTime dataEmissao, LocalDateTime dataCancelamento, BigDecimal valorProdutos,
                BigDecimal valorImpostosTotal, BigDecimal valorTotal, String observacoes, String chaveAcesso,
                String protocoloAutorizacao, Usuario createdBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.numero = numero;
        this.serie = serie;
        this.tipo = tipo;
        this.status = status;
        this.empresaId = empresaId;
        this.clienteId = clienteId;
        this.dataEmissao = dataEmissao;
        this.dataCancelamento = dataCancelamento;
        this.valorProdutos = valorProdutos;
        this.valorImpostosTotal = valorImpostosTotal;
        this.valorTotal = valorTotal;
        this.observacoes = observacoes;
        this.chaveAcesso = chaveAcesso;
        this.protocoloAutorizacao = protocoloAutorizacao;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor sem id e timestamps
    public Nota(String numero, String serie, TipoNota tipo, StatusNota status, Empresa empresaId, Cliente clienteId,
                LocalDateTime dataEmissao, LocalDateTime dataCancelamento, BigDecimal valorProdutos,
                BigDecimal valorImpostosTotal, BigDecimal valorTotal, String observacoes, String chaveAcesso,
                String protocoloAutorizacao, Usuario createdBy) {
        this.numero = numero;
        this.serie = serie;
        this.tipo = tipo;
        this.status = status;
        this.empresaId = empresaId;
        this.clienteId = clienteId;
        this.dataEmissao = dataEmissao;
        this.dataCancelamento = dataCancelamento;
        this.valorProdutos = valorProdutos;
        this.valorImpostosTotal = valorImpostosTotal;
        this.valorTotal = valorTotal;
        this.observacoes = observacoes;
        this.chaveAcesso = chaveAcesso;
        this.protocoloAutorizacao = protocoloAutorizacao;
        this.createdBy = createdBy;
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

    public Empresa getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Empresa empresaId) {
        this.empresaId = empresaId;
    }

    public Cliente getClienteId() {
        return clienteId;
    }

    public void setClienteId(Cliente clienteId) {
        this.clienteId = clienteId;
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

    public Usuario getCreatedByUserId() {
        return createdBy;
    }

    public void setCreatedByUserId(Usuario createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Nota{" +
                "numero='" + numero + '\'' +
                ", serie='" + serie + '\'' +
                ", tipo=" + tipo +
                ", status=" + status +
                ", empresaId=" + empresaId +
                ", clienteId=" + clienteId +
                ", dataEmissao=" + dataEmissao +
                ", dataCancelamento=" + dataCancelamento +
                ", valorProdutos=" + valorProdutos +
                ", valorImpostosTotal=" + valorImpostosTotal +
                ", valorTotal=" + valorTotal +
                ", observacoes='" + observacoes + '\'' +
                ", chaveAcesso='" + chaveAcesso + '\'' +
                ", protocoloAutorizacao='" + protocoloAutorizacao + '\'' +
                ", createdBy=" + createdBy +
                ", id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}


