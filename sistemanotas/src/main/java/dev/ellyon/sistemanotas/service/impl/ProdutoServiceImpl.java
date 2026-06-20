package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.produto.ProdutoListResponseDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoResponseDTO;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.EntityNotFoundException;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.*;
import dev.ellyon.sistemanotas.repository.*;
import dev.ellyon.sistemanotas.service.NcmService;
import dev.ellyon.sistemanotas.service.ProdutoService;
import dev.ellyon.sistemanotas.service.mapper.ProdutoMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProdutoServiceImpl implements ProdutoService {
    private final NcmService ncmService;
    private final ProdutoRepository produtoRepository;
    private final TipoProdutoRepository tipoProdutoRepository;
    private final ItemNotaRepository itemNotaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaUsuarioRepository empresaUsuarioRepository;
    private final ProdutoMapper produtoMapper;
    public ProdutoServiceImpl(NcmService ncmService, ProdutoRepository produtoRepository, TipoProdutoRepository tipoProdutoRepository, ItemNotaRepository itemNotaRepository, UsuarioRepository usuarioRepository, EmpresaUsuarioRepository empresaUsuarioRepository, ProdutoMapper produtoMapper) {
        this.ncmService = ncmService;
        this.produtoRepository = produtoRepository;
        this.tipoProdutoRepository = tipoProdutoRepository;
        this.itemNotaRepository = itemNotaRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaUsuarioRepository = empresaUsuarioRepository;
        this.produtoMapper = produtoMapper;
    }

    // Criar um novo produto
    @Override
    public ProdutoResponseDTO create(ProdutoRequestDTO dto, Authentication authentication) {
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

        String codigoProdutoUpperCase = dto.getCodigoProduto().toUpperCase();

        // Validacoes
        // Código obrigatório, único, max 50
        if (codigoProdutoUpperCase == null || codigoProdutoUpperCase.trim().isEmpty()) {
            errors.put("codigoProduto", "Código do produto é obrigatório");
        }
        if (produtoRepository.existsByCodigoProduto(codigoProdutoUpperCase)) {
            throw new BusinessException("Já existe produto com o código informado: " + codigoProdutoUpperCase);
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

        // Validar se o produto tem preço de venda
        if (dto.getPrecoVenda() == null || dto.getPrecoVenda().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Produto não possui preço de venda cadastrado");
        }

        // Validar NCM se informado
        if (dto.getNcm() != null && !dto.getNcm().isBlank()) {
            if (!ncmService.validarNCM(dto.getNcm())) {
                errors.put("ncm", "NCM inválido ou não encontrado na base da Receita Federal");
            }
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        // Busca o tipo de produto (lança EntityNotFoundException se não existir)
        TipoProduto tipoProduto = tipoProdutoRepository.findById(dto.getTipoProduto())
                .orElseThrow(() -> new EntityNotFoundException("TipoProduto", dto.getTipoProduto()));

        // BUSCAR ALÍQUOTAS AUTOMATICAMENTE SE NCM FOR INFORMADO
        BigDecimal aliquotaIcms = dto.getAliquotaIcmsPadrao();
        BigDecimal aliquotaPis = dto.getAliquotaPisPadrao();
        BigDecimal aliquotaCofins = dto.getAliquotaCofinsPadrao();

        if (dto.getNcm() != null && !dto.getNcm().isBlank()) {
            try {
                Map<String, BigDecimal> aliquotasSugeridas = ncmService.buscarAliquotasSugeridas(dto.getNcm());

                // Se não foi informado alíquota, usar a REAL da API
                if (aliquotaIcms == null || aliquotaIcms.compareTo(BigDecimal.ZERO) == 0) {
                    aliquotaIcms = aliquotasSugeridas.get("icms");
                }
                if (aliquotaPis == null || aliquotaPis.compareTo(BigDecimal.ZERO) == 0) {
                    aliquotaPis = aliquotasSugeridas.get("pis");
                }
                if (aliquotaCofins == null || aliquotaCofins.compareTo(BigDecimal.ZERO) == 0) {
                    aliquotaCofins = aliquotasSugeridas.get("cofins");
                }
            } catch (BusinessException e) {
                // Se der erro ao buscar alíquotas, lançar exceção
                throw new BusinessException("Erro ao buscar alíquotas do NCM: " + e.getMessage());
            }
        }

        // Criar produto
        Produto produto = new Produto();
        produto.setCodigoProduto(codigoProdutoUpperCase);
        produto.setNome(dto.getNome());
        produto.setDescricaoProduto(dto.getDescricao());
        produto.setTipoProduto(tipoProduto);
        produto.setUnidade(dto.getUnidade());
        produto.setPrecoVenda(dto.getPrecoVenda());
        produto.setNcm(dto.getNcm() != null ? dto.getNcm().replaceAll("[^0-9]", "") : null);
        produto.setCfopPadrao(dto.getCfopPadrao());
        produto.setAliquotaIcmsPadrao(aliquotaIcms != null ? aliquotaIcms : BigDecimal.ZERO);
        produto.setAliquotaPisPadrao(aliquotaPis != null ? aliquotaPis : BigDecimal.ZERO);
        produto.setAliquotaCofinsPadrao(aliquotaCofins != null ? aliquotaCofins : BigDecimal.ZERO);
        produto.setEmpresa(empresa);
        produto.setIsAtivo(true);

        // Salva e retorna DTO de resposta
        Produto produtoSalvo = produtoRepository.save(produto);
        return produtoMapper.toResponseDTO(produtoSalvo);
    }

    // Deletar um produto
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

        // Buscar o produto (lança EntityNotFoundException se não existir)
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto", id));

        // Verificar se o produto pertence à empresa do usuário logado
        if (!produto.getEmpresa().getId().equals(empresa.getId())) {
            throw new BusinessException("Produto não pertence à empresa do usuário logado");
        }

        // Verificar sem o produto tem vinculo com algum item de alguma nota
        List<ItemNota> itemNota = itemNotaRepository.findByProdutoId(id);
        if (!itemNota.isEmpty()){
            errors.put("itemNota", "Esse produto tem vinculo com alguma nota. É recomendavel a desativação do produto!");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        produtoRepository.delete(produto);
    }

    // Atualizar um produto
    @Override
    public ProdutoResponseDTO update(Long id, ProdutoRequestDTO dto, Authentication authentication) {
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

        String codigoProdutoUpperCase = dto.getCodigoProduto().toUpperCase();

        // Validacoes
        // Código obrigatório, único, max 50
        if (codigoProdutoUpperCase == null || codigoProdutoUpperCase.trim().isEmpty()) {
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

        // Busca o tipo de produto (lança EntityNotFoundException se não existir)
        TipoProduto tipoProduto = tipoProdutoRepository.findById(dto.getTipoProduto())
                .orElseThrow(() -> new EntityNotFoundException("TipoProduto", dto.getTipoProduto()));

        // Buscar o produto (lança EntityNotFoundException se não existir)
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto", id));

        if (!produto.getEmpresa().getId().equals(empresa.getId())) {
            throw new BusinessException("Produto não pertence à empresa do usuário logado!");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        produto.setCodigoProduto(codigoProdutoUpperCase);
        produto.setNome(dto.getNome());
        produto.setDescricaoProduto(dto.getDescricao());
        produto.setTipoProduto(tipoProduto);
        produto.setUnidade(dto.getUnidade());
        produto.setPrecoVenda(dto.getPrecoVenda());
        produto.setNcm(dto.getNcm() != null ? dto.getNcm().replaceAll("[^0-9]", "") : null);
        produto.setCfopPadrao(dto.getCfopPadrao());
        produto.setAliquotaIcmsPadrao(dto.getAliquotaIcmsPadrao());
        produto.setAliquotaPisPadrao(dto.getAliquotaPisPadrao());
        produto.setAliquotaCofinsPadrao(dto.getAliquotaCofinsPadrao());
        produto.setIsAtivo(true);

        // Salva e retorna DTO de resposta
        Produto produtoAtualizado = produtoRepository.save(produto);
        return produtoMapper.toResponseDTO(produtoAtualizado);
    }

    // Desativar um produto (soft delete)
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

        // Buscar o produto (lança EntityNotFoundException se não existir)
        Produto produto = produtoRepository.findByEmpresaIdAndId(empresa.getId(),id)
                .orElseThrow(() -> new EntityNotFoundException("Produto", id));

        // Verificar se o produto pertence à empresa do usuário logado
        if (!produto.getEmpresa().getId().equals(empresa.getId())) {
            throw new BusinessException("Produto não pertence à empresa do usuário logado!");
        }

        if (produto.getId() == null) {
            throw new BusinessException("Produto não existe!");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        produto.setIsAtivo(false);
        produtoRepository.save(produto);

    }

    // Ativar um produto
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

        // Buscar o produto (lança EntityNotFoundException se não existir)
        Produto produto = produtoRepository.findByEmpresaIdAndId(empresa.getId(),id)
                .orElseThrow(() -> new EntityNotFoundException("Produto", id));

        // Verificar se o produto pertence à empresa do usuário logado
        if (!produto.getEmpresa().getId().equals(empresa.getId())) {
            throw new BusinessException("Produto não pertence à empresa do usuário logado!");
        }

        if (produto.getId() == null) {
            throw new BusinessException("Produto não existe!");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        produto.setIsAtivo(true);
        produtoRepository.save(produto);
    }

    // Buscar produto por ID
    @Override
    public ProdutoResponseDTO findById(Long id, Authentication authentication) {
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

        // Buscar o produto pela empresa (lança EntityNotFoundException se não existir)
        Produto produto = produtoRepository.findByEmpresaIdAndId(empresa.getId(), id)
                .orElseThrow(() -> new EntityNotFoundException("Produto", id));

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        return produtoMapper.toResponseDTO(produto);
    }

    // Buscar todos os produtos
    @Override
    public List<ProdutoListResponseDTO> findAll(Authentication authentication) {
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

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        List<Produto> produtos = produtoRepository.findAllByEmpresaId(empresa.getId());
        return produtos.stream()
                .map(produtoMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar produtos por tipo de produto ID
    @Override
    public List<ProdutoListResponseDTO> findByTipoProdutoId(Long tipoProdutoId, Authentication authentication) {
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

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        // Buscar produtos por tipo de produto ID
        List<Produto> produtos = produtoRepository.findByEmpresaIdAndTipoProdutoId(empresa.getId(), tipoProdutoId);
        if (produtos.isEmpty()) {
            throw new EntityNotFoundException("Nenhum produto encontrado para o Tipo Produto ID: " + tipoProdutoId);
        }

        return produtos.stream()
                .map(produtoMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar produtos por tipo de produto nome
    @Override
    public List<ProdutoListResponseDTO> findByTipoProdutoNome(String tipoProdutoNome, Authentication authentication) {
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

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        // Buscar produtos por tipo de produto nome
        List<Produto> produtos = produtoRepository.findByEmpresaIdAndTipoProdutoNome(empresa.getId(),tipoProdutoNome);
        if (produtos.isEmpty()) {
            throw new EntityNotFoundException("Nenhum produto encontrado para o Tipo Produto nome: " + tipoProdutoNome);
        }

        return produtos.stream()
                .map(produtoMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar produtos por status (ativo/inativo)
    @Override
    public List<ProdutoListResponseDTO> findByIsAtivo(Boolean ativo, Authentication authentication) {
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

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        // Buscar produtos por status (ativo/inativo)
        List<Produto> produtos = produtoRepository.findByEmpresaIdAndIsAtivo(empresa.getId(),ativo);
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
    public List<ProdutoListResponseDTO> findByNomeContainingIgnoreCase(String nome, Authentication authentication) {
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

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        // Buscar produtos por nome (contendo, case insensitive)
        List<Produto> produtos = produtoRepository.findByEmpresaIdAndNomeContainingIgnoreCase(empresa.getId(),nome);
        if (produtos.isEmpty()) {
            throw new EntityNotFoundException("Nenhum produto encontrado contendo o nome: " + nome);
        }
        return produtos.stream()
                .map(produtoMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar produtos por código (contendo, case insensitive)
    @Override
    public List<ProdutoListResponseDTO> findByCodigoProdutoContainingIgnoreCase(String codigoProduto, Authentication authentication) {
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

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        // Buscar produtos por código (contendo, case insensitive)
        List<Produto> produtos = produtoRepository.findByEmpresaIdAndCodigoProdutoContainingIgnoreCase(empresa.getId(),codigoProduto);
        if (produtos.isEmpty()) {
            throw new EntityNotFoundException("Nenhum produto encontrado contendo o código: " + codigoProduto);
        }

        return produtos.stream()
                .map(produtoMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar produtos por faixa de preço de venda
    @Override
    public List<ProdutoListResponseDTO> findByPrecoVendaBetween(BigDecimal precoMinimo, BigDecimal precoMaximo, Authentication authentication) {
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

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        // Buscar produtos por faixa de preço de venda
        List<Produto> produtos = produtoRepository.findByEmpresaIdAndPrecoVendaBetween(empresa.getId(),precoMinimo, precoMaximo);
        if (produtos.isEmpty()) {
            throw new EntityNotFoundException("Nenhum produto encontrado na faixa de preço: " + "R$ " + precoMinimo + " ~ " + "R$ " + precoMaximo);
        }

        return produtos.stream()
                .map(produtoMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar produtos por faixa de data de criação
    @Override
    public List<ProdutoListResponseDTO> findByCreatedAtBetween(java.time.LocalDateTime dataInicio, java.time.LocalDateTime dataFim, Authentication authentication) {
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

        // Validação das datas
        if (dataInicio == null || dataFim == null) {
            throw new BusinessException("Data de início e data de fim são obrigatórias");
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new BusinessException("Data de início não pode ser posterior à data de fim");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        // Buscar produtos por faixa de data de criação
        List<Produto> produtos = produtoRepository.findByEmpresaIdAndCreatedAtBetween(empresa.getId(),dataInicio, dataFim);

        // Formatação das datas para exibição na mensagem de erro
        if (produtos.isEmpty()) {
            // Formata a data para o padrão brasileiro
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataInicioFormatada = dataInicio.format(formatter);
            String dataFimFormatada = dataFim.format(formatter);

            throw new EntityNotFoundException(
                    String.format("Nenhum produto encontrado entre %s e %s",
                            dataInicioFormatada, dataFimFormatada)
            );
        }
        return produtos.stream()
                .map(produtoMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar todos os produtos com paginação
    @Override
    public Page<ProdutoListResponseDTO> findAllPaged(Pageable pageable, Authentication authentication) {
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

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        // Buscar todos os produtos com paginação
        Page<Produto> produtosPage = produtoRepository.findAllByEmpresaId(empresa.getId(), pageable);

        if (produtosPage.isEmpty()) {
            throw new EntityNotFoundException("Nenhum produto encontrado");
        }

        // Converte Page<Produto> para Page<ProdutoListResponseDTO>
        return produtosPage.map(produtoMapper::toListResponseDTO);
    }
}
