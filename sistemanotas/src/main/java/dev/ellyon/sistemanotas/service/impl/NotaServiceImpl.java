package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.itemNota.ItemNotaRequestDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaListResponseDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaRequestDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaResponseDTO;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.EntityNotFoundException;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.*;
import dev.ellyon.sistemanotas.model.enums.StatusNota;
import dev.ellyon.sistemanotas.model.enums.TipoNota;
import dev.ellyon.sistemanotas.repository.*;
import dev.ellyon.sistemanotas.service.NotaService;
import dev.ellyon.sistemanotas.service.mapper.NotaMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotaServiceImpl implements NotaService {
    private final NotaRepository notaRepository;
    private final EmpresaRepository empresaRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final NotaMapper notaMapper;
    public NotaServiceImpl(NotaRepository notaRepository, EmpresaRepository empresaRepository, ClienteRepository clienteRepository, UsuarioRepository usuarioRepository, ProdutoRepository produtoRepository, NotaMapper notaMapper) {
        this.notaRepository = notaRepository;
        this.empresaRepository = empresaRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.notaMapper = notaMapper;
    }

    // criar nota
    @Override
    public NotaResponseDTO create(NotaRequestDTO dto) {
        Map<String, String> errors = new HashMap<>();

        // 1. Empresa é obrigatória
        if (dto.getEmpresaId() == null) {
            errors.put("empresaId", "Empresa é obrigatória");
        }
        // Verificar se a empresa está ativa
        if (empresaRepository.findByIdAndIsAtivo(dto.getEmpresaId(), true).isEmpty()) {
            errors.put("empresaId", "Empresa informada está inativa ou não existe");
        }

        // 2. Usuário é obrigatório
        if (dto.getUsuarioId() == null) {
            errors.put("usuarioId", "Usuário é obrigatório");
        }
        // Verificar se o usuário está ativo
        if (usuarioRepository.findByIdAndIsAtivo(dto.getUsuarioId(), true).isEmpty()) {
            errors.put("usuarioId", "Usuário informado está inativo ou não existe");
        }

        // 3. Validar observações (se informado)
        if (dto.getObservacoes() != null && dto.getObservacoes().length() > 500) {
            errors.put("observacoes", "Observações devem ter no máximo 500 caracteres");
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados da nota", errors);
        }

        // ========================================
        // BUSCAR EMPRESA
        // ========================================
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new EntityNotFoundException("Empresa", dto.getEmpresaId()));

        if (!empresa.getAtivo()) {
            throw new BusinessException("Não é possível criar nota para empresa inativa");
        }

        // ========================================
        // BUSCAR USUÁRIO
        // ========================================
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário", dto.getUsuarioId()));

        // ========================================
        // BUSCAR CLIENTE (OPCIONAL)
        // ========================================
        Cliente cliente = null;
        if (dto.getClienteId() != null) {
            cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Cliente", dto.getClienteId()));

            if (!cliente.getAtivo()) {
                throw new BusinessException("Não é possível criar nota para cliente inativo");
            }
        }

        // Verificar o ultimo número da nota para a empresa
        Integer ultimoNumero = notaRepository.findUltimoNumeroPorEmpresa(empresa.getId());
        int proximoNumero = (ultimoNumero != null) ? ultimoNumero + 1 : 1;
        String numeroFormatado  = String.format("%06d", proximoNumero);

        // ========================================
        // CRIAR NOTA
        // ========================================
        Nota nota = new Nota();
        nota.setEmpresa(empresa);
        nota.setCliente(cliente);
        nota.setCreatedBy(usuario);
        nota.setNumero(numeroFormatado);
        nota.setSerie("1");
        nota.setTipo(TipoNota.SAIDA);
        nota.setStatus(StatusNota.RASCUNHO);
        nota.setObservacoes(dto.getObservacoes());

        // Valores iniciais zerados
        nota.setValorProdutos(BigDecimal.ZERO);
        nota.setValorImpostosTotal(BigDecimal.ZERO);
        nota.setValorTotal(BigDecimal.ZERO);

        // Datas nulas
        nota.setDataEmissao(null);
        nota.setDataCancelamento(null);

        // ========================================
        // SALVAR E RETORNAR
        // ========================================
        Nota notaSalva = notaRepository.save(nota);
        return notaMapper.toResponseDTO(notaSalva);
    }

    // adicionar item na nota
    @Override
    public NotaResponseDTO addItem(Long notaId, ItemNotaRequestDTO itemNotaRequestDTO) {
        Map<String, String> errors = new HashMap<>();

        // 1. Verificar se a nota existe e está em rascunho
        Nota nota = notaRepository.findById(notaId)
                .orElseThrow(() -> new EntityNotFoundException("Nota", notaId));
        if (nota.getStatus() != StatusNota.RASCUNHO) {
            errors.put("nota", "Só é possível adicionar itens em notas com status RASCUNHO");
        }

        // 2. Validar se o produto existe e está ativo
        Produto produto = produtoRepository.findByIdAndIsAtivo(itemNotaRequestDTO.getProdutoId(), true)
                .orElseGet(() -> {
                    errors.put("produtoId", "Produto informado está inativo ou não existe");
                    return null;
                });

        // 3. Validar quantidade
        if (itemNotaRequestDTO.getQuantidade() == null || itemNotaRequestDTO.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("quantidade", "Quantidade deve ser maior que zero");
        }

        // 4. Validar preço unitário
        if (itemNotaRequestDTO.getPrecoUnitario() == null || itemNotaRequestDTO.getPrecoUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("precoUnitario", "Preço unitário deve ser maior que zero");
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados da nota", errors);
        }

        // 5. Criar ItemNota com snapshot do produto (usando construtor)
        ItemNota itemNota = new ItemNota(nota, produto, itemNotaRequestDTO.getQuantidade());

        // 6. Se alíquotas não informadas, usa as do produto
        if (itemNotaRequestDTO.getAliquotaIcms() != null) {
            itemNotaRequestDTO.setAliquotaIcms(produto.getAliquotaIcmsPadrao());
        }
        if (itemNotaRequestDTO.getAliquotaPis() != null) {
            itemNotaRequestDTO.setAliquotaPis(produto.getAliquotaPisPadrao());
        }
        if (itemNotaRequestDTO.getAliquotaCofins() != null) {
            itemNotaRequestDTO.setAliquotaCofins(produto.getAliquotaCofinsPadrao());
        }

        // 7. Se preço unitário for informado, sobrescreve o do produto
        if (itemNotaRequestDTO.getPrecoUnitario() != null) {
            itemNota.setPrecoUnitario(itemNotaRequestDTO.getPrecoUnitario());
        }

        // Nota: calcularValores() já foi chamado automaticamente pelos setters

        // 8. Adicionar item à nota
        nota.getItens().add(itemNota);

        // 9. Recalcular totais da nota
        nota.setValorProdutos(nota.getValorProdutos().add(itemNota.getSubtotal()));
        BigDecimal valorImpostosItem = itemNota.getValorIcms()
                .add(itemNota.getValorPis())
                .add(itemNota.getValorCofins());
        nota.setValorImpostosTotal(nota.getValorImpostosTotal().add(valorImpostosItem));
        nota.setValorTotal(nota.getValorProdutos().add(nota.getValorImpostosTotal()));

        // 10. Salvar e retornar
        Nota notaAtualizada = notaRepository.save(nota);
        return notaMapper.toResponseDTO(notaAtualizada);
    }

    // atualizar item da nota
    @Override
    public NotaResponseDTO updateItem(Long notaId, Long itemId, ItemNotaRequestDTO itemNotaRequestDTO) {
        Map<String, String> errors = new HashMap<>();

        // 1. Verificar se a nota existe e está em rascunho
        Nota nota = notaRepository.findById(notaId)
                .orElseThrow(() -> new EntityNotFoundException("Nota", notaId));
        if (nota.getStatus() != StatusNota.RASCUNHO) {
            errors.put("nota", "Só é possível atualizar itens em notas com status RASCUNHO");
        }

        // 2. Verificar se o item existe na nota
        ItemNota itemNota = nota.getItens().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("ItemNota", itemId));

        // ========================================
        // GUARDAR VALORES ANTIGOS ANTES DE ALTERAR
        // ========================================
        BigDecimal subTotalAntigo = itemNota.getSubtotal();
        BigDecimal valorImpostosAntigo = itemNota.getValorIcms()
                .add(itemNota.getValorPis())
                .add(itemNota.getValorCofins());

        // 3. Validar quantidade
        if (itemNotaRequestDTO.getQuantidade() == null || itemNotaRequestDTO.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("quantidade", "Quantidade deve ser maior que zero");
        }

        // 4. Validar preço unitário
        BigDecimal precoParaValidar = itemNotaRequestDTO.getPrecoUnitario();
        if (precoParaValidar == null) {
            // Se não informado, usa o preço do produto
            precoParaValidar = itemNota.getProduto().getPrecoVenda();
            if (precoParaValidar == null) {
                errors.put("precoUnitario", "Preço unitário não informado e produto não possui preço de venda cadastrado");
            }
        }
        if (precoParaValidar != null && precoParaValidar.compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("precoUnitario", "Preço unitário deve ser maior que zero");
        }

        // 5. Valida as alíquotas (se informadas)
        if (itemNotaRequestDTO.getAliquotaIcms() != null && itemNotaRequestDTO.getAliquotaIcms().compareTo(BigDecimal.ZERO) < 0) {
            errors.put("aliquotaIcms", "Alíquota ICMS não pode ser negativa");
        }
        if (itemNotaRequestDTO.getAliquotaPis() != null && itemNotaRequestDTO.getAliquotaPis().compareTo(BigDecimal.ZERO) < 0) {
            errors.put("aliquotaPis", "Alíquota PIS não pode ser negativa");
        }
        if (itemNotaRequestDTO.getAliquotaCofins() != null && itemNotaRequestDTO.getAliquotaCofins().compareTo(BigDecimal.ZERO) < 0) {
            errors.put("aliquotaCofins", "Alíquota COFINS não pode ser negativa");
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados da nota", errors);
        }

        // 6. Atualizar campos do item
        itemNota.setQuantidade(itemNotaRequestDTO.getQuantidade());

        if (itemNotaRequestDTO.getPrecoUnitario() != null) {
            itemNota.setPrecoUnitario(itemNotaRequestDTO.getPrecoUnitario());
        } else {
            // Usa o preço do produto se não foi informado
            itemNota.setPrecoUnitario(itemNota.getProduto().getPrecoVenda());
        }

        if (itemNotaRequestDTO.getAliquotaIcms() != null) {
            itemNota.setAliquotaIcms(itemNotaRequestDTO.getAliquotaIcms());
        }
        if (itemNotaRequestDTO.getAliquotaPis() != null) {
            itemNota.setAliquotaPis(itemNotaRequestDTO.getAliquotaPis());
        }
        if (itemNotaRequestDTO.getAliquotaCofins() != null) {
            itemNota.setAliquotaCofins(itemNotaRequestDTO.getAliquotaCofins());
        }

        // 7. Recalcular totais da nota
        // Subtrai valores antigos
        nota.setValorProdutos(nota.getValorProdutos().subtract(subTotalAntigo));
        nota.setValorImpostosTotal(nota.getValorImpostosTotal().subtract(valorImpostosAntigo));

        // Adiciona valores novos
        BigDecimal subTotalNovo = itemNota.getSubtotal();
        BigDecimal valorImpostosNovo = itemNota.getValorIcms()
                .add(itemNota.getValorPis())
                .add(itemNota.getValorCofins());

        nota.setValorProdutos(nota.getValorProdutos().add(subTotalNovo));
        nota.setValorImpostosTotal(nota.getValorImpostosTotal().add(valorImpostosNovo));
        nota.setValorTotal(nota.getValorProdutos().add(nota.getValorImpostosTotal()));

        // 8. Salvar e retornar
        Nota notaAtualizada = notaRepository.save(nota);
        return notaMapper.toResponseDTO(notaAtualizada);
    }

    // remover item da nota
    @Override
    public NotaResponseDTO removeItem(Long notaId, Long itemId) {
        Map<String, String> errors = new HashMap<>();

        // 1. Verificar se a nota existe e está em rascunho
        Nota nota = notaRepository.findById(notaId)
                .orElseThrow(() -> new EntityNotFoundException("Nota", notaId));
        if (nota.getStatus() != StatusNota.RASCUNHO) {
            errors.put("nota", "Só é possível remover itens em notas com status RASCUNHO");
        }

        // 2. Verificar se o item existe na nota
        ItemNota itemNota = nota.getItens().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("ItemNota", itemId));

        // 3. Remover item da nota
        nota.getItens().remove(itemNota);

        // 4. Recalcular totais da nota
        nota.setValorProdutos(nota.getValorProdutos().subtract(itemNota.getSubtotal()));
        BigDecimal valorImpostosItem = itemNota.getValorIcms()
                .add(itemNota.getValorPis())
                .add(itemNota.getValorCofins());
        nota.setValorImpostosTotal(nota.getValorImpostosTotal().subtract(valorImpostosItem));
        nota.setValorTotal(nota.getValorProdutos().add(nota.getValorImpostosTotal()));

        // 5. Salvar e retornar
        Nota notaAtualizada = notaRepository.save(nota);
        return notaMapper.toResponseDTO(notaAtualizada);

    }

    // emitir nota
    @Override
    public NotaResponseDTO emitirNota(Long notaId) {
        Map<String, String> errors = new HashMap<>();
        // 1. Verificar se a nota existe e está em rascunho
        Nota nota = notaRepository.findById(notaId)
                .orElseThrow(() -> new EntityNotFoundException("Nota", notaId));
        if (nota.getStatus() != StatusNota.RASCUNHO) {
            errors.put("nota", "Só é possível emitir notas com status RASCUNHO");
        }

        // 2. Verificar se a nota possui pelo menos um item
        if (nota.getItens().isEmpty()) {
            errors.put("itens", "Não é possível emitir nota sem itens");
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados da nota", errors);
        }

        // 3. Atualizar status e data de emissão
        nota.setStatus(StatusNota.EMITIDA);
        LocalDateTime now = LocalDateTime.now();
        nota.setDataEmissao(now);

        // 4. Salvar e retornar
        Nota notaAtualizada = notaRepository.save(nota);
        return notaMapper.toResponseDTO(notaAtualizada);

    }

    // atualizar dados da nota
    @Override
    public NotaResponseDTO updateNota(Long notaId, NotaRequestDTO dto) {
        Map<String, String> errors = new HashMap<>();

        // 1. Verificar se a nota existe e está em rascunho
        Nota nota = notaRepository.findById(notaId)
                .orElseThrow(() -> new EntityNotFoundException("Nota", notaId));
        if (nota.getStatus() != StatusNota.RASCUNHO) {
            errors.put("nota", "Só é possível atualizar notas com status RASCUNHO");
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados da nota", errors);
        }

        // 2. Atualizar campos permitidos
        // Validar observações
        if (dto.getObservacoes() != null && dto.getObservacoes().length() > 500) {
            errors.put("observacoes", "Observações devem ter no máximo 500 caracteres");
        }

        // Atualizar empresa (se informado)
        Empresa novaEmpresa = null;
        if (dto.getEmpresaId() != null) {
            novaEmpresa = empresaRepository.findByIdAndIsAtivo(dto.getEmpresaId(), true)
                    .orElseGet(() -> {
                        errors.put("empresaId", "Empresa não encontrada ou inativa");
                        return null;
                    });
        }

        // Validar usuário (se informado)
        Usuario novoUsuario = null;
        if (dto.getUsuarioId() != null) {
            novoUsuario = usuarioRepository.findByIdAndIsAtivo(dto.getUsuarioId(), true)
                    .orElseGet(() -> {
                        errors.put("usuarioId", "Usuário não encontrado ou inativo");
                        return null;
                    });
        }

        // Validar cliente (se informado)
        Cliente novoCliente = null;
        if (dto.getClienteId() != null) {
            novoCliente = clienteRepository.findByIdAndIsAtivo(dto.getClienteId(), true)
                    .orElseGet(() -> {
                        errors.put("clienteId", "Cliente não encontrado ou inativo");
                        return null;
                    });
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados da nota", errors);
        }

        // 3. Atualizar campos
        nota.setObservacoes(dto.getObservacoes());

        // Atualizar empresa (se informada e diferente da atual)
        if (dto.getEmpresaId() != null) {
            if (nota.getEmpresa() == null || !dto.getEmpresaId().equals(nota.getEmpresa().getId())) {
                nota.setEmpresa(novaEmpresa);
            }
        }

        // Atualizar usuário (se informado e diferente do atual)
        if (dto.getUsuarioId() != null) {
            if (nota.getCreatedBy() == null || !dto.getUsuarioId().equals(nota.getCreatedBy().getId())) {
                nota.setCreatedBy(novoUsuario);
            }
        }

        // Atualizar cliente (permite null para remover)
        if (dto.getClienteId() != null) {
            // Se informado, atualiza apenas se for diferente
            if (nota.getCliente() == null || !dto.getClienteId().equals(nota.getCliente().getId())) {
                nota.setCliente(novoCliente);
            }
        } else {
            // Se não informado (null), remove o cliente da nota
            nota.setCliente(null);
        }

        // Atualizar frete (se informado)
        BigDecimal freteAtual = nota.getFrete() != null ? nota.getFrete() : BigDecimal.ZERO;
        BigDecimal novoFrete = dto.getFrete() != null ? dto.getFrete() : BigDecimal.ZERO;

        // Atualizar frete
        nota.setFrete(novoFrete);

        // Recalcular valor total: valorProdutos + valorImpostos + frete
        BigDecimal valorProdutos = nota.getValorProdutos() != null ? nota.getValorProdutos() : BigDecimal.ZERO;
        BigDecimal valorImpostos = nota.getValorImpostosTotal() != null ? nota.getValorImpostosTotal() : BigDecimal.ZERO;

        nota.setValorTotal(valorProdutos.add(valorImpostos).add(novoFrete));

        // Retirar data de cancelamento e emissao se houver
        nota.setDataCancelamento(null);
        nota.setDataEmissao(null);

        // 4. Salvar e retornar
        Nota notaAtualizada = notaRepository.save(nota);
        return notaMapper.toResponseDTO(notaAtualizada);

    }

    // cancelar nota
    @Override
    public void cancelarNota(Long notaId) {
        Map<String, String> errors = new HashMap<>();

        // 1. Verificar se a nota existe e está emitida
        Nota nota = notaRepository.findById(notaId)
                .orElseThrow(() -> new EntityNotFoundException("Nota", notaId));
        if (nota.getStatus() == StatusNota.CANCELADA) {
            errors.put("nota", "Só é possível cancelar notas com status EMITIDA ou RASCUNHO");
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados da nota", errors);
        }

        // 2. Atualizar status e data de cancelamento
        nota.setStatus(StatusNota.CANCELADA);
        LocalDateTime now = LocalDateTime.now();
        nota.setDataCancelamento(now);

        // 3. Salvar
        notaRepository.save(nota);


    }

    // buscar nota por ID
    @Override
    public NotaResponseDTO findById(Long notaId) {
        Nota nota = notaRepository.findById(notaId)
                .orElseThrow(() -> new EntityNotFoundException("Nota", notaId));
        return notaMapper.toResponseDTO(nota);
    }

    // buscar todas as notas
    @Override
    public List<NotaListResponseDTO> findAll() {
        List<Nota> notas = notaRepository.findAll();


        return notas.stream().map(notaMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // buscar todas as notas com paginação
    @Override
    public Page<NotaListResponseDTO> findAllPaged(Pageable pageable) {
        Page<Nota> notasPage = notaRepository.findAll(pageable);
        if (notasPage.isEmpty()){
            throw new EntityNotFoundException("Nenhuma nota encontrada.");
        }

        return notasPage.map(notaMapper::toListResponseDTO);
    }

    // buscar nota por número e empresa
    @Override
    public NotaResponseDTO findByNumeroAndEmpresaId(Long empresaId, String numero) {
        // Validações
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("Número da nota é obrigatório");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("ID da empresa é obrigatório");
        }

        Nota nota = notaRepository.findByNumeroAndEmpresaId(numero, empresaId);
        if (nota == null) {
            throw new EntityNotFoundException(
                    "Nota com número " + numero + " não encontrada para a empresa " + empresaId
            );
        }
        return notaMapper.toResponseDTO(nota);
    }

    // buscar notas por tipo
    @Override
    public List<NotaListResponseDTO> findByTipo(String tipo) {
        // Validar tipo
        TipoNota tipoNota;
        try {
            tipoNota = TipoNota.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Tipo de nota inválido. Valores permitidos: ENTRADA, SAIDA, NFE, NFCE, NFSE");
        }
        List<Nota> notas = notaRepository.findAll().stream()
                .filter(nota -> nota.getTipo() == tipoNota)
                .collect(Collectors.toList());
        if (notas.isEmpty()){
            throw new EntityNotFoundException("Nenhuma nota encontrada para o tipo: " + tipo);
        }

        return notas.stream().map(notaMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // buscar notas por status
    @Override
    public List<NotaListResponseDTO> findByStatus(String status) {
        // Validar status
        StatusNota statusNota;
        try {
            statusNota = StatusNota.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Status de nota inválido. Valores permitidos: RASCUNHO, EMITIDA, CANCELADA");
        }
        List<Nota> notas = notaRepository.findAll().stream()
                .filter(nota -> nota.getStatus() == statusNota)
                .collect(Collectors.toList());
        if (notas.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma nota encontrada para o status: " + status);
        }
        return notas.stream().map(notaMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // buscar notas por empresa
    @Override
    public List<NotaListResponseDTO> findByEmpresaId(Long empresaId) {
        List<Nota> notas = notaRepository.findAll().stream()
                .filter(nota -> nota.getEmpresa().getId().equals(empresaId))
                .collect(Collectors.toList());
        if (notas.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma nota encontrada para a empresa ID: " + empresaId);
        }
        return notas.stream().map(notaMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // buscar notas por cliente
    @Override
    public List<NotaListResponseDTO> findByClienteId(Long clienteId) {
        List<Nota> notas = notaRepository.findAll().stream()
                .filter(nota -> nota.getCliente() != null && nota.getCliente().getId().equals(clienteId))
                .collect(Collectors.toList());
        if (notas.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma nota encontrada para o cliente ID: " + clienteId);
        }
        return notas.stream().map(notaMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // buscar notas por usuário que criou
    @Override
    public List<NotaListResponseDTO> findByCreatedByUserId(Long userId) {
        List<Nota> notas = notaRepository.findAll().stream()
                .filter(nota -> nota.getCreatedBy() != null && nota.getCreatedBy().getId().equals(userId))
                .collect(Collectors.toList());
        if (notas.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma nota encontrada para o usuário ID: " + userId);
        }
        return notas.stream().map(notaMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // buscar notas por intervalo de datas de emissão
    @Override
    public List<NotaListResponseDTO> findByDataEmissaoBetween(LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new BusinessException("Data de início e data de fim são obrigatórias");
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new BusinessException("Data de início não pode ser posterior à data de fim");
        }

        List<Nota> notas = notaRepository.findByDataEmissaoBetweenOrderByDataEmissaoDesc(dataInicio, dataFim);

        // Formatação das datas para exibição na mensagem de erro
        if (notas.isEmpty()) {
            // Formata a data para o padrão brasileiro
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataInicioFormatada = dataInicio.format(formatter);
            String dataFimFormatada = dataFim.format(formatter);

            throw new EntityNotFoundException(
                    String.format("Nenhuma nota encontrada entre %s e %s",
                            dataInicioFormatada, dataFimFormatada)
            );
        }

        return notas.stream().map(notaMapper::toListResponseDTO).collect(Collectors.toList());

    }

    // buscar notas por intervalo de datas de cancelamento
    @Override
    public List<NotaListResponseDTO> findByDataCancelamentoBetween(LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new BusinessException("Data de início e data de fim são obrigatórias");
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new BusinessException("Data de início não pode ser posterior à data de fim");
        }

        List<Nota> notas = notaRepository.findByDataCancelamentoBetweenOrderByDataCancelamentoDesc(dataInicio, dataFim);

        // Formatação das datas para exibição na mensagem de erro
        if (notas.isEmpty()) {
            // Formata a data para o padrão brasileiro
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataInicioFormatada = dataInicio.format(formatter);
            String dataFimFormatada = dataFim.format(formatter);

            throw new EntityNotFoundException(
                    String.format("Nenhuma nota encontrada entre %s e %s",
                            dataInicioFormatada, dataFimFormatada)
            );
        }

        return notas.stream().map(notaMapper::toListResponseDTO).collect(Collectors.toList());

    }
}
