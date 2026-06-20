package dev.ellyon.sistemanotas.service.impl;


import com.beust.ah.A;
import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoResponseDTO;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.EntityNotFoundException;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.Empresa;
import dev.ellyon.sistemanotas.model.EmpresaUsuario;
import dev.ellyon.sistemanotas.model.TipoProduto;
import dev.ellyon.sistemanotas.model.Usuario;
import dev.ellyon.sistemanotas.repository.EmpresaUsuarioRepository;
import dev.ellyon.sistemanotas.repository.ProdutoRepository;
import dev.ellyon.sistemanotas.repository.TipoProdutoRepository;
import dev.ellyon.sistemanotas.repository.UsuarioRepository;
import dev.ellyon.sistemanotas.service.TipoProdutoService;
import dev.ellyon.sistemanotas.service.mapper.TipoProdutoMapper;
import dev.ellyon.sistemanotas.utils.FormatUtils;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
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
    private final UsuarioRepository usuarioRepository;
    private final EmpresaUsuarioRepository empresaUsuarioRepository;
    private final TipoProdutoMapper tipoProdutoMapper;
    public TipoProdutoImpl(TipoProdutoRepository tipoProdutoRepository, ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository, EmpresaUsuarioRepository empresaUsuarioRepository, TipoProdutoMapper tipoProdutoMapper) {
        this.tipoProdutoRepository = tipoProdutoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaUsuarioRepository = empresaUsuarioRepository;
        this.tipoProdutoMapper = tipoProdutoMapper;
    }

    // Criar um tipo de produto
    @Override
    public TipoProdutoResponseDTO create(TipoProdutoRequestDTO dto, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresa = empresasUsuario.get(0).getEmpresa();

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

        // Cria o tipo de produto
        TipoProduto tipoProduto = new TipoProduto();
        tipoProduto.setNome(nomeCapitalize);
        tipoProduto.setIsAtivo(true);
        tipoProduto.setEmpresa(empresa);

        // Salva o tipo de produto no banco de dados
        TipoProduto savedTipoProduto = tipoProdutoRepository.save(tipoProduto);
        return tipoProdutoMapper.toResponseDTO(savedTipoProduto);
    }

    // Deletar um tipo de produto
    @Override
    public void delete(Long id, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresa = empresasUsuario.get(0).getEmpresa();

        // Verifica se o tipo de produto existe
        TipoProduto tipoProduto = tipoProdutoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de produto não encontrado com o ID", id));

        // Verificar se a empresa do tipo de produto é a mesma do usuário logado
        if (!tipoProduto.getEmpresa().getId().equals(empresa.getId())) {
            throw new BusinessException("Tipo de produto não pertence à empresa do usuário logado");
        }

        // Verifica se existe produtos associados a esse tipo de produto
        long quantidadeProdutosAssociados = produtoRepository.countByTipoProdutoId(id);
        if (quantidadeProdutosAssociados > 0) {
            throw new BusinessException(
                    String.format("Não é possível excluir este tipo de produto. Existem %d produto(s) cadastrado(s) com este tipo. Desative-o ao invés de excluir.",
                            quantidadeProdutosAssociados)
            );
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        tipoProdutoRepository.delete(tipoProduto);

    }

    // Atualizar um tipo de produto
    @Override
    public TipoProdutoResponseDTO update(Long id, TipoProdutoRequestDTO dto, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresa = empresasUsuario.get(0).getEmpresa();

        // Verifica se o tipo de produto existe
        TipoProduto tipoProdutoExistente = tipoProdutoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de produto não encontrado com o ID", id));

        // Verificar se a empresa do tipo de produto é a mesma do usuário logado
        if (!tipoProdutoExistente.getEmpresa().getId().equals(empresa.getId())) {
            throw new BusinessException("Tipo de produto não pertence à empresa do usuário logado");
        }

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

        // Atualiza os dados do tipo de produto existente com os dados do DTO
        tipoProdutoExistente.setNome(nomeCapitalize);
        tipoProdutoExistente.setIsAtivo(true);

        // Salva o tipo de produto no banco de dados
        TipoProduto updatedTipoProduto = tipoProdutoRepository.save(tipoProdutoExistente);
        return tipoProdutoMapper.toResponseDTO(updatedTipoProduto);
    }

    // Desativar um tipo de produto
    @Override
    public void softDelete(Long id, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresa = empresasUsuario.get(0).getEmpresa();

        // Verifica se o tipo de produto existe
        TipoProduto tipoProduto = tipoProdutoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de produto não encontrado com o ID", id));

        // Verificar se a empresa do tipo de produto é a mesma do usuário logado
        if (!tipoProduto.getEmpresa().getId().equals(empresa.getId())) {
            throw new BusinessException("Tipo de produto não pertence à empresa do usuário logado");
        }

        // Verifica se existe produtos associados a esse tipo de produto
        long quantidadeProdutosAssociados = produtoRepository.countByTipoProdutoId(id);
        if (quantidadeProdutosAssociados > 0) {
            throw new BusinessException(
                    String.format("Não é possível desativar este tipo de produto. Existem %d produto(s) cadastrado(s) com este tipo. Desative os produtos associados ou mude o tipo de produto dos produtos associados para outro tipo antes de desativar.",
                            quantidadeProdutosAssociados)
            );
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        tipoProduto.setIsAtivo(false);
        tipoProdutoRepository.save(tipoProduto);

    }

    // Ativar um tipo de produto
    @Override
    public void activate(Long id, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresa = empresasUsuario.get(0).getEmpresa();

        // Verifica se o tipo de produto existe
        TipoProduto tipoProduto = tipoProdutoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de produto não encontrado com o ID", id));

        // Verificar se a empresa do tipo de produto é a mesma do usuário logado
        if (!tipoProduto.getEmpresa().getId().equals(empresa.getId())) {
            throw new BusinessException("Tipo de produto não pertence à empresa do usuário logado");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        tipoProduto.setIsAtivo(true);
        tipoProdutoRepository.save(tipoProduto);
    }

    // Buscar um tipo de produto por ID
    @Override
    public TipoProdutoResponseDTO findById(Long id, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresa = empresasUsuario.get(0).getEmpresa();

        // Verifica se o tipo de produto existe
        TipoProduto tipoProduto = tipoProdutoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de produto não encontrado com o ID", id));

        // Verificar se a empresa do tipo de produto é a mesma do usuário logado
        if (!tipoProduto.getEmpresa().getId().equals(empresa.getId())) {
            throw new BusinessException("Tipo de produto não pertence à empresa do usuário logado");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        return tipoProdutoMapper.toResponseDTO(tipoProduto);
    }

    // Buscar todos os tipos de produtos
    @Override
    public List<TipoProdutoResponseDTO> findAll(Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresa = empresasUsuario.get(0).getEmpresa();

        // Buscar todos os tipos de produtos da empresa do usuário logado
        List<TipoProduto> tipoProdutos = tipoProdutoRepository.findByEmpresaId(empresa.getId());
        if(tipoProdutos.isEmpty()){
            throw new EntityNotFoundException("Nenhum tipo de produto encontrado.");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        return tipoProdutos.stream().map(tipoProdutoMapper::toResponseDTO).toList();
    }

    // Buscar tipos de produtos por status (ativo/inativo)
    @Override
    public List<TipoProdutoResponseDTO> findByAtivoInativo(Boolean ativo, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresa = empresasUsuario.get(0).getEmpresa();

        // Buscar os tipos de produtos da empresa do usuário logado pelo status (ativo/inativo)
        List<TipoProduto> tipoProdutos = tipoProdutoRepository.findByEmpresaIdAndIsAtivo(empresa.getId() ,ativo);
        if(tipoProdutos.isEmpty()) {
            throw new EntityNotFoundException("Nenhum tipo de produto encontrado com o status informado.");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        return tipoProdutos.stream().map(tipoProdutoMapper::toResponseDTO).toList();
    }

    // Buscar tipos de produtos por nome (contendo, case insensitive)
    @Override
    public List<TipoProdutoResponseDTO> findByNomeContainingIgnoreCase(String nome, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresa = empresasUsuario.get(0).getEmpresa();

        // Buscar os tipos de produtos da empresa do usuário logado pelo nome (contendo, case insensitive)
        List<TipoProduto> tipoProdutos = tipoProdutoRepository.findByEmpresaIdAndNomeContainingIgnoreCase(empresa.getId(),nome);
        if(tipoProdutos.isEmpty()) {
            throw new EntityNotFoundException("Nenhum tipo de produto encontrado com o nome informado.");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        return tipoProdutos.stream().map(tipoProdutoMapper::toResponseDTO).toList();
    }

    // Buscar tipos de produtos criados entre duas datas
    @Override
    public List<TipoProdutoResponseDTO> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fim, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresa = empresasUsuario.get(0).getEmpresa();

        // Validações das datas
        if (inicio == null || fim == null) {
            throw new BusinessException("Data de início e data de fim são obrigatórias");
        }

        if (inicio.isAfter(fim)) {
            throw new BusinessException("Data de início não pode ser posterior à data de fim");
        }

        // Buscar os tipos de produtos da empresa do usuário logado criados entre as datas informadas
        List<TipoProduto> tipoProdutos = tipoProdutoRepository.findByEmpresaIdAndCreatedAtBetween(empresa.getId() ,inicio, fim);
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
