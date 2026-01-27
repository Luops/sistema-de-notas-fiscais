package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.dto.itemNota.ItemNotaRequestDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaRequestDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaResponseDTO;
import dev.ellyon.sistemanotas.service.NotaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<SuccessResponseDTO> create(@RequestBody @Valid NotaRequestDTO dto) {
        NotaResponseDTO notaResponseDTO = notaService.create(dto);
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
    public ResponseEntity<SuccessResponseDTO> addItem(
            @PathVariable Long notaId,
            @RequestBody @Valid ItemNotaRequestDTO itemNotaRequestDTO) {
        NotaResponseDTO notaResponseDTO = notaService.addItem(notaId, itemNotaRequestDTO);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Item adicionado com sucesso",
                notaResponseDTO
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para atualizar item da nota
    @PutMapping("/{notaId}/update-item/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SuccessResponseDTO> updateItem(
            @PathVariable Long notaId,
            @PathVariable Long itemId,
            @RequestBody @Valid ItemNotaRequestDTO itemNotaRequestDTO) {
        NotaResponseDTO notaResponseDTO = notaService.updateItem(notaId, itemId, itemNotaRequestDTO);
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
    public ResponseEntity<SuccessResponseDTO> removeItem(
            @PathVariable Long notaId,
            @PathVariable Long itemId) {
        NotaResponseDTO notaResponseDTO = notaService.removeItem(notaId, itemId);
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
    public ResponseEntity<SuccessResponseDTO> emitirNota(@PathVariable Long notaId) {
        NotaResponseDTO notaResponseDTO = notaService.emitirNota(notaId);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Nota emitida com sucesso",
                notaResponseDTO
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Rota para atualizar dados da nota
    @PutMapping("/update/{notaId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SuccessResponseDTO> updateNota(
            @PathVariable Long notaId,
            @RequestBody @Valid NotaRequestDTO dto) {
        NotaResponseDTO notaResponseDTO = notaService.updateNota(notaId, dto);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Nota atualizada com sucesso",
                notaResponseDTO
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
