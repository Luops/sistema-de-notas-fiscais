package dev.ellyon.sistemanotas.dto.nota;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NotaRequestDTO {
    @NotNull(message = "ID da empresa é obrigatório")
    private Long empresaId;

    private Long clienteId;

    @NotNull(message = "ID do usuario é obrigatório")
    private Long usuarioId;

    @Size(max = 500, message = "Observações devem ter no máximo 500 caracteres")
    private String observacoes;

    public NotaRequestDTO(Long empresaId, Long clienteId, Long usuarioId, String observacoes) {
        this.empresaId = empresaId;
        this.clienteId = clienteId;
        this.usuarioId = usuarioId;
        this.observacoes = observacoes;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
