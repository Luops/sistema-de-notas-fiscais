package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.cliente.ClienteRequestDTO;
import dev.ellyon.sistemanotas.dto.cliente.ClienteResponseDTO;
import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cliente")
public class ClienteController {
    private final ClienteService clienteService;
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Rota para criar um novo cliente
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponseDTO create(@RequestBody @Valid ClienteRequestDTO dto) {
        return clienteService.create(dto);
    }

    // Rota para deletar um produto
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SuccessResponseDTO> delete(@PathVariable Long id) {
        clienteService.delete(id);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Cliente deletado com sucesso",
                null // ou produtoDeletado
        );

        return ResponseEntity.ok(response);
    }

}
