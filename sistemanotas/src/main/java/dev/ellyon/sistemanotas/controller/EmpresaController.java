package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.empresa.EmpresaListResponseDTO;
import dev.ellyon.sistemanotas.dto.empresa.EmpresaRequestDTO;
import dev.ellyon.sistemanotas.dto.empresa.EmpresaResponseDTO;
import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
