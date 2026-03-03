package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.itemNota.ItemNotaRequestDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaListResponseDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaRequestDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface NotaService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete
    NotaResponseDTO create(NotaRequestDTO dto, Authentication authentication);// Criar nova nota
    NotaResponseDTO addItem(Long notaId, ItemNotaRequestDTO itemNotaRequestDTO, Authentication authentication); // Adicionar item a nota
    NotaResponseDTO updateItem(Long notaId, Long itemId, ItemNotaRequestDTO itemNotaRequestDTO, Authentication authentication); // Atualizar item da nota
    NotaResponseDTO removeItem(Long notaId, Long itemId, Authentication authentication); // Remover item da nota
    NotaResponseDTO emitirNota(Long notaId, Authentication authentication); // Emitir nota
    NotaResponseDTO updateNota(Long notaId, NotaRequestDTO dto, Authentication authentication); // Atualizar dados da nota
    void cancelarNota(Long notaId, Authentication authentication); // Cancelar nota

    // Buscas
    NotaResponseDTO findById(Long notaId); // Buscar nota por ID
    List<NotaListResponseDTO> findAll(); // Buscar todas as notas
    Page<NotaListResponseDTO> findAllPaged(Pageable pageable); // Buscar todas as notas com paginação
    NotaResponseDTO findByNumeroAndEmpresaId(Long empresaId, String numero); // Buscar nota por número e empresa
    List<NotaListResponseDTO> findByTipo(String tipo); // Buscar notas por tipo
    List<NotaListResponseDTO> findByStatus(String status); // Buscar notas por status
    List<NotaListResponseDTO> findByEmpresaId(Long empresaId); // Buscar notas por empresa
    List<NotaListResponseDTO> findByClienteId(Long clienteId); // Buscar notas por cliente
    List<NotaListResponseDTO> findByCreatedByUserId(Long userId); // Buscar notas por usuário que criou
    List<NotaListResponseDTO> findByDataEmissaoBetween(LocalDateTime dataInicio, LocalDateTime dataFim); // Buscar notas por intervalo de datas de emissão
    List<NotaListResponseDTO> findByDataCancelamentoBetween(LocalDateTime dataInicio, LocalDateTime dataFim); // Buscar notas por intervalo de datas de cancelamento
    List<NotaListResponseDTO> findByValorTotalBetween(BigDecimal valorMinimo, BigDecimal valorMaximo); // Buscar notas por intervalo de valor total
    List<NotaListResponseDTO> findByValorImpostosTotalBetween(BigDecimal valorMinimo, BigDecimal valorMaximo); // Buscar notas por intervalo de valor total de impostos
}
