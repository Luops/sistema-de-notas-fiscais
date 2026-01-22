package dev.ellyon.sistemanotas.service.impl;


import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoResponseDTO;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.EntityNotFoundException;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.TipoProduto;
import dev.ellyon.sistemanotas.repository.ProdutoRepository;
import dev.ellyon.sistemanotas.repository.TipoProdutoRepository;
import dev.ellyon.sistemanotas.service.TipoProdutoService;
import dev.ellyon.sistemanotas.service.mapper.TipoProdutoMapper;
import dev.ellyon.sistemanotas.utils.FormatUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class TipoProdutoImpl implements TipoProdutoService {
    private final TipoProdutoRepository tipoProdutoRepository;
    private final ProdutoRepository produtoRepository;
    private final TipoProdutoMapper tipoProdutoMapper;
    public TipoProdutoImpl(TipoProdutoRepository tipoProdutoRepository, ProdutoRepository produtoRepository, TipoProdutoMapper tipoProdutoMapper) {
        this.tipoProdutoRepository = tipoProdutoRepository;
        this.produtoRepository = produtoRepository;
        this.tipoProdutoMapper = tipoProdutoMapper;
    }

    // Criar um tipo de produto
    @Override
    public TipoProdutoResponseDTO create(TipoProdutoRequestDTO dto) {
        // declaracao dos erros
        Map<String, String> errors = new HashMap<>();

        // Verifica se o campo nome esta vazio
        if (dto.getNome() == null || dto.getNome().isEmpty()) {
            errors.put("nome", "O campo nome é obrigatório.");
        }

        if (dto.getNome().length() < 3 || dto.getNome().length() > 100){
            errors.put("nome", "O nome deve ter entre 3 e 100 caracteres.");
        }

        // Deixar a primeira letra do nome em maiusculo para evitar duplicidade
        String nomeCapitalize = FormatUtils.capitalizeNome(dto.getNome());

        // Verifica se ja existe um tipo de produto com o mesmo nome
        if (tipoProdutoRepository.existsByNomeIgnoreCase(nomeCapitalize)) {
            errors.put("nome", "Já existe um tipo de produto com este nome.");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        /*
        * CRIAR TIPO DE PRODUTO
        * */
        TipoProduto tipoProduto = new TipoProduto();
        tipoProduto.setNome(nomeCapitalize);
        tipoProduto.setAtivo(true);

        // Salva o tipo de produto no banco de dados
        TipoProduto savedTipoProduto = tipoProdutoRepository.save(tipoProduto);
        return tipoProdutoMapper.toResponseDTO(savedTipoProduto);
    }

    // Deletar um tipo de produto
    @Override
    public void delete(Long id) {
        TipoProduto tipoProduto = tipoProdutoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de produto não encontrado com o ID", id));

        // Verifica se existe produtos associados a esse tipo de produto
        long quantidadeProdutosAssociados = produtoRepository.countByTipoProdutoId(id);
        if (quantidadeProdutosAssociados > 0) {
            throw new BusinessException(
                    String.format("Não é possível excluir este tipo de produto. Existem %d produto(s) cadastrado(s) com este tipo. Desative-o ao invés de excluir.",
                            quantidadeProdutosAssociados)
            );
        }

        tipoProdutoRepository.delete(tipoProduto);

    }

    // Atualizar um tipo de produto
    @Override
    public TipoProdutoResponseDTO update(Long id, TipoProdutoRequestDTO dto) {
        // declaracao dos erros
        Map<String, String> errors = new HashMap<>();

        // Verifica se o tipo de produto existe
        TipoProduto tipoProdutoExistente = tipoProdutoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de produto não encontrado com o ID", id));

        // Verifica se o campo nome esta vazio
        if (dto.getNome() == null || dto.getNome().isEmpty()) {
            errors.put("nome", "O campo nome é obrigatório.");
        }

        // Deixar a primeira letra do nome em maiusculo para evitar duplicidade
        String nomeCapitalize = FormatUtils.capitalizeNome(dto.getNome());

        // Verifica se ja existe um tipo de produto com o mesmo nome, se id for diferente do id do tipo de produto que esta sendo atualizado
        if(nomeCapitalize.isEmpty()){
            errors.put("nome", "O nome não pode ser vazio.");
        } else if (nomeCapitalize.length() < 3 || nomeCapitalize.length() > 100){
            errors.put("nome", "O nome deve ter entre 3 e 100 caracteres.");
        } else {
            // Buscar o tipo de produto pelo id
            Optional<TipoProduto> existingTipoProduto = tipoProdutoRepository.findByNome(nomeCapitalize);

            // Verifica se o tipo de produto existe
            if (existingTipoProduto.isPresent()) {
                // Verifica se NÃO é a mesmo tipo de produto sendo editado
                if (!existingTipoProduto.get().getId().equals(id)) {
                    errors.put("nome", "Já existe um tipo de produto com este nome.");
                }
            }
        }


        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        /*
         * EDITA TIPO DE PRODUTO
         * */
        tipoProdutoExistente.setNome(nomeCapitalize);
        tipoProdutoExistente.setAtivo(true);

        // Salva o tipo de produto no banco de dados
        TipoProduto updatedTipoProduto = tipoProdutoRepository.save(tipoProdutoExistente);
        return tipoProdutoMapper.toResponseDTO(updatedTipoProduto);
    }

    // Desativar um tipo de produto
    @Override
    public void softDelete(Long id) {
        TipoProduto tipoProduto = tipoProdutoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de produto não encontrado com o ID", id));
        if (tipoProduto.getId() == null) {
            throw new BusinessException("Tipo de produto não existe.");
        }

        tipoProduto.setAtivo(false);
        tipoProdutoRepository.save(tipoProduto);
    }

    // Ativar um tipo de produto
    @Override
    public void activate(Long id) {
        TipoProduto tipoProduto = tipoProdutoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de produto não encontrado com o ID", id));
        if (tipoProduto.getId() == null) {
            throw new BusinessException("Tipo de produto não existe.");
        }
        tipoProduto.setAtivo(true);
        tipoProdutoRepository.save(tipoProduto);
    }

    // Buscar um tipo de produto por ID
    @Override
    public TipoProdutoResponseDTO findById(Long id) {
        TipoProduto tipoProduto = tipoProdutoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de produto não encontrado com o ID", id));
        return tipoProdutoMapper.toResponseDTO(tipoProduto);
    }

    // Buscar todos os tipos de produtos
    @Override
    public List<TipoProdutoResponseDTO> findAll() {
        List<TipoProduto> tipoProdutos = tipoProdutoRepository.findAll();
        if(tipoProdutos.isEmpty()){
            throw new EntityNotFoundException("Nenhum tipo de produto encontrado.");
        }
        return tipoProdutos.stream().map(tipoProdutoMapper::toResponseDTO).toList();
    }

    // Buscar tipos de produtos por status (ativo/inativo)
    @Override
    public List<TipoProdutoResponseDTO> findByAtivoInativo(Boolean ativo) {
        List<TipoProduto> tipoProdutos = tipoProdutoRepository.findByIsAtivo(ativo);
        if(tipoProdutos.isEmpty()) {
            throw new EntityNotFoundException("Nenhum tipo de produto encontrado com o status informado.");
        }
        return tipoProdutos.stream().map(tipoProdutoMapper::toResponseDTO).toList();
    }

    // Buscar tipos de produtos por nome (contendo, case insensitive)
    @Override
    public List<TipoProdutoResponseDTO> findByNomeContainingIgnoreCase(String nome) {
        List<TipoProduto> tipoProdutos = tipoProdutoRepository.findByNomeContainingIgnoreCase(nome);
        if(tipoProdutos.isEmpty()) {
            throw new EntityNotFoundException("Nenhum tipo de produto encontrado com o nome informado.");
        }
        return tipoProdutos.stream().map(tipoProdutoMapper::toResponseDTO).toList();
    }

    // Buscar tipos de produtos criados entre duas datas
    @Override
    public List<TipoProdutoResponseDTO> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            throw new BusinessException("Data de início e data de fim são obrigatórias");
        }

        if (inicio.isAfter(fim)) {
            throw new BusinessException("Data de início não pode ser posterior à data de fim");
        }

        List<TipoProduto> tipoProdutos = tipoProdutoRepository.findByCreatedAtBetween(inicio, fim);
        if(tipoProdutos.isEmpty()) {
            // Formata a data para o padrao brasileiro
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataInicioFormatada = inicio.format(formatter);
            String dataFimFormatada = fim.format(formatter);

            throw new EntityNotFoundException(
                    String.format("Nenhuma tipo de produto encontrado entre %s e %s",
                            dataInicioFormatada, dataFimFormatada)
            );
        }
        return tipoProdutos.stream().map(tipoProdutoMapper::toResponseDTO).toList();
    }
}
