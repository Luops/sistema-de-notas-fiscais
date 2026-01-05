package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.produto.ProdutoRequestDTO;
import dev.ellyon.sistemanotas.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/produto")
public class ProdutoController {
    private final ProdutoService produtoService;
    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestBody @Valid ProdutoRequestDTO dto) {

        produtoService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
