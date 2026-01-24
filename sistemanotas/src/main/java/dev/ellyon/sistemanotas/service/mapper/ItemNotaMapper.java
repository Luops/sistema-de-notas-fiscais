package dev.ellyon.sistemanotas.service.mapper;

import dev.ellyon.sistemanotas.dto.itemNota.ItemNotaResponseDTO;
import dev.ellyon.sistemanotas.model.ItemNota;
import org.springframework.stereotype.Component;

@Component
public class ItemNotaMapper {
    public ItemNotaResponseDTO toResponseDTO(ItemNota itemNota){
        if (itemNota == null) {
            return null;
        }
        return new ItemNotaResponseDTO(
                itemNota.getId(),
                itemNota.getProduto().getId(),
                itemNota.getCodigoProduto(),
                itemNota.getDescricaoProduto(),
                itemNota.getQuantidade(),
                itemNota.getUnidade().toString(),
                itemNota.getPrecoUnitario(),
                itemNota.getSubtotal(),
                itemNota.getNcm(),
                itemNota.getCfop(),
                itemNota.getAliquotaIcms(),
                itemNota.getValorIcms(),
                itemNota.getAliquotaPis(),
                itemNota.getValorPis(),
                itemNota.getAliquotaCofins(),
                itemNota.getValorCofins(),
                itemNota.getValorTotalItem(),
                itemNota.getCreatedAt(),
                itemNota.getUpdatedAt()
        );
    }
}
