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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cliente")
public class ClienteController {
    // Injeção de dependência do ClienteService
    private final ClienteService clienteService;

    // Construtor para injeção de dependência
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Rota para criar um novo cliente
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> create(@RequestBody @Valid ClienteRequestDTO dto,
                                                     Authentication authentication) {

        // Chamar serviço para criar
        ClienteResponseDTO response = clienteService.create(dto, authentication);

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Cliente criado com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para deletar um cliente
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> delete(@PathVariable Long id, String senha, Authentication authentication) {

        // Chamar serviço para deletar
        clienteService.delete(id, senha, authentication);

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Cliente deletado com sucesso",
                null  // DELETE geralmente retorna null no data
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para atualizar um cliente
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> update(@PathVariable Long id, @RequestBody @Valid ClienteRequestDTO dto, Authentication authentication) {

        // Chamar serviço para atualizar
        ClienteResponseDTO response = clienteService.update(id, dto, authentication);

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Cliente atualizado com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para desativar (soft delete) um cliente
    @PutMapping("/update/softDelete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> softDelete(@PathVariable Long id, Authentication authentication) {

        // Chamar serviço para soft delete
        clienteService.softDelete(id, authentication);

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Cliente desativado com sucesso",
                null  // SOFT_DELETE geralmente retorna null
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para ativar um cliente
    @PutMapping("/update/activate/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> activate(@PathVariable Long id, Authentication authentication) {

        // Chamar serviço para ativar
        clienteService.activate(id, authentication);

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Cliente ativado com sucesso",
                null  // ACTIVATE geralmente retorna null
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar um cliente por ID
    @GetMapping("/findById/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findById(@PathVariable Long id, Authentication authentication) {

        // Chamar serviço para buscar
        ClienteResponseDTO response = clienteService.findById(id, authentication);

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Cliente encontrado com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para listar todos os clientes
    @GetMapping("/findAll")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findAll(Authentication authentication) {

        // Chamar serviço para buscar todos
        List<ClienteListResponseDTO> response = clienteService.findAll(authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (response.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para listar todos clientes com paginacao
    @GetMapping("/paginated")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findAllPaged(
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable, Authentication authentication) {

        // Chamar serviço para buscar com paginação
        Page<ClienteListResponseDTO> response = clienteService.findAllPaged(pageable, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (response.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar um cliente por CPF/CNPJ
    @GetMapping("/findByCpfCnpj/{cpfCnpj}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByCpfCnpj(
            @PathVariable String cpfCnpj, Authentication authentication) {

        // Chamar serviço para buscar
        ClienteResponseDTO response = clienteService.findByCpfCnpj(cpfCnpj, authentication);

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Cliente encontrado com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para listar clientes por tipo de pessoa
    @GetMapping("/findByTipoPessoa/{tipoPessoa}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByTipoPessoa(@PathVariable String tipoPessoa, Authentication authentication) {

        // Chamar serviço para buscar
        List<ClienteListResponseDTO> response = clienteService.findByTipoPessoa(tipoPessoa, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (response.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar clientes por email (contendo, case insensitive)
    @GetMapping("/findByEmail/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByEmail(@PathVariable String email, Authentication authentication) {

        // Chamar serviço para buscar
        List<ClienteListResponseDTO> response = clienteService.findByEmailContainingIgnoreCase(email, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (response.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar clientes por telefone (contendo, case insensitive)
    @GetMapping("/findByTelefone/{telefone}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByTelefoneContainingIgnoreCase(@PathVariable String telefone, Authentication authentication) {

        // Chamar serviço para buscar
        List<ClienteListResponseDTO> response = clienteService.findByTelefoneContainingIgnoreCase(telefone, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (response.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar clientes por cidade (contendo, case insensitive)
    @GetMapping("/findByCidade/{cidade}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByCidadeContainingIgnoreCase(@PathVariable String cidade, Authentication authentication) {

        // Chamar serviço para buscar
        List<ClienteListResponseDTO> response = clienteService.findByCidadeContainingIgnoreCase(cidade, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (response.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar clientes por estado
    @GetMapping("/findByEstadoUF/{estadoUF}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByEstadoUF(@PathVariable String estadoUF, Authentication authentication) {

        // Chamar serviço para buscar
        List<ClienteListResponseDTO> response = clienteService.findByEstadoUF(estadoUF, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (response.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Buscar clientes por CEP
    @GetMapping("/findByCEP/{cep}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByCep(@PathVariable String cep, Authentication authentication) {

        // Chamar serviço para buscar
        List<ClienteListResponseDTO> response = clienteService.findByCep(cep, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (response.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar clientes por status (Ativo/Inativo)
    @GetMapping("/findByAtivoInativo/{isAtivo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByAtivo(
            @PathVariable boolean isAtivo, Authentication authentication) {

        // Chamar serviço para buscar
        List<ClienteListResponseDTO> response = clienteService.findByIsAtivo(isAtivo, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (response.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar clientes pela data de criação
    @GetMapping("/findByCreatedAtBetween")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByCreatedAtBetween(
            @RequestParam(name = "dataInicio")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(name = "dataFim")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim, Authentication authentication) {
        // Converte LocalDate para LocalDateTime (início do dia e fim do dia)
        LocalDateTime dataInicioTime = dataInicio.atStartOfDay(); // 00:00:00
        LocalDateTime dataFimTime = dataFim.atTime(23, 59, 59);   // 23:59:59

        // Chamar serviço para buscar clientes entre as datas
        List<ClienteListResponseDTO> response = clienteService.findByCreatedAtBetween(dataInicioTime, dataFimTime, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (response.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar clientes por nome (contendo, case insensitive)
    @GetMapping("/findByNome/{nome}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByNome(@PathVariable String nome, Authentication authentication) {

        // Chamar serviço para buscar
        List<ClienteListResponseDTO> response = clienteService.findByNomeContainingIgnoreCase(nome, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (response.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);
    }
}
