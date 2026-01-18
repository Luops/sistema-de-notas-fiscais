package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.cliente.ClienteListResponseDTO;
import dev.ellyon.sistemanotas.dto.cliente.ClienteRequestDTO;
import dev.ellyon.sistemanotas.dto.cliente.ClienteResponseDTO;
import dev.ellyon.sistemanotas.dto.generics.SuccessResponseDTO;
import dev.ellyon.sistemanotas.service.ClienteService;
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

    // Rota para deletar um cliente
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SuccessResponseDTO> delete(@PathVariable Long id) {
        clienteService.delete(id);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Cliente deletado com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }

    // Rota para atualizar um cliente
    @PutMapping("/update/{id}")
    public ClienteResponseDTO update(@PathVariable Long id, @RequestBody @Valid ClienteRequestDTO dto) {
        return clienteService.update(id, dto);
    }

    // Rota para desativar (soft delete) um cliente
    @PutMapping("/update/softDelete/{id}")
    public ResponseEntity<SuccessResponseDTO> softDelete(@PathVariable Long id) {
        clienteService.softDelete(id);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Cliente desativado com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }

    // Rota para ativar um cliente
    @PutMapping("/update/activate/{id}")
    public ResponseEntity<SuccessResponseDTO> activate(@PathVariable Long id) {
        clienteService.activate(id);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Cliente ativado com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }

    // Rota para buscar um cliente por ID
    @GetMapping("/findById/{id}")
    public ClienteResponseDTO findById(@PathVariable Long id) {
        return clienteService.findById(id);
    }

    // Rota para listar todos os clientes
    @GetMapping("/findAll")
    public ResponseEntity<List<ClienteListResponseDTO>> findAll() {
        List<ClienteListResponseDTO> clientes = clienteService.findAll();
        return ResponseEntity.ok(clientes);
    }

    // Rota para listar todos clientes com paginacao
    @GetMapping("/paginated")
    public ResponseEntity<Page<ClienteListResponseDTO>> findAllPaged(
           @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC)Pageable pageable
    ) {
        Page<ClienteListResponseDTO> clientes = clienteService.findAllPaged(pageable);
        return ResponseEntity.ok(clientes);
    }

    // Rota para buscar um cliente por CPF/CNPJ
    @GetMapping("/findByCpfCnpj/{cpfCnpj}")
    public ClienteResponseDTO findByCpfCnpj(@PathVariable String cpfCnpj) {
        return clienteService.findByCpfCnpj(cpfCnpj);
    }

    // Rota para listar clientes por tipo de pessoa
    @GetMapping("/findByTipoPessoa/{tipoPessoa}")
    public ResponseEntity<List<ClienteListResponseDTO>> findByTipoPessoa(@PathVariable String tipoPessoa) {
        List<ClienteListResponseDTO> clientes = clienteService.findByTipoPessoa(tipoPessoa);
        return ResponseEntity.ok(clientes);
    }

    // Rota para buscar clientes por email (contendo, case insensitive)
    @GetMapping("/findByEmail/{email}")
    public ResponseEntity<List<ClienteListResponseDTO>> findByEmailContainingIgnoreCase(@PathVariable String email) {
        List<ClienteListResponseDTO> clientes = clienteService.findByEmailContainingIgnoreCase(email);
        return ResponseEntity.ok(clientes);
    }

    // Rota para buscar clientes por telefone (contendo, case insensitive)
    @GetMapping("/findByTelefone/{telefone}")
    public ResponseEntity<List<ClienteListResponseDTO>> findByTelefoneContainingIgnoreCase(@PathVariable String telefone) {
        List<ClienteListResponseDTO> clientes = clienteService.findByTelefoneContainingIgnoreCase(telefone);
        return ResponseEntity.ok(clientes);
    }

    // Rota para buscar clientes por cidade (contendo, case insensitive)
    @GetMapping("/findByCidade/{cidade}")
    public ResponseEntity<List<ClienteListResponseDTO>> findByCidadeContainingIgnoreCase(@PathVariable String cidade) {
        List<ClienteListResponseDTO> clientes = clienteService.findByCidadeContainingIgnoreCase(cidade);
        return ResponseEntity.ok(clientes);
    }

    // Rota para buscar clientes por estado
    @GetMapping("/findByEstadoUF/{estadoUF}")
    public ResponseEntity<List<ClienteListResponseDTO>> findByEstadoUF(@PathVariable String estadoUF) {
        List<ClienteListResponseDTO> clientes = clienteService.findByEstadoUF(estadoUF);
        return ResponseEntity.ok(clientes);
    }

    // Buscar clientes por CEP
    @GetMapping("/findByCEP/{cep}")
    public ResponseEntity<List<ClienteListResponseDTO>> findByCep(@PathVariable String cep) {
        List<ClienteListResponseDTO> clientes = clienteService.findByCep(cep);
        return ResponseEntity.ok(clientes);
    }

    // Rota para buscar clientes por status (Ativo/Inativo)
    @GetMapping("/findByIsAtivo/{ativo}")
    public ResponseEntity<List<ClienteListResponseDTO>> findByIsAtivo(@PathVariable Boolean ativo) {
        List<ClienteListResponseDTO> clientes = clienteService.findByIsAtivo(ativo);
        return ResponseEntity.ok(clientes);
    }

    // Rota para buscar clientes pela data de criação
    @GetMapping("/findByCreatedAtBetween")
    public ResponseEntity<List<ClienteListResponseDTO>> findByCreatedAtBetween(
            @RequestParam(name = "dataInicio")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(name = "dataFim")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        // Converte LocalDate para LocalDateTime (início do dia e fim do dia)
        LocalDateTime dataInicioTime = dataInicio.atStartOfDay(); // 00:00:00
        LocalDateTime dataFimTime = dataFim.atTime(23, 59, 59);   // 23:59:59

        List<ClienteListResponseDTO> clientes = clienteService.findByCreatedAtBetween(dataInicioTime, dataFimTime);
        return ResponseEntity.ok(clientes);
    }

    // Rota para buscar clientes por nome (contendo, case insensitive)
    @GetMapping("/findByNome/{nome}")
    public ResponseEntity<List<ClienteListResponseDTO>> findByNomeContainingIgnoreCase(@PathVariable String nome) {
        List<ClienteListResponseDTO> clientes = clienteService.findByNomeContainingIgnoreCase(nome);
        return ResponseEntity.ok(clientes);
    }
}
