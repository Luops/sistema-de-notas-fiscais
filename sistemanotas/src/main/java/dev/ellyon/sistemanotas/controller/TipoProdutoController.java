package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoResponseDTO;
import dev.ellyon.sistemanotas.service.TipoProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
