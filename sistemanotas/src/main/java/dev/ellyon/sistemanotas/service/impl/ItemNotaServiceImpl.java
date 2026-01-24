package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.itemNota.ItemNotaRequestDTO;
import dev.ellyon.sistemanotas.dto.itemNota.ItemNotaResponseDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoResponseDTO;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.EntityNotFoundException;
import dev.ellyon.sistemanotas.model.ItemNota;
import dev.ellyon.sistemanotas.model.Nota;
import dev.ellyon.sistemanotas.model.Produto;
import dev.ellyon.sistemanotas.model.enums.StatusNota;
import dev.ellyon.sistemanotas.repository.ItemNotaRepository;
import dev.ellyon.sistemanotas.repository.NotaRepository;
import dev.ellyon.sistemanotas.repository.ProdutoRepository;
import dev.ellyon.sistemanotas.service.ItemNotaService;
import dev.ellyon.sistemanotas.service.mapper.ItemNotaMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ItemNotaServiceImpl implements ItemNotaService {
    private final ItemNotaRepository itemNotaRepository;
    private final NotaRepository notaRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemNotaMapper itemNotaMapper;

    public ItemNotaServiceImpl(ItemNotaRepository itemNotaRepository, NotaRepository notaRepository, ProdutoRepository produtoRepository, ItemNotaMapper itemNotaMapper) {
        this.itemNotaRepository = itemNotaRepository;
        this.notaRepository = notaRepository;
        this.produtoRepository = produtoRepository;
        this.itemNotaMapper = itemNotaMapper;
    }

    @Override
    public ItemNotaResponseDTO adicionarItem(Long notaId, ItemNotaRequestDTO dto) {
       return null;
    }

    @Override
    public ItemNotaResponseDTO atualizarItem(Long notaId, Long itemId, ItemNotaRequestDTO dto) {
        return null;
    }

    @Override
    public void removerItem(Long notaId, Long itemId) {

    }

    @Override
    public List<ItemNotaResponseDTO> listarItensDaNota(Long notaId) {
        return List.of();
    }

    @Override
    public ItemNotaResponseDTO buscarItemPorId(Long notaId, Long itemId) {
        return null;
    }

    // ==================== MÉTODOS AUXILIARES ====================
/*
    private void validarNotaEditavel(Nota nota) {
        if (nota.getStatus() != StatusNota.RASCUNHO) {
            throw new BusinessException(
                    String.format("Não é possível editar nota com status %s. Apenas notas em RASCUNHO podem ser editadas.",
                            nota.getStatus())
            );
        }
    }

    private void validarAliquota(BigDecimal aliquota, String nome) {
        if (aliquota.compareTo(BigDecimal.ZERO) < 0 || aliquota.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException(
                    String.format("Alíquota de %s deve estar entre 0 e 100", nome)
            );
        }
    }

    private void recalcularTotaisDaNota(Nota nota) {
        // Busca todos os itens da nota
        List<ItemNota> itens = itemNotaRepository.findByNotaId(nota.getId());

        // Calcula totais
        BigDecimal valorProdutos = BigDecimal.ZERO;
        BigDecimal valorImpostos = BigDecimal.ZERO;

        for (ItemNota item : itens) {
            valorProdutos = valorProdutos.add(item.getSubtotal());
            valorImpostos = valorImpostos.add(item.getValorIcms())
                    .add(item.getValorPis())
                    .add(item.getValorCofins());
        }

        // Atualiza nota
        nota.setValorProdutos(valorProdutos);
        nota.setValorImpostosTotal(valorImpostos);
        nota.setValorTotal(valorProdutos.add(valorImpostos));

        notaRepository.save(nota);
    }*/
}
