package dev.ellyon.sistemanotas.service.mapper;

import dev.ellyon.sistemanotas.dto.produto.ProdutoListResponseDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoResponseDTO;
import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoSimpleDTO;
import dev.ellyon.sistemanotas.model.Produto;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {
    public ProdutoResponseDTO toResponseDTO(Produto produto){
        TipoProdutoSimpleDTO tipoProdutoDTO = new TipoProdutoSimpleDTO(
                produto.getTipoProduto().getId(),
                produto.getTipoProduto().getNome()
        );

        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getCodigoProduto(),
                produto.getNome(),
                produto.getDescricaoProduto(),
                tipoProdutoDTO,
                produto.getUnidade().toString(),
                produto.getPrecoVenda(),
                produto.getNcm(),
                produto.getCfopPadrao(),
                produto.getAliquotaIcmsPadrao(),
                produto.getAliquotaPisPadrao(),
                produto.getAliquotaCofinsPadrao(),
                produto.getAtivo(),
                produto.getCreatedAt(),
                produto.getUpdatedAt()
        );
    }

    public ProdutoListResponseDTO toListResponseDTO(Produto produto){
        return new ProdutoListResponseDTO(
                produto.getId(),
                produto.getCodigoProduto(),
                produto.getNome(),
                produto.getTipoProduto().getNome(),
                produto.getUnidade().toString(),
                produto.getPrecoVenda(),
                produto.getAtivo()
        );
    }
}
