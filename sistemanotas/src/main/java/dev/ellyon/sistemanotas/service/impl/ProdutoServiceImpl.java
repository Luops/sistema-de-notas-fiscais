package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.produto.ProdutoRequestDTO;
import dev.ellyon.sistemanotas.model.Produto;
import dev.ellyon.sistemanotas.model.TipoProduto;
import dev.ellyon.sistemanotas.repository.ProdutoRepository;
import dev.ellyon.sistemanotas.repository.TipoProdutoRepository;
import dev.ellyon.sistemanotas.service.ProdutoService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProdutoServiceImpl implements ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final TipoProdutoRepository tipoProdutoRepository;

    public ProdutoServiceImpl(
            ProdutoRepository produtoRepository,
            TipoProdutoRepository tipoProdutoRepository
    ) {
        this.produtoRepository = produtoRepository;
        this.tipoProdutoRepository = tipoProdutoRepository;
    }

    @Override
    public Produto create(ProdutoRequestDTO dto) {
        if (produtoRepository.existsByCodigoProduto(dto.getCodigoProduto())) {
            throw new IllegalArgumentException("Já existe produto com o código informado. Código: " + dto.getCodigoProduto());
        }

        TipoProduto tipoProduto = tipoProdutoRepository.findById(dto.getTipoProduto())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de produto não encontrado. ID: " + dto.getTipoProduto()));

        Produto produto = new Produto();
        produto.setCodigoProduto(dto.getCodigoProduto());
        produto.setNome(dto.getNome());
        produto.setDescricaoProduto(dto.getDescricao());
        produto.setTipoProduto(tipoProduto);
        produto.setUnidade(dto.getUnidade());
        produto.setPrecoVenda(dto.getPrecoVenda());
        produto.setNcm(dto.getNcm());
        produto.setCfopPadrao(dto.getCfopPadrao());
        produto.setAliquotaIcmsPadrao(dto.getAliquotaIcmsPadrao());
        produto.setAliquotaPisPadrao(dto.getAliquotaPisPadrao());
        produto.setAliquotaCofinsPadrao(dto.getAliquotaCofinsPadrao());
        produto.setAtivo(dto.getAtivo());

        return produtoRepository.save(produto);
    }
}
