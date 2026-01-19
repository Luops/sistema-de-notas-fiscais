package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.empresa.EmpresaListResponseDTO;
import dev.ellyon.sistemanotas.dto.empresa.EmpresaRequestDTO;
import dev.ellyon.sistemanotas.dto.empresa.EmpresaResponseDTO;
import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.service.EmpresaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/empresa")
public class EmpresaController {
    private final EmpresaService empresaService;
    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    // Rota para criar uma nova empresa
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public EmpresaResponseDTO create(@RequestBody @Valid EmpresaRequestDTO dto){
        return empresaService.create(dto);
    }

    // Rota para atualizar uma empresa
    @PutMapping("/update/{id}")
    public EmpresaResponseDTO update(@PathVariable Long id, @RequestBody @Valid EmpresaRequestDTO dto) {
        return empresaService.update(id, dto);
    }

    // Rota para deletar uma empresa
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SuccessResponseDTO> delete(@PathVariable Long id) {
        empresaService.delete(id);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresa deletada com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }

    // Rota para desativar uma empresa
    @PutMapping("/update/softDelete/{id}")
    public ResponseEntity<SuccessResponseDTO> softDelete(@PathVariable Long id) {
        empresaService.softDelete(id);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresa desativada com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }

    // Rota para ativar uma empresa
    @PutMapping("/update/activate/{id}")
    public ResponseEntity<SuccessResponseDTO> activate(@PathVariable Long id) {
        empresaService.activate(id);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresa ativada com sucesso",
                null
        );
        return ResponseEntity.ok(response);
    }

    // Rota para buscar uma empresa por ID
    @GetMapping("/findById/{id}")
    public EmpresaResponseDTO findById(@PathVariable Long id) {
        return empresaService.findById(id);
    }

    // Rota para buscar todas as empresas
    @GetMapping("/findAll")
    public ResponseEntity<List<EmpresaListResponseDTO>> findAll() {
        List<EmpresaListResponseDTO> empresas = empresaService.findAll();
        return ResponseEntity.ok(empresas);
    }

    // Rota para buscar empresas com paginação
    @GetMapping("/paginated")
    public ResponseEntity<Page<EmpresaListResponseDTO>> findAllPaged(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<EmpresaListResponseDTO> empresasPage = empresaService.findAllPaged(pageable);
        return ResponseEntity.ok(empresasPage);
    }

    // Rota para buscar empresa por CNPJ
    @GetMapping("/findByCnpj/{cnpj}")
    public EmpresaResponseDTO findByCnpj(@PathVariable String cnpj) {
        return empresaService.findByCnpj(cnpj);
    }

    // Rota para buscar empresas por razão social contendo um termo
    @GetMapping("/findByRazaoSocial")
    public ResponseEntity<List<EmpresaListResponseDTO>> findByRazaoSocialContainingIgnoreCase(
            @RequestParam(name = "razaoSocial")
            @NotBlank(message = "Razão Social não pode ser vazia") String razaoSocial){
        List<EmpresaListResponseDTO> empresas = empresaService.findByRazaoSocialContainingIgnoreCase(razaoSocial);
        return ResponseEntity.ok(empresas);
    }

    // Rota para buscar empresas pelo nome fantasia contendo um termo
    @GetMapping("/findByNomeFantasia")
    public ResponseEntity<List<EmpresaListResponseDTO>> findByNomeFantasiaContainingIgnoreCase(
            @RequestParam(name = "nomeFantasia")
            @NotBlank(message = "Nome Fantasia não pode ser vazio") String nomeFantasia) {
        List<EmpresaListResponseDTO> empresas = empresaService.findByNomeFantasiaContainingIgnoreCase(nomeFantasia);
        return ResponseEntity.ok(empresas);
    }

    // Rota para buscar empresas por email
    @GetMapping("/findByEmail/{email}")
    public ResponseEntity<List<EmpresaListResponseDTO>> findByEmailContainingIgnoreCase(
            @PathVariable String email) {
        List<EmpresaListResponseDTO> empresas = empresaService.findByEmailContainingIgnoreCase(email);
        return ResponseEntity.ok(empresas);
    }

    // Rota para buscar empresas por telefone
    @GetMapping("/findByTelefone/{telefone}")
    public ResponseEntity<List<EmpresaListResponseDTO>> findByTelefoneContaining(
            @PathVariable String telefone) {
        List<EmpresaListResponseDTO> empresas = empresaService.findByTelefoneContaining(telefone);
        return ResponseEntity.ok(empresas);
    }

    // Rota para buscar empresas por cidade
    @GetMapping("/findByCidade")
    public ResponseEntity<List<EmpresaListResponseDTO>> findByCidadeIgnoreCase(
            @RequestParam(name = "cidade")
            @NotBlank(message = "Cidade não pode ser vazia") String cidade) {
        List<EmpresaListResponseDTO> empresas = empresaService.findByCidadeIgnoreCase(cidade);
        return ResponseEntity.ok(empresas);
    }

    // Rota para buscar empresas por estado (UF)
    @GetMapping("/findByEstadoUF/{estadoUF}")
    public ResponseEntity<List<EmpresaListResponseDTO>> findByEstadoUFIgnoreCase(
            @PathVariable String estadoUF) {
        List<EmpresaListResponseDTO> empresas = empresaService.findByEstadoUFIgnoreCase(estadoUF);
        return ResponseEntity.ok(empresas);
    }

    // Rota para buscar empresas por CEP
    @GetMapping("/findByCep/{cep}")
    public ResponseEntity<List<EmpresaListResponseDTO>> findByCep(
            @PathVariable String cep) {
        List<EmpresaListResponseDTO> empresas = empresaService.findByCep(cep);
        return ResponseEntity.ok(empresas);
    }

    // Rota para buscar empresas por status de ativo/inativo
    @GetMapping("/findByAtivoInativo/{ativo}")
    public ResponseEntity<List<EmpresaListResponseDTO>> findByEmpresaAtivoInativo(
            @PathVariable Boolean ativo) {
        List<EmpresaListResponseDTO> empresas = empresaService.findByIsAtivo(ativo);
        return ResponseEntity.ok(empresas);
    }

    // Rota para buscar empresas criadas entre duas datas
    @GetMapping("/findByCreatedAtBetween")
    public ResponseEntity<List<EmpresaListResponseDTO>> findByCreatedAtBetween(
            @RequestParam(name = "dataInicio")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(name = "dataFim")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim)  {
        // Converter LocalDate para LocalDateTime no início do dia e fim do dia
        LocalDateTime inicioDateTime = dataInicio.atStartOfDay(); // 00:00:00
        LocalDateTime fimDateTime = dataFim.atTime(23, 59, 59); // 23:59:59

        List<EmpresaListResponseDTO> empresas = empresaService.findByCreatedAtBetween(inicioDateTime, fimDateTime);
        return ResponseEntity.ok(empresas);
    }
}
