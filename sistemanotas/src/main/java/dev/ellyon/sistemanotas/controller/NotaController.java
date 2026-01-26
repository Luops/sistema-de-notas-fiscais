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

}
