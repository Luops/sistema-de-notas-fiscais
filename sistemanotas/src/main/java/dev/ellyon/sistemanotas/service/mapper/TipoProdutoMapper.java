package dev.ellyon.sistemanotas.service.mapper;

import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoResponseDTO;
import dev.ellyon.sistemanotas.model.TipoProduto;
import dev.ellyon.sistemanotas.utils.FormatUtils;
import org.springframework.stereotype.Component;

@Component
public class TipoProdutoMapper {
    public TipoProdutoResponseDTO toResponseDTO(TipoProduto tipoProduto){
        return new TipoProdutoResponseDTO(
                tipoProduto.getId(),
                tipoProduto.getNome(),
                tipoProduto.getAtivo(),
                tipoProduto.getCreatedAt(),
                tipoProduto.getUpdatedAt()
        );
    }
}
