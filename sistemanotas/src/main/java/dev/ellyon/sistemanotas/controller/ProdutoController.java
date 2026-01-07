package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoListResponseDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoResponseDTO;
import dev.ellyon.sistemanotas.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/produto")
public class ProdutoController {
    private final ProdutoService produtoService;
    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // Rota para criar um novo produto
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponseDTO create(@RequestBody @Valid ProdutoRequestDTO dto) {
        return produtoService.create(dto);
    }

    // Rota para deletar um produto
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SuccessResponseDTO> delete(@PathVariable Long id) {
        produtoService.delete(id);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produto deletado com sucesso",
                null // ou produtoDeletado
        );

        return ResponseEntity.ok(response);
    }

    // Rota para atualizar um produto
    @PutMapping("/update/{id}")
    public ProdutoResponseDTO update(@PathVariable Long id, @RequestBody @Valid ProdutoRequestDTO dto) {
        return produtoService.update(id, dto);
    }

    // Rota para desativar (soft delete) um produto
    @PutMapping("/update/softDelete/{id}")
    public ResponseEntity<SuccessResponseDTO> softDelete(@PathVariable Long id) {
        produtoService.softDelete(id);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produto desativado com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }

    // Rota para ativar um produto
    @PutMapping("/update/activate/{id}")
    public ResponseEntity<SuccessResponseDTO> activate(@PathVariable Long id) {
        produtoService.activate(id);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produto ativado com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }

    // Rota para listar todos os produtos
    @GetMapping("/findAll")
    public ResponseEntity<List<ProdutoListResponseDTO>> findAll() {
        List<ProdutoListResponseDTO> produtos = produtoService.findAll();
        return ResponseEntity.ok(produtos);
    }
}
