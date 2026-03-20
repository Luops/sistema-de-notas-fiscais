package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.dto.itemNota.ItemNotaRequestDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaListResponseDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaRequestDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaResponseDTO;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.service.NotaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notas")
public class NotaController {
    private final NotaService notaService;
    public NotaController(NotaService notaService) {
        this.notaService = notaService;
    }

    // Rota para criar uma nova rascunho
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> create(@RequestBody @Valid NotaRequestDTO dto,
                                                     Authentication authentication) {
        NotaResponseDTO notaResponseDTO = notaService.create(dto, authentication);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Nota criada com sucesso",
                notaResponseDTO
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Rota para adicionar item a nota
    @PostMapping("/{notaId}/add-item")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> addItem(
            @PathVariable Long notaId,
            @RequestBody @Valid ItemNotaRequestDTO itemNotaRequestDTO,
            Authentication authentication) {

        try {
            NotaResponseDTO nota = notaService.addItem(notaId, itemNotaRequestDTO, authentication);

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    "Item adicionado à nota com sucesso",
                    nota
            );
            return ResponseEntity.ok(response);

        } catch (ValidationException e) {
            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    e.getErrors()
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Rota para atualizar item da nota
    @PutMapping("/{notaId}/update-item/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> updateItem(
            @PathVariable Long notaId,
            @PathVariable Long itemId,
            @RequestBody @Valid ItemNotaRequestDTO itemNotaRequestDTO, Authentication authentication) {
        NotaResponseDTO notaResponseDTO = notaService.updateItem(notaId, itemId, itemNotaRequestDTO, authentication);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Item atualizado com sucesso",
                notaResponseDTO
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para remover item da nota
    @DeleteMapping("/{notaId}/remove-item/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> removeItem(
            @PathVariable Long notaId,
            @PathVariable Long itemId, Authentication authentication) {
        NotaResponseDTO notaResponseDTO = notaService.removeItem(notaId, itemId, authentication);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Item removido com sucesso",
                notaResponseDTO
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para emitir nota
    @PostMapping("/{notaId}/emitir")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> emitirNota(@PathVariable Long notaId, Authentication authentication) {
        NotaResponseDTO notaResponseDTO = notaService.emitirNota(notaId, authentication);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Nota emitida com sucesso",
                notaResponseDTO
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para atualizar dados da nota
    @PutMapping("/update/{notaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SuccessResponseDTO> updateNota(
            @PathVariable Long notaId,
            @RequestBody @Valid NotaRequestDTO dto, Authentication authentication) {
        NotaResponseDTO notaResponseDTO = notaService.updateNota(notaId, dto, authentication);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Nota atualizada com sucesso",
                notaResponseDTO
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para cancelar nota
    @PutMapping("/cancel/{notaId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SuccessResponseDTO> cancelarNota(@PathVariable Long notaId, Authentication authentication) {
        notaService.cancelarNota(notaId, authentication);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Nota cancelada com sucesso",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para buscar nota por ID
    @GetMapping("/findById/{notaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SuccessResponseDTO> findById(@PathVariable Long notaId) {
        NotaResponseDTO notaResponseDTO = notaService.findById(notaId);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Nota encontrada com sucesso",
                notaResponseDTO
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para buscar todas as notas
    @GetMapping("/findAll")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SuccessResponseDTO> findAll() {
        List<NotaListResponseDTO> notas = notaService.findAll();
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Notas encontradas com sucesso",
                notas
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para buscar todas as notas com paginação
    @GetMapping("/paginated")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<Page<NotaListResponseDTO>> findAllPaged(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<NotaListResponseDTO> notas = notaService.findAllPaged(pageable);
        return ResponseEntity.ok(notas);
    }

    // Rota para buscar nota por número e empresa
    @GetMapping("/find-by-numero-and-empresa")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByNumeroAndEmpresaId(
            @RequestParam(required = true) Long empresaId,
            @RequestParam(required = true) String numero) {
        NotaResponseDTO notaResponseDTO = notaService.findByNumeroAndEmpresaId(empresaId, numero);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Nota encontrada com sucesso",
                notaResponseDTO
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para buscar notas por tipo
    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByTipo(@PathVariable String tipo) {
        List<NotaListResponseDTO> notas = notaService.findByTipo(tipo);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Notas encontradas com sucesso",
                notas
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para buscar notas por status
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByStatus(@PathVariable String status) {
        List<NotaListResponseDTO> notas = notaService.findByStatus(status);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Notas encontradas com sucesso",
                notas
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para buscar notas por empresa
    @GetMapping("/empresa/{empresaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByEmpresaId(@PathVariable Long empresaId) {
        List<NotaListResponseDTO> notas = notaService.findByEmpresaId(empresaId);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Notas encontradas com sucesso",
                notas
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para buscar notas por cliente
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByClienteId(@PathVariable Long clienteId) {
        List<NotaListResponseDTO> notas = notaService.findByClienteId(clienteId);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Notas encontradas com sucesso",
                notas
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para buscar notas por usuário que criou
    @GetMapping("/created-by/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByCreatedByUserId(@PathVariable Long userId) {
        List<NotaListResponseDTO> notas = notaService.findByCreatedByUserId(userId);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Notas encontradas com sucesso",
                notas
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para buscar notas por intervalo de datas de emissão
    @GetMapping("/data-emissao")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByDataEmissaoBetween(
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        // Validar se ambos foram enviados
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException(
                    "Os parâmetros 'dataInicio' e 'dataFim' são obrigatórios. " +
                            "Formato: YYYY-MM-DD. Exemplo: ?dataInicio=2026-01-01&dataFim=2026-01-31"
            );
        }

        // Converte LocalDate para LocalDateTime
        LocalDateTime dataInicioTime = dataInicio.atStartOfDay();
        LocalDateTime dataFimTime = dataFim.atTime(23, 59, 59);

        List<NotaListResponseDTO> notas = notaService.findByDataEmissaoBetween(dataInicioTime, dataFimTime);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Notas encontradas com sucesso",
                notas
        );
        return ResponseEntity.ok(response);
    }

    // Rota para buscar notas por intervalo de datas de emissão
    @GetMapping("/data-cancelamento")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByDataCancelamentoBetween(
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        // Validar se ambos foram enviados
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException(
                    "Os parâmetros 'dataInicio' e 'dataFim' são obrigatórios. " +
                            "Formato: YYYY-MM-DD. Exemplo: ?dataInicio=2026-01-01&dataFim=2026-01-31"
            );
        }

        // Converte LocalDate para LocalDateTime
        LocalDateTime dataInicioTime = dataInicio.atStartOfDay();
        LocalDateTime dataFimTime = dataFim.atTime(23, 59, 59);

        List<NotaListResponseDTO> notas = notaService.findByDataCancelamentoBetween(dataInicioTime, dataFimTime);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Notas encontradas com sucesso",
                notas
        );
        return ResponseEntity.ok(response);
    }

    // Rota para buscar notas por intervalo de valor total
    @GetMapping("/valor-total")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByValorTotalBetween(
            @RequestParam(value = "valorMinimo", required = true) String valorMinimo,
            @RequestParam(value = "valorMaximo", required = true) String valorMaximo
    ) {
        List<NotaListResponseDTO> notas = notaService.findByValorTotalBetween(
                new BigDecimal(valorMinimo),
                new BigDecimal(valorMaximo)
        );

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Notas encontradas com sucesso",
                notas
        );
        return ResponseEntity.ok(response);
    }

    // Rota para buscar notas por intervalo de valor total de impostos
    @GetMapping("/valor-impostos-total")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByValorImpostosTotalBetween(
            @RequestParam(value = "valorMinimo", required = true) String valorMinimo,
            @RequestParam(value = "valorMaximo", required = true) String valorMaximo
    ) {
        List<NotaListResponseDTO> notas = notaService.findByValorImpostosTotalBetween(
                new BigDecimal(valorMinimo),
                new BigDecimal(valorMaximo)
        );

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Notas encontradas com sucesso",
                notas
        );
        return ResponseEntity.ok(response);
    }
}
