package dev.ellyon.sistemanotas.dto.nota;

import com.fasterxml.jackson.annotation.JsonFormat;
import jdk.jshell.Snippet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class NotaListResponseDTO {
    private Long id;
    private String numero;
    private String tipo;
    private String status;
    private String clienteNome;
    private String tipoCliente;
    private BigDecimal valorTotal;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dataEmissao;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dataCancelamento;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    // Construtor padrão
    public NotaListResponseDTO() {

    }

    // Construtor completo
    public NotaListResponseDTO(Long id, String numero, String tipo, String status, String clienteNome, String tipoCliente, BigDecimal valorTotal, LocalDateTime dataEmissao, LocalDateTime dataCancelamento, LocalDateTime createdAt) {
        this.id = id;
        this.numero = numero;
        this.tipo = tipo;
        this.status = status;
        this.clienteNome = clienteNome;
        this.tipoCliente = tipoCliente;
        this.valorTotal = valorTotal;
        this.dataEmissao = dataEmissao;
        this.dataCancelamento = dataCancelamento;
        this.createdAt = createdAt;
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

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
