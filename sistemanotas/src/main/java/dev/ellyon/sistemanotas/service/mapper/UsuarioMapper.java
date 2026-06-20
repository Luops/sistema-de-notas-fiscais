package dev.ellyon.sistemanotas.service.mapper;

import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioSimpleResponseDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioResponseDTO;
import dev.ellyon.sistemanotas.model.EmpresaUsuario;
import dev.ellyon.sistemanotas.model.Usuario;
import dev.ellyon.sistemanotas.repository.EmpresaUsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {
    private final EmpresaUsuarioRepository empresaUsuarioRepository;
    public UsuarioMapper(EmpresaUsuarioRepository empresaUsuarioRepository) {
        this.empresaUsuarioRepository = empresaUsuarioRepository;
    }

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setTelefone(usuario.getTelefone());
        dto.setCidade(usuario.getCidade());
        dto.setEndereco(usuario.getEndereco());
        dto.setCep(usuario.getCep());
        dto.setNumeroEndereco(usuario.getNumeroEndereco());
        dto.setIsAtivo(usuario.getIsAtivo());
        dto.setCreatedAt(usuario.getCreatedAt());
        dto.setUpdatedAt(usuario.getUpdatedAt());

        // Buscar empresas associadas ao usuário
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());
        List<EmpresaUsuarioSimpleResponseDTO> empresasDTO = empresasUsuario.stream()
                .map(eu -> new EmpresaUsuarioSimpleResponseDTO(
                        eu.getEmpresa().getId(),
                        eu.getEmpresa().getNomeFantasia(),
                        eu.getPerfil()
                ))
                .collect(Collectors.toList());

        dto.setEmpresas(empresasDTO);

        return dto;

    }
}
