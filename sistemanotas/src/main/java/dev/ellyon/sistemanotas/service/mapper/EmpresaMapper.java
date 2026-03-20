package dev.ellyon.sistemanotas.service.mapper;

import dev.ellyon.sistemanotas.dto.empresa.EmpresaListResponseDTO;
import dev.ellyon.sistemanotas.dto.empresa.EmpresaResponseDTO;
import dev.ellyon.sistemanotas.model.Empresa;
import dev.ellyon.sistemanotas.utils.FormatUtils;
import org.springframework.stereotype.Component;

@Component
public class EmpresaMapper {
    public EmpresaResponseDTO toResponseDTO(Empresa empresa){
        return new EmpresaResponseDTO(
                empresa.getId(),
                empresa.getRazaoSocial(),
                empresa.getNomeFantasia(),
                FormatUtils.formatCpfCnpj(empresa.getCnpj()),
                empresa.getInscricaoEstadual(),
                empresa.getEnderecoCompleto(),
                empresa.getCidade(),
                empresa.getEstadoUF(),
                FormatUtils.formatCep(empresa.getCep()),
                FormatUtils.formatTelefone(empresa.getTelefone()),
                empresa.getEmail(),
                empresa.getLogoUrl(),
                empresa.getAtivo(),
                empresa.getCreatedAt(),
                empresa.getUpdatedAt()
        );
    }

    public EmpresaListResponseDTO toListResponseDTO(Empresa empresa){
        return new EmpresaListResponseDTO(
                empresa.getId(),
                empresa.getRazaoSocial(),
                empresa.getNomeFantasia(),
                FormatUtils.formatCpfCnpj(empresa.getCnpj()),
                empresa.getCidade(),
                empresa.getEstadoUF(),
                FormatUtils.formatTelefone(empresa.getTelefone()),
                empresa.getEmail(),
                empresa.getAtivo()
        );
    }
}
