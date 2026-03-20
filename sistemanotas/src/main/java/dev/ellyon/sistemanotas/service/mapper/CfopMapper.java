package dev.ellyon.sistemanotas.service.mapper;

import dev.ellyon.sistemanotas.dto.cfop.CfopResponseDTO;
import dev.ellyon.sistemanotas.dto.cfop.CfopSimpleDTO;
import dev.ellyon.sistemanotas.model.Cfop;
import org.springframework.stereotype.Component;

@Component
public class CfopMapper {
    public CfopResponseDTO toResponseDTO(Cfop cfop) {
        return new CfopResponseDTO(
                cfop.getId(),
                cfop.getCodigo(),
                cfop.getDescricao(),
                cfop.getAplicacao(),
                cfop.getTipo(),
                cfop.getNatureza(),
                cfop.getOperacao(),
                cfop.getAtivo(),
                cfop.getCreatedAt(),
                cfop.getUpdatedAt()
        );
    }

    public CfopSimpleDTO toSimpleDTO(Cfop cfop) {
        return new CfopSimpleDTO(
                cfop.getId(),
                cfop.getCodigo(),
                cfop.getDescricao(),
                cfop.getTipo(),
                cfop.getNatureza(),
                cfop.getOperacao()
        );
    }
}
