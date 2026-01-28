package dev.ellyon.sistemanotas.dto.nota;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class NotaListResponseDTO {
    private Long id;
    private String numero;
    private String tipo;
    private String status;
    private String clienteNome;
    private String tipoCliente;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dataEmissao;

    // Construtor padrão
    public NotaListResponseDTO() {

    }

    // Construtor completo


    public NotaListResponseDTO(Long id, String numero, String tipo, String status, String clienteNome, String tipoCliente, LocalDateTime dataEmissao) {
        this.id = id;
        this.numero = numero;
        this.tipo = tipo;
        this.status = status;
        this.clienteNome = clienteNome;
        this.tipoCliente = tipoCliente;
        this.dataEmissao = dataEmissao;
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

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public LocalDateTime getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDateTime dataEmissao) {
        this.dataEmissao = dataEmissao;
    }
}
