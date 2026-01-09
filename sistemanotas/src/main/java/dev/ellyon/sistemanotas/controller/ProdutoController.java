package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoListResponseDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoResponseDTO;
import dev.ellyon.sistemanotas.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // Rota para buscar um produto por ID
    @GetMapping("/findById/{id}")
    public ProdutoResponseDTO findById(@PathVariable Long id) {
        return produtoService.findById(id);
    }

    // Rota para listar todos os produtos
    @GetMapping("/findAll")
    public ResponseEntity<List<ProdutoListResponseDTO>> findAll() {
        List<ProdutoListResponseDTO> produtos = produtoService.findAll();
        return ResponseEntity.ok(produtos);
    }

    // Rota para listar produtos por tipo de produto ID
    @GetMapping("/findByTipoProdutoId/{tipoProdutoId}")
    public ResponseEntity<List<ProdutoListResponseDTO>> findByTipoProdutoId(@PathVariable Long tipoProdutoId) {
        List<ProdutoListResponseDTO> produtos = produtoService.findByTipoProdutoId(tipoProdutoId);
        return ResponseEntity.ok(produtos);
    }

    // Rota para listar produtos por tipo de produto Nome
    @GetMapping("/findByTipoProdutoNome/{tipoProdutoNome}")
    public ResponseEntity<List<ProdutoListResponseDTO>> findByTipoProdutoNome(@PathVariable String tipoProdutoNome) {
        List<ProdutoListResponseDTO> produtos = produtoService.findByTipoProdutoNome(tipoProdutoNome);
        return ResponseEntity.ok(produtos);
    }

    // Rota para listar produtos por ativo/inativo
    @GetMapping("/findByAtivoInativo/{ativo}")
    public ResponseEntity<List<ProdutoListResponseDTO>> findByTipoProdutoAtivoInativo(@PathVariable Boolean ativo) {
        List<ProdutoListResponseDTO> produtos = produtoService.findByIsAtivo(ativo);
        return ResponseEntity.ok(produtos);
    }

    // Rota para listar produtos por nome (contendo, case insensitive)
    @GetMapping("/findByNome/{nome}")
    public ResponseEntity<List<ProdutoListResponseDTO>> findByNomeContainingIgnoreCase(@PathVariable String nome) {
        List<ProdutoListResponseDTO> produtos = produtoService.findByNomeContainingIgnoreCase(nome);
        return ResponseEntity.ok(produtos);
    }

    // Rota para listar produtos pelo codigo do produto (contendo, case insensitive)
    @GetMapping("/findByCodigo/{codigo}")
    public ResponseEntity<List<ProdutoListResponseDTO>> findByCodigoProdutoContainingIgnoreCase(@PathVariable String codigo) {
        List<ProdutoListResponseDTO> produtos = produtoService.findByCodigoProdutoContainingIgnoreCase(codigo);
        return ResponseEntity.ok(produtos);
    }

    // Rota para listar produtos por faixa de preço de venda
    @GetMapping("/findByPrecoVendaBetween")
    public ResponseEntity<List<ProdutoListResponseDTO>> findByPrecoVendaBetween(
            @RequestParam("precoMinimo") String precoMinimo,
            @RequestParam("precoMaximo") String precoMaximo) {
        List<ProdutoListResponseDTO> produtos = produtoService.findByPrecoVendaBetween(
                new java.math.BigDecimal(precoMinimo),
                new java.math.BigDecimal(precoMaximo));
        return ResponseEntity.ok(produtos);
    }

    // Rota para listar produtos por faixa de data de criação
    @GetMapping("/findByCreatedAtBetween")
    public ResponseEntity<List<ProdutoListResponseDTO>> findByCreatedAtBetween(
            @RequestParam(name = "dataInicio")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

            @RequestParam(name = "dataFim")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        // Converte LocalDate para LocalDateTime (início do dia e fim do dia)
        LocalDateTime dataInicioTime = dataInicio.atStartOfDay(); // 00:00:00
        LocalDateTime dataFimTime = dataFim.atTime(23, 59, 59);   // 23:59:59

        List<ProdutoListResponseDTO> produtos = produtoService.findByCreatedAtBetween(dataInicioTime, dataFimTime);
        return ResponseEntity.ok(produtos);
    }

    // Rota para listar todos os produtos com paginação
    @GetMapping("/paginated")
    public ResponseEntity<Page<ProdutoListResponseDTO>> findAllPaginated(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ProdutoListResponseDTO> produtos = produtoService.findAllPaged(pageable);
        return ResponseEntity.ok(produtos);
    }
}
