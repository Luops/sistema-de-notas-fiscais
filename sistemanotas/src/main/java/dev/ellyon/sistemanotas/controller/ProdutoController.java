package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoListResponseDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoResponseDTO;
import dev.ellyon.sistemanotas.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> create(@RequestBody @Valid ProdutoRequestDTO dto, Authentication authentication) {
        // Chamar o serviço para criar o produto
        ProdutoResponseDTO result = produtoService.create(dto, authentication);

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Produto criado com sucesso",
                result
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
    }

    // Rota para deletar um produto
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> delete(@PathVariable Long id, Authentication authentication) {
        produtoService.delete(id, authentication);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produto deletado com sucesso",
                null // ou produtoDeletado
        );

        return ResponseEntity.ok(response);
    }

    // Rota para atualizar um produto
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> update(@PathVariable Long id, @RequestBody @Valid ProdutoRequestDTO dto, Authentication authentication) {
        produtoService.update(id, dto, authentication);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produto atualizado com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }

    // Rota para desativar (soft delete) um produto
    @PutMapping("/update/softDelete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> softDelete(@PathVariable Long id, Authentication authentication) {
        produtoService.softDelete(id, authentication);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produto desativado com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }

    // Rota para ativar um produto
    @PutMapping("/update/activate/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> activate(@PathVariable Long id, Authentication authentication) {
        produtoService.activate(id, authentication);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produto ativado com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }

    // Rota para buscar um produto por ID
    @GetMapping("/findById/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findById(@PathVariable Long id, Authentication authentication) {
        ProdutoResponseDTO produtoEncontrado = produtoService.findById(id, authentication);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produto encontrado com sucesso",
                produtoEncontrado
        );

        return ResponseEntity.ok(response);
    }

    // Rota para listar todos os produtos
    @GetMapping("/findAll")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<ProdutoListResponseDTO>> findAll(Authentication authentication) {
        List<ProdutoListResponseDTO> produtos = produtoService.findAll(authentication);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produtos encontrados com sucesso",
                null // ou produtoEncontrado
        );

        return ResponseEntity.ok(produtos);
    }

    // Rota para listar produtos por tipo de produto ID
    @GetMapping("/findByTipoProdutoId/{tipoProdutoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<ProdutoListResponseDTO>> findByTipoProdutoId(@PathVariable Long tipoProdutoId, Authentication authentication) {
        List<ProdutoListResponseDTO> produtos = produtoService.findByTipoProdutoId(tipoProdutoId, authentication);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produtos encontrados com sucesso",
                null // ou produtoEncontrado
        );

        return ResponseEntity.ok(produtos);
    }

    // Rota para listar produtos por tipo de produto Nome
    @GetMapping("/findByTipoProdutoNome/{tipoProdutoNome}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<ProdutoListResponseDTO>> findByTipoProdutoNome(@PathVariable String tipoProdutoNome, Authentication authentication) {
        List<ProdutoListResponseDTO> produtos = produtoService.findByTipoProdutoNome(tipoProdutoNome, authentication);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produtos encontrados com sucesso",
                null // ou produtoEncontrado
        );

        return ResponseEntity.ok(produtos);
    }

    // Rota para listar produtos por ativo/inativo
    @GetMapping("/findByAtivoInativo/{ativo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<ProdutoListResponseDTO>> findByTipoProdutoAtivoInativo(@PathVariable Boolean ativo, Authentication authentication) {
        List<ProdutoListResponseDTO> produtos = produtoService.findByIsAtivo(ativo, authentication);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produtos encontrados com sucesso",
                null // ou produtoEncontrado
        );

        return ResponseEntity.ok(produtos);
    }

    // Rota para listar produtos por nome (contendo, case insensitive)
    @GetMapping("/findByNome/{nome}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<ProdutoListResponseDTO>> findByNomeContainingIgnoreCase(@PathVariable String nome, Authentication authentication) {
        List<ProdutoListResponseDTO> produtos = produtoService.findByNomeContainingIgnoreCase(nome, authentication);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produtos encontrados com sucesso",
                null // ou produtoEncontrado
        );

        return ResponseEntity.ok(produtos);
    }

    // Rota para listar produtos pelo codigo do produto (contendo, case insensitive)
    @GetMapping("/findByCodigo/{codigo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<ProdutoListResponseDTO>> findByCodigoProdutoContainingIgnoreCase(@PathVariable String codigo, Authentication authentication) {
        List<ProdutoListResponseDTO> produtos = produtoService.findByCodigoProdutoContainingIgnoreCase(codigo, authentication);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produtos encontrados com sucesso",
                null // ou produtoEncontrado
        );

        return ResponseEntity.ok(produtos);
    }

    // Rota para listar produtos por faixa de preço de venda
    @GetMapping("/findByPrecoVendaBetween")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<ProdutoListResponseDTO>> findByPrecoVendaBetween(
            @RequestParam("precoMinimo") String precoMinimo,
            @RequestParam("precoMaximo") String precoMaximo, Authentication authentication) {
        List<ProdutoListResponseDTO> produtos = produtoService.findByPrecoVendaBetween(
                new java.math.BigDecimal(precoMinimo),
                new java.math.BigDecimal(precoMaximo), authentication);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produtos encontrados com sucesso",
                null // ou produtoEncontrado
        );

        return ResponseEntity.ok(produtos);
    }

    // Rota para listar produtos por faixa de data de criação
    @GetMapping("/findByCreatedAtBetween")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<ProdutoListResponseDTO>> findByCreatedAtBetween(
            @RequestParam(name = "dataInicio")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(name = "dataFim")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim, Authentication authentication) {

        // Converte LocalDate para LocalDateTime (início do dia e fim do dia)
        LocalDateTime dataInicioTime = dataInicio.atStartOfDay(); // 00:00:00
        LocalDateTime dataFimTime = dataFim.atTime(23, 59, 59);   // 23:59:59

        List<ProdutoListResponseDTO> produtos = produtoService.findByCreatedAtBetween(dataInicioTime, dataFimTime, authentication);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produtos encontrados com sucesso",
                null // ou produtoEncontrado
        );

        return ResponseEntity.ok(produtos);
    }

    // Rota para listar todos os produtos com paginação
    @GetMapping("/paginated")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<Page<ProdutoListResponseDTO>> findAllPaginated(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable, Authentication authentication) {
        Page<ProdutoListResponseDTO> produtos = produtoService.findAllPaged(pageable, authentication);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produtos encontrados com sucesso",
                null // ou produtoEncontrado
        );

        return ResponseEntity.ok(produtos);
    }
}
