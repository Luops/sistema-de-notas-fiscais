package dev.ellyon.sistemanotas.dto.empresa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CertificadoResponseDTO {

    private Boolean isAtivo;
    private String tipo;
    private String cnpj;
    private LocalDateTime validade;
    private LocalDateTime dataUpload;
    private Long diasParaVencer;
    private Boolean vencido;
    private Long tamanhoBytes;

    // Construtor padrao
    public CertificadoResponseDTO() {}

    // Construtor completo
    public CertificadoResponseDTO(Boolean isAtivo, String tipo, String cnpj, LocalDateTime validade, LocalDateTime dataUpload, Long diasParaVencer, Boolean vencido, Long tamanhoBytes) {
        this.isAtivo = isAtivo;
        this.tipo = tipo;
        this.cnpj = cnpj;
        this.validade = validade;
        this.dataUpload = dataUpload;
        this.diasParaVencer = diasParaVencer;
        this.vencido = vencido;
        this.tamanhoBytes = tamanhoBytes;
    }

    public Boolean getIsAtivo() {
        return isAtivo;
    }

    public void setIsAtivo(Boolean ativo) {
        this.isAtivo = ativo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public LocalDateTime getValidade() {
        return validade;
    }

    public void setValidade(LocalDateTime validade) {
        this.validade = validade;
    }

    public LocalDateTime getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(LocalDateTime dataUpload) {
        this.dataUpload = dataUpload;
    }

    public Long getDiasParaVencer() {
        return diasParaVencer;
    }

    public void setDiasParaVencer(Long diasParaVencer) {
        this.diasParaVencer = diasParaVencer;
    }

    public Boolean getVencido() {
        return vencido;
    }

    public void setVencido(Boolean vencido) {
        this.vencido = vencido;
    }

    public Long getTamanhoBytes() {
        return tamanhoBytes;
    }

    public void setTamanhoBytes(Long tamanhoBytes) {
        this.tamanhoBytes = tamanhoBytes;
    }
}