package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoResponseDTO;
import dev.ellyon.sistemanotas.model.TipoProduto;
import dev.ellyon.sistemanotas.service.TipoProdutoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tipoProduto")
public class TipoProdutoController {
    private final TipoProdutoService tipoProdutoService;
    public TipoProdutoController(TipoProdutoService tipoProdutoService) {
        this.tipoProdutoService = tipoProdutoService;
    }

    // Rota para criar um tipo de produto
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> create(@RequestBody @Valid TipoProdutoRequestDTO dto, Authentication authentication) {
        TipoProdutoResponseDTO response = tipoProdutoService.create(dto, authentication);

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Tipo Produto criado com sucesso",
                response
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
    }

    // Rota para atualizar um tipo de produto
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> update(@PathVariable Long id, @RequestBody @Valid TipoProdutoRequestDTO dto, Authentication authentication) {
        TipoProdutoResponseDTO response = tipoProdutoService.update(id, dto, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Tipo de produto atualizado com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para deletar um tipo de produto
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> delete(@PathVariable Long id, Authentication authentication) {
        tipoProdutoService.delete(id, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Tipo de produto deletado com sucesso",
                null
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para desativar um tipo de produto
    @PutMapping("/update/softDelete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> softDelete(@PathVariable Long id, Authentication authentication) {
        tipoProdutoService.softDelete(id, authentication);
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Tipo de produto desativado com sucesso",
                null
        );
        return ResponseEntity.ok(successResponse);
    }

    // Rota para ativar um tipo de produto
    @PutMapping("/update/activate/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> activate(@PathVariable Long id) {
        tipoProdutoService.activate(id, null);
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Tipo de produto ativado com sucesso",
                null
        );
        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar um tipo de produto por ID
    @GetMapping("/findById/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findById(@PathVariable Long id, Authentication authentication) {
        TipoProdutoResponseDTO response = tipoProdutoService.findById(id, authentication);
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produto encontrado com sucesso",
                response
        );
        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar todos os produtos
    @GetMapping("/findAll")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<TipoProdutoResponseDTO>> findAll(Authentication authentication) {
        List<TipoProdutoResponseDTO> response = tipoProdutoService.findAll(authentication);

        return ResponseEntity.ok(response);
    }

    // Rota para buscar tipos de produtos por status (ativo/inativo)
    @GetMapping("/findByAtivoInativo/{ativo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<TipoProdutoResponseDTO>> findByAtivoInativo(@PathVariable Boolean ativo, Authentication authentication) {
        List<TipoProdutoResponseDTO> response = tipoProdutoService.findByAtivoInativo(ativo, authentication);
        return ResponseEntity.ok(response);
    }

    // Rota para buscar tipos de produtos por nome (contendo, case insensitive)
    @GetMapping("/findByNome/{nome}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<TipoProdutoResponseDTO>> findByNomeContainingIgnoreCase(@PathVariable String nome, Authentication authentication) {
        List<TipoProdutoResponseDTO> response = tipoProdutoService.findByNomeContainingIgnoreCase(nome, authentication);
        return ResponseEntity.ok(response);
    }

    // Rota para buscar tipos de produtos criados entre duas datas
    @GetMapping("/findByCreatedAtBetween")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<TipoProdutoResponseDTO>> findByCreatedAtBetween(
            @RequestParam("dataInicio")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate dataInicio,
            @RequestParam("dataFim")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate dataFim, Authentication authentication) { // Converter LocalDate para LocalDateTime no início do dia e fim do dia
        LocalDateTime inicioDateTime = dataInicio.atStartOfDay(); // 00:00:00
        LocalDateTime fimDateTime = dataFim.atTime(23, 59, 59); // 23:59:59

        List<TipoProdutoResponseDTO> response = tipoProdutoService.findByCreatedAtBetween(inicioDateTime, fimDateTime, authentication);
        return ResponseEntity.ok(response);
    }
}
