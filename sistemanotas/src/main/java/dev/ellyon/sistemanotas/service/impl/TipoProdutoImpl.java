package dev.ellyon.sistemanotas.service.impl;


import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoResponseDTO;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.EntityNotFoundException;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.TipoProduto;
import dev.ellyon.sistemanotas.repository.TipoProdutoRepository;
import dev.ellyon.sistemanotas.service.TipoProdutoService;
import dev.ellyon.sistemanotas.service.mapper.TipoProdutoMapper;
import dev.ellyon.sistemanotas.utils.FormatUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class TipoProdutoImpl implements TipoProdutoService {
    private final TipoProdutoRepository tipoProdutoRepository;
    private final TipoProdutoMapper tipoProdutoMapper;
    public TipoProdutoImpl(TipoProdutoRepository tipoProdutoRepository, TipoProdutoMapper tipoProdutoMapper) {
        this.tipoProdutoRepository = tipoProdutoRepository;
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
        if (tipoProduto.getId() == null) {
            throw new BusinessException("Tipo de produto não existe!");
        }

        tipoProdutoRepository.deleteById(id);

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

    @Override
    public void softDelete(Long id) {

    }

    @Override
    public void activate(Long id) {

    }

    @Override
    public TipoProdutoResponseDTO findById(Long id) {
        return null;
    }

    @Override
    public List<TipoProdutoResponseDTO> findAll() {
        return List.of();
    }

    @Override
    public List<TipoProdutoResponseDTO> findByAtivoInativo(Boolean ativo) {
        return List.of();
    }

    @Override
    public TipoProdutoResponseDTO findByNomeContainingIgnoreCase(String nome) {
        return null;
    }

    @Override
    public List<TipoProdutoResponseDTO> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fim) {
        return List.of();
    }
}
