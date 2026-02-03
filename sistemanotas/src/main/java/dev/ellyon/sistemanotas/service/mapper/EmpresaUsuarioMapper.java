package dev.ellyon.sistemanotas.service.mapper;

import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioResponseDTO;
import dev.ellyon.sistemanotas.model.EmpresaUsuario;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EmpresaUsuarioMapper {
    public EmpresaUsuarioResponseDTO toResponseDTO(EmpresaUsuario empresaUsuario) {
        return new EmpresaUsuarioResponseDTO(
                empresaUsuario.getUsuario().getId(),
                empresaUsuario.getUsuario().getNome(),
                empresaUsuario.getUsuario().getEmail(),
                empresaUsuario.getUsuario().getIsAtivo(),
                empresaUsuario.getPerfil().name(),
                empresaUsuario.getUsuario().getCreatedAt()
        );
    }

    // Método com parâmetros individuais (se precisar)
    public EmpresaUsuarioResponseDTO toResponseDTO(Long id, String nome, String email,
                                                   Boolean isAtivo, String perfil,
                                                   LocalDateTime createdAt) {
        return new EmpresaUsuarioResponseDTO(
                id,
                nome,
                email,
                isAtivo,
                perfil,
                createdAt
        );
    }

}
