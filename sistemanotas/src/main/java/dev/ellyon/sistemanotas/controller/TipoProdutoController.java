package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoResponseDTO;
import dev.ellyon.sistemanotas.service.TipoProdutoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public TipoProdutoResponseDTO create(@RequestBody @Valid TipoProdutoRequestDTO dto){
        return tipoProdutoService.create(dto);
    }

    // Rota para atualizar um tipo de produto
    @PutMapping("/update/{id}")
    public TipoProdutoResponseDTO update(@PathVariable Long id, @RequestBody @Valid TipoProdutoRequestDTO dto) {
        return tipoProdutoService.update(id, dto);
    }

    // Rota para deletar um tipo de produto
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SuccessResponseDTO> delete(@PathVariable Long id){
        tipoProdutoService.delete(id);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Tipo de produto deletado com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }

    // Rota para desativar um tipo de produto
    @PutMapping("/update/softDelete/{id}")
    public ResponseEntity<SuccessResponseDTO> softDelete(@PathVariable Long id) {
        tipoProdutoService.softDelete(id);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Tipo de produto desativado com sucesso",
                null
        );
        return ResponseEntity.ok(response);
    }

    // Rota para ativar um tipo de produto
    @PutMapping("/update/activate/{id}")
    public ResponseEntity<SuccessResponseDTO> activate(@PathVariable Long id) {
        tipoProdutoService.activate(id);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Tipo de produto ativado com sucesso",
                null
        );
        return ResponseEntity.ok(response);
    }

    // Rota para buscar um tipo de produto por ID
    @GetMapping("/findById/{id}")
    public TipoProdutoResponseDTO findById(@PathVariable Long id) {
        return tipoProdutoService.findById(id);
    }

    // Rota para buscar todos os produtos
    @GetMapping("/findAll")
    public ResponseEntity<List<TipoProdutoResponseDTO>> findAll() {
        List<TipoProdutoResponseDTO> tipoProdutos = tipoProdutoService.findAll();
        return ResponseEntity.ok(tipoProdutos);
    }

    // Rota para buscar tipos de produtos por status (ativo/inativo)
    @GetMapping("/findByAtivoInativo/{ativo}")
    public ResponseEntity<List<TipoProdutoResponseDTO>> findByAtivoInativo(@PathVariable Boolean ativo) {
        List<TipoProdutoResponseDTO> tipoProdutos = tipoProdutoService.findByAtivoInativo(ativo);
        return ResponseEntity.ok(tipoProdutos);
    }

    // Rota para buscar tipos de produtos por nome (contendo, case insensitive)
    @GetMapping("/findByNome/{nome}")
    public ResponseEntity<List<TipoProdutoResponseDTO>> findByNomeContainingIgnoreCase(@PathVariable String nome) {
        List<TipoProdutoResponseDTO> tipoProdutos = tipoProdutoService.findByNomeContainingIgnoreCase(nome);
        return ResponseEntity.ok(tipoProdutos);
    }

    // Rota para buscar tipos de produtos criados entre duas datas
    @GetMapping("/findByCreatedAtBetween")
    public ResponseEntity<List<TipoProdutoResponseDTO>> findByCreatedAtBetween(
            @RequestParam("dataInicio")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate dataInicio,
            @RequestParam("dataFim")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate dataFim) { // Converter LocalDate para LocalDateTime no início do dia e fim do dia
        LocalDateTime inicioDateTime = dataInicio.atStartOfDay(); // 00:00:00
        LocalDateTime fimDateTime = dataFim.atTime(23, 59, 59); // 23:59:59

        List<TipoProdutoResponseDTO> tipoProdutos = tipoProdutoService.findByCreatedAtBetween(inicioDateTime, fimDateTime);
        return ResponseEntity.ok(tipoProdutos);
    }
}
