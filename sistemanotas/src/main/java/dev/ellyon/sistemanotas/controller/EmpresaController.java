package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.empresa.EmpresaRequestDTO;
import dev.ellyon.sistemanotas.dto.empresa.EmpresaResponseDTO;
import dev.ellyon.sistemanotas.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
}
