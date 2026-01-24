package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.nota.NotaRequestDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaResponseDTO;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.EntityNotFoundException;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.Cliente;
import dev.ellyon.sistemanotas.model.Empresa;
import dev.ellyon.sistemanotas.model.Nota;
import dev.ellyon.sistemanotas.model.Usuario;
import dev.ellyon.sistemanotas.model.enums.StatusNota;
import dev.ellyon.sistemanotas.model.enums.TipoNota;
import dev.ellyon.sistemanotas.repository.ClienteRepository;
import dev.ellyon.sistemanotas.repository.EmpresaRepository;
import dev.ellyon.sistemanotas.repository.NotaRepository;
import dev.ellyon.sistemanotas.repository.UsuarioRepository;
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
    private final NotaMapper notaMapper;
    public NotaServiceImpl(NotaRepository notaRepository, EmpresaRepository empresaRepository, ClienteRepository clienteRepository, UsuarioRepository usuarioRepository, NotaMapper notaMapper) {
        this.notaRepository = notaRepository;
        this.empresaRepository = empresaRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
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

        // 2. Usuário é obrigatório
        if (dto.getUsuarioId() == null) {
            errors.put("usuarioId", "Usuário é obrigatório");
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
}
