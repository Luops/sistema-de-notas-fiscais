package dev.ellyon.sistemanotas.dto.empresaUsuario;

import dev.ellyon.sistemanotas.model.enums.Perfil;

public class EmpresaUsuarioSimpleResponseDTO {
    private Long empresaId;
    private String nomeFantasia;
    private Perfil perfil;

    // Construtor padrao
    public EmpresaUsuarioSimpleResponseDTO() {

    }

    // Construtor completo
    public EmpresaUsuarioSimpleResponseDTO(Long empresaId, String nomeFantasia, Perfil perfil) {
        this.empresaId = empresaId;
        this.nomeFantasia = nomeFantasia;
        this.perfil = perfil;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }
}
