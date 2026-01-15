package dev.ellyon.sistemanotas.service.mapper;

import dev.ellyon.sistemanotas.dto.empresa.EmpresaResponseDTO;
import dev.ellyon.sistemanotas.model.Empresa;
import org.springframework.stereotype.Component;

@Component
public class EmpresaMapper {
    public EmpresaResponseDTO toResponseDTO(Empresa empresa){
        return new EmpresaResponseDTO(
                empresa.getId(),
                empresa.getRazaoSocial(),
                empresa.getNomeFantasia(),
                empresa.getCnpj(),
                empresa.getInscricaoEstadual(),
                empresa.getEnderecoCompleto(),
                empresa.getCidade(),
                empresa.getEstadoUF(),
                empresa.getCep(),
                empresa.getTelefone(),
                empresa.getEmail(),
                empresa.getLogoUrl(),
                empresa.getAtivo(),
                empresa.getCreatedAt(),
                empresa.getUpdatedAt()
        );
    }
}
