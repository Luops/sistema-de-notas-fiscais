package dev.ellyon.sistemanotas.dto.nota;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.ellyon.sistemanotas.dto.cliente.ClienteSimpleResponseDTO;
import dev.ellyon.sistemanotas.dto.empresa.EmpresaSimpleResponseDTO;
import dev.ellyon.sistemanotas.dto.itemNota.ItemNotaResponseDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioSimpleResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class NotaResponseDTO {
    private Long id;
    private String numero;
    private String serie;
    private String tipo;
    private String status;
    private EmpresaSimpleResponseDTO empresa;
    private ClienteSimpleResponseDTO cliente;
    private LocalDateTime dataEmissao;
    private LocalDateTime dataCancelamento;
    private ItemNotaResponseDTO[] itens;
    private BigDecimal valorProdutos;
    private BigDecimal valorImpostosTotal;
    private BigDecimal valorTotal;
    private String observacoes;
    private UsuarioSimpleResponseDTO createdBy;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

    // construtor padrao
    public NotaResponseDTO() {

    }

    // construtor completo
    public NotaResponseDTO(Long id, String numero, String serie, String tipo, String status, EmpresaSimpleResponseDTO empresa, ClienteSimpleResponseDTO cliente, LocalDateTime dataEmissao, LocalDateTime dataCancelamento, ItemNotaResponseDTO[] itens, BigDecimal valorProdutos, BigDecimal valorImpostosTotal, BigDecimal valorTotal, String observacoes, UsuarioSimpleResponseDTO createdBy,  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
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
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EmpresaSimpleResponseDTO getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaSimpleResponseDTO empresa) {
        this.empresa = empresa;
    }

    public ClienteSimpleResponseDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteSimpleResponseDTO cliente) {
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

    public ItemNotaResponseDTO[] getItens() {
        return itens;
    }

    public void setItens(ItemNotaResponseDTO[] itens) {
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

    public UsuarioSimpleResponseDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UsuarioSimpleResponseDTO createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
