package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.itemNota.ItemNotaRequestDTO;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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
}
