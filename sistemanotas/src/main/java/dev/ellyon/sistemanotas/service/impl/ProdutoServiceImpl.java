package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.produto.ProdutoListResponseDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoResponseDTO;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.EntityNotFoundException;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.Produto;
import dev.ellyon.sistemanotas.model.TipoProduto;
import dev.ellyon.sistemanotas.repository.ProdutoRepository;
import dev.ellyon.sistemanotas.repository.TipoProdutoRepository;
import dev.ellyon.sistemanotas.service.ProdutoService;
import dev.ellyon.sistemanotas.service.mapper.ProdutoMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProdutoServiceImpl implements ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final TipoProdutoRepository tipoProdutoRepository;
    private final ProdutoMapper produtoMapper;

    public ProdutoServiceImpl(
            ProdutoRepository produtoRepository,
            TipoProdutoRepository tipoProdutoRepository,
            ProdutoMapper produtoMapper
    ) {
        this.produtoRepository = produtoRepository;
        this.tipoProdutoRepository = tipoProdutoRepository;
        this.produtoMapper = produtoMapper;
    }

    // Criar um novo produto
    @Override
    public ProdutoResponseDTO create(ProdutoRequestDTO dto) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        /*
        * VALIDAÇÕES
        * */
        // Código obrigatório, único, max 50
        if (dto.getCodigoProduto() == null || dto.getCodigoProduto().trim().isEmpty()) {
            errors.put("codigoProduto", "Código do produto é obrigatório");
        }
        if (produtoRepository.existsByCodigoProduto(dto.getCodigoProduto())) {
            throw new BusinessException("Já existe produto com o código informado: " + dto.getCodigoProduto());
        }

        // Nome: min 3, max 255
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            errors.put("nome", "Nome do produto é obrigatório");
        } else if (dto.getNome().trim().length() < 3) {
            errors.put("nome", "Nome do produto deve ter no mínimo 3 caracteres");
        } else if (dto.getNome().length() > 255) {
            errors.put("nome", "Nome do produto deve ter no máximo 255 caracteres");
        }

        // Validação de preço
        if (dto.getPrecoVenda() == null) {
            errors.put("precoVenda", "Preço de venda é obrigatório");
        } else if (dto.getPrecoVenda().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("precoVenda", "Preço de venda deve ser maior que zero");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        // Busca o tipo de produto (lança EntityNotFoundException se não existir)
        TipoProduto tipoProduto = tipoProdutoRepository.findById(dto.getTipoProduto())
                .orElseThrow(() -> new EntityNotFoundException("TipoProduto", dto.getTipoProduto()));


        /*
        * CRIAÇÃO DO PRODUTO
        * */
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
        produto.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);

        /*
        * SALVA E RETORNA DTO DE RESPOSTA
        * */
        Produto produtoSalvo = produtoRepository.save(produto);
        return produtoMapper.toResponseDTO(produtoSalvo);
    }

    // Deletar um produto
    @Override
    public void delete(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto", id));

        if (produto.getId() == null) {
            throw new BusinessException("Produto não existe!");
        }

        produtoRepository.delete(produto);
    }

    // Atualizar um produto
    @Override
    public ProdutoResponseDTO update(Long id, ProdutoRequestDTO dto) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        /*
         * VALIDAÇÕES
         * */
        // Código obrigatório, único, max 50
        if (dto.getCodigoProduto() == null || dto.getCodigoProduto().trim().isEmpty()) {
            errors.put("codigoProduto", "Código do produto é obrigatório");
        }

        // Nome: min 3, max 255
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            errors.put("nome", "Nome do produto é obrigatório");
        } else if (dto.getNome().trim().length() < 3) {
            errors.put("nome", "Nome do produto deve ter no mínimo 3 caracteres");
        } else if (dto.getNome().length() > 255) {
            errors.put("nome", "Nome do produto deve ter no máximo 255 caracteres");
        }

        // Validação de preço
        if (dto.getPrecoVenda() == null) {
            errors.put("precoVenda", "Preço de venda é obrigatório");
        } else if (dto.getPrecoVenda().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("precoVenda", "Preço de venda deve ser maior que zero");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        // Busca o tipo de produto (lança EntityNotFoundException se não existir)
        TipoProduto tipoProduto = tipoProdutoRepository.findById(dto.getTipoProduto())
                .orElseThrow(() -> new EntityNotFoundException("TipoProduto", dto.getTipoProduto()));

        /*
         * ATUALIZAÇÃO DO PRODUTO
         * */
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto", id));
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
        produto.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : produto.getAtivo());

        /*
         * SALVA E RETORNA DTO DE RESPOSTA
         * */
        Produto produtoAtualizado = produtoRepository.save(produto);
        return produtoMapper.toResponseDTO(produtoAtualizado);
    }

    // Desativar um produto (soft delete)
    @Override
    public void softDelete(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto", id));

        if (produto.getId() == null) {
            throw new BusinessException("Produto não existe!");
        }

        produto.setAtivo(false);
        produtoRepository.save(produto);

    }

    // Ativar um produto
    @Override
    public void activate(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto", id));

        if (produto.getId() == null) {
            throw new BusinessException("Produto não existe!");
        }

        produto.setAtivo(true);
        produtoRepository.save(produto);
    }

    // Buscar produto por ID
    @Override
    public ProdutoResponseDTO findById(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto", id));
        return produtoMapper.toResponseDTO(produto);
    }

    // Buscar todos os produtos
    @Override
    public List<ProdutoListResponseDTO> findAll() {
        List<Produto> produtos = produtoRepository.findAll();
        return produtos.stream()
                .map(produtoMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar produtos por tipo de produto ID
    @Override
    public List<ProdutoListResponseDTO> findByTipoProdutoId(Long tipoProdutoId) {
        List<Produto> produtos = produtoRepository.findByTipoProdutoId(tipoProdutoId);
        if (produtos.isEmpty()) {
            throw new EntityNotFoundException("Nenhum produto encontrado para o Tipo Produto ID: " + tipoProdutoId);
        }
        return produtos.stream()
                .map(produtoMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar produtos por tipo de produto nome
    @Override
    public List<ProdutoListResponseDTO> findByTipoProdutoNome(String tipoProdutoNome) {
        List<Produto> produtos = produtoRepository.findByTipoProdutoNome(tipoProdutoNome);
        if (produtos.isEmpty()) {
            throw new EntityNotFoundException("Nenhum produto encontrado para o Tipo Produto nome: " + tipoProdutoNome);
        }
        return produtos.stream()
                .map(produtoMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar produtos por status (ativo/inativo)
    @Override
    public List<ProdutoListResponseDTO> findByIsAtivo(Boolean ativo) {
        List<Produto> produtos = produtoRepository.findByIsAtivo(ativo);
        if (produtos.isEmpty()) {
            String status = ativo ? "ativos" : "inativos";
            throw new EntityNotFoundException("Nenhum produto " + status + " encontrado.");
        }
        return produtos.stream()
                .map(produtoMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar produtos por nome (contendo, case insensitive)
    @Override
    public List<ProdutoListResponseDTO> findByNomeContainingIgnoreCase(String nome) {
        List<Produto> produtos = produtoRepository.findByNomeContainingIgnoreCase(nome);
        if (produtos.isEmpty()) {
            throw new EntityNotFoundException("Nenhum produto encontrado contendo o nome: " + nome);
        }
        return produtos.stream()
                .map(produtoMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar produtos por código (contendo, case insensitive)
    @Override
    public List<ProdutoListResponseDTO> findByCodigoProdutoContainingIgnoreCase(String codigoProduto) {
        List<Produto> produtos = produtoRepository.findByCodigoProdutoContainingIgnoreCase(codigoProduto);
        if (produtos.isEmpty()) {
            throw new EntityNotFoundException("Nenhum produto encontrado contendo o código: " + codigoProduto);
        }
        return produtos.stream()
                .map(produtoMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar produtos por faixa de preço de venda
    @Override
    public List<ProdutoListResponseDTO> findByPrecoVendaBetween(BigDecimal precoMinimo, BigDecimal precoMaximo) {
        List<Produto> produtos = produtoRepository.findByPrecoVendaBetween(precoMinimo, precoMaximo);
        if (produtos.isEmpty()) {
            throw new EntityNotFoundException("Nenhum produto encontrado na faixa de preço: " + "R$ " + precoMinimo + " ~ " + "R$ " + precoMaximo);
        }
        return produtos.stream()
                .map(produtoMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar produtos por faixa de data de criação
    @Override
    public List<ProdutoListResponseDTO> findByCreatedAtBetween(java.time.LocalDateTime dataInicio, java.time.LocalDateTime dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new BusinessException("Data de início e data de fim são obrigatórias");
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new BusinessException("Data de início não pode ser posterior à data de fim");
        }
            List<Produto> produtos = produtoRepository.findByCreatedAtBetween(dataInicio, dataFim);

        if (produtos.isEmpty()) {
            throw new EntityNotFoundException(
                    String.format("Nenhum produto encontrado entre %s e %s",
                            dataInicio.toLocalDate(), dataFim.toLocalDate())
            );
        }
        return produtos.stream()
                .map(produtoMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar todos os produtos com paginação
    @Override
    public Page<ProdutoListResponseDTO> findAllPaged(Pageable pageable) {
        Page<Produto> produtosPage = produtoRepository.findAll(pageable);

        if (produtosPage.isEmpty()) {
            throw new EntityNotFoundException("Nenhum produto encontrado");
        }

        // Converte Page<Produto> para Page<ProdutoListResponseDTO>
        return produtosPage.map(produtoMapper::toListResponseDTO);
    }
}
