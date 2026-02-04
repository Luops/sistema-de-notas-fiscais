package dev.ellyon.sistemanotas.dto.empresaUsuario;

import dev.ellyon.sistemanotas.model.enums.Perfil;
import jakarta.validation.constraints.NotNull;

public class EmpresaUsuarioRequestDTO {
    @NotNull(message = "O ID da empresa não pode ser nulo")
    private Long empresaId;

    @NotNull(message = "O ID do usuário não pode ser nulo")
    private Long usuarioId;

    @NotNull(message = "O perfil do usuário não pode ser nulo")
    private String perfil;

    public EmpresaUsuarioRequestDTO(Long empresaId, Long usuarioId, String perfil) {
        this.empresaId = empresaId;
        this.usuarioId = usuarioId;
        this.perfil = perfil;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }
}
