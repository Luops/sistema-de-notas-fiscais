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

    /**
     * Criar um novo cliente.
     *
     * @param dto Dados do cliente a ser criado
     * @return DTO do cliente criado
     * @status 201 CREATED - Cliente criado com sucesso
     * @status 400 BAD_REQUEST - Dados inválidos
     * @status 409 CONFLICT - Recurso já existe (CPF/Email duplicado)
     */
    // Rota para criar um novo cliente
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> create(@RequestBody @Valid ClienteRequestDTO dto,
                                                     Authentication authentication) {

        // Chamar serviço para criar
        ClienteResponseDTO result = clienteService.create(dto, authentication);

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Cliente criado com sucesso",
                result
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Deletar permanentemente um cliente.
     * ATENÇÃO: Operação irreversível. Verifica dependências.
     *
     * @param id ID do cliente a deletar
     * @status 200 OK - Cliente deletado com sucesso
     * @status 404 NOT_FOUND - Cliente não existe
     * @status 409 CONFLICT - Cliente tem dependências
     */
    // Rota para deletar um cliente
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> delete(@PathVariable Long id, Authentication authentication) {

        // Chamar serviço para deletar
        clienteService.delete(id, authentication);

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Cliente deletado com sucesso",
                null  // DELETE geralmente retorna null no data
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Atualizar um cliente existente.
     *
     * @param id ID do cliente a atualizar
     * @param dto Novos dados do cliente
     * @return DTO do cliente atualizado
     * @status 200 OK - Cliente atualizado com sucesso
     * @status 404 NOT_FOUND - Cliente não existe
     * @status 400 BAD_REQUEST - Dados inválidos
     */
    // Rota para atualizar um cliente
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> update(@PathVariable Long id, @RequestBody @Valid ClienteRequestDTO dto, Authentication authentication) {

        // Chamar serviço para atualizar
        ClienteResponseDTO result = clienteService.update(id, dto, authentication);

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Cliente atualizado com sucesso",
                result
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Desativar um cliente (Soft Delete).
     * O registro permanece no banco com status inativo.
     *
     * @param id ID do cliente a desativar
     * @status 200 OK - Cliente desativado com sucesso
     * @status 404 NOT_FOUND - Cliente não existe
     */
    // Rota para desativar (soft delete) um cliente
    @PutMapping("/update/softDelete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> softDelete(@PathVariable Long id, Authentication authentication) {

        // Chamar serviço para soft delete
        clienteService.softDelete(id, authentication);

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Cliente desativado com sucesso",
                null  // SOFT_DELETE geralmente retorna null
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Ativar um cliente inativo.
     *
     * @param id ID do cliente a ativar
     * @status 200 OK - Cliente ativado com sucesso
     * @status 404 NOT_FOUND - Cliente não existe
     * @status 400 BAD_REQUEST - Cliente já está ativo
     */
    // Rota para ativar um cliente
    @PutMapping("/update/activate/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> activate(@PathVariable Long id, Authentication authentication) {

        // Chamar serviço para ativar
        clienteService.activate(id, authentication);

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Cliente ativado com sucesso",
                null  // ACTIVATE geralmente retorna null
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Buscar um cliente pelo ID.
     *
     * @param id ID do cliente
     * @return DTO do cliente encontrado
     * @status 200 OK - Cliente encontrado
     * @status 404 NOT_FOUND - Cliente não existe
     */
    // Rota para buscar um cliente por ID
    @GetMapping("/findById/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findById(@PathVariable Long id, Authentication authentication) {

        // Chamar serviço para buscar
        ClienteResponseDTO result = clienteService.findById(id, authentication);

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Cliente encontrado com sucesso",
                result
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Buscar todos os clientes.
     *
     * @return Lista de DTOs de clientes
     * @status 200 OK - Clientes encontrados
     * @status 204 NO_CONTENT - Nenhum cliente cadastrado
     */
    // Rota para listar todos os clientes
    @GetMapping("/findAll")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findAll(Authentication authentication) {

        // Chamar serviço para buscar todos
        List<ClienteListResponseDTO> result = clienteService.findAll(authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                result
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Buscar todos os clientes com paginação.
     *
     * @param pageable Parâmetros de paginação
     * @return Página de DTOs de clientes
     * @status 200 OK - Clientes encontrados
     * @status 204 NO_CONTENT - Nenhum cliente cadastrado
     */
    // Rota para listar todos clientes com paginacao
    @GetMapping("/paginated")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findAllPaged(
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable, Authentication authentication) {

        // Chamar serviço para buscar com paginação
        Page<ClienteListResponseDTO> result = clienteService.findAllPaged(pageable, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                result
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Buscar cliente pelo CPF/CNPJ.
     *
     * @param cpfCnpj CPF ou CNPJ do cliente (será normalizado no serviço)
     * @return DTO do cliente encontrado
     * @status 200 OK - Cliente encontrado
     * @status 404 NOT_FOUND - Cliente não existe
     */
    // Rota para buscar um cliente por CPF/CNPJ
    @GetMapping("/findByCpfCnpj/{cpfCnpj}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByCpfCnpj(
            @PathVariable String cpfCnpj, Authentication authentication) {

        // Chamar serviço para buscar
        ClienteResponseDTO result = clienteService.findByCpfCnpj(cpfCnpj, authentication);

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Cliente encontrado com sucesso",
                result
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Buscar clientes pelo tipo de pessoa.
     *
     * @param tipoPessoa para buscar clientes por tipo de pessoa ("FISICA", "JURIDICA", ...)
     * @return Lista de DTOs de clientes
     * @status 200 OK - Clientes encontrados
     * @status 204 NO_CONTENT - Nenhum cliente encontrado
     */
    // Rota para listar clientes por tipo de pessoa
    @GetMapping("/findByTipoPessoa/{tipoPessoa}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<ResponseEntity> findByTipoPessoa(@PathVariable String tipoPessoa, Authentication authentication) {

        // Chamar serviço para buscar
        List<ClienteListResponseDTO> result = clienteService.findByTipoPessoa(tipoPessoa, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                result
        );

        return new ResponseEntity(response, HttpStatus.OK);
    }

    /**
     * Buscar clientes por email (contém, case insensitive).
     *
     * @param email Email ou parte do email do cliente
     * @return Lista de DTOs de clientes
     * @status 200 OK - Clientes encontrados
     * @status 204 NO_CONTENT - Nenhum cliente encontrado
     */
    // Rota para buscar clientes por email (contendo, case insensitive)
    @GetMapping("/findByEmail/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByEmail(@PathVariable String email, Authentication authentication) {

        // Chamar serviço para buscar
        List<ClienteListResponseDTO> result = clienteService.findByEmailContainingIgnoreCase(email, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                result
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Buscar clientes pelo telefone.
     *
     * @param telefone para buscar clientes por telefone (contendo, case insensitive)
     * @return Lista de DTOs de clientes
     * @status 200 OK - Clientes encontrados
     * @status 204 NO_CONTENT - Nenhum cliente encontrado
     */
    // Rota para buscar clientes por telefone (contendo, case insensitive)
    @GetMapping("/findByTelefone/{telefone}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<ResponseEntity> findByTelefoneContainingIgnoreCase(@PathVariable String telefone, Authentication authentication) {

        // Chamar serviço para buscar
        List<ClienteListResponseDTO> result = clienteService.findByTelefoneContainingIgnoreCase(telefone, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                result
        );

        return new ResponseEntity(response, HttpStatus.OK);
    }

    /**
     * Buscar clientes pelo status (ativo/inativo).
     *
     * @param cidade true para ativos, false para inativos
     * @return Lista de DTOs de clientes
     * @status 200 OK - Clientes encontrados
     * @status 204 NO_CONTENT - Nenhum cliente encontrado
     */
    // Rota para buscar clientes por cidade (contendo, case insensitive)
    @GetMapping("/findByCidade/{cidade}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<ResponseEntity> findByCidadeContainingIgnoreCase(@PathVariable String cidade, Authentication authentication) {

        // Chamar serviço para buscar
        List<ClienteListResponseDTO> result = clienteService.findByCidadeContainingIgnoreCase(cidade, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                result
        );

        return new ResponseEntity(response, HttpStatus.OK);
    }

    /**
     * Buscar clientes pelo UF do estado
     *
     * @param estadoUF para buscar clientes por UF do estado (ex: "SP", "RJ", "MG")
     * @return Lista de DTOs de clientes
     * @status 200 OK - Clientes encontrados
     * @status 204 NO_CONTENT - Nenhum cliente encontrado
     */
    // Rota para buscar clientes por estado
    @GetMapping("/findByEstadoUF/{estadoUF}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<ResponseEntity> findByEstadoUF(@PathVariable String estadoUF, Authentication authentication) {

        // Chamar serviço para buscar
        List<ClienteListResponseDTO> result = clienteService.findByEstadoUF(estadoUF, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                result
        );

        return new ResponseEntity(response, HttpStatus.OK);
    }

    /**
     * Buscar clientes pelo status (ativo/inativo).
     *
     * @param cep para buscar clientes por CEP (contendo, case insensitive)
     * @return Lista de DTOs de clientes
     * @status 200 OK - Clientes encontrados
     * @status 204 NO_CONTENT - Nenhum cliente encontrado
     */
    // Buscar clientes por CEP
    @GetMapping("/findByCEP/{cep}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<ResponseEntity> findByCep(@PathVariable String cep, Authentication authentication) {

        // Chamar serviço para buscar
        List<ClienteListResponseDTO> result = clienteService.findByCep(cep, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                result
        );

        return new ResponseEntity(response, HttpStatus.OK);
    }

    /**
     * Buscar clientes pelo status (ativo/inativo).
     *
     * @param isAtivo true para ativos, false para inativos
     * @return Lista de DTOs de clientes
     * @status 200 OK - Clientes encontrados
     * @status 204 NO_CONTENT - Nenhum cliente encontrado
     */
    // Rota para buscar clientes por status (Ativo/Inativo)
    @GetMapping("/findByAtivoInativo/{isAtivo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByAtivo(
            @PathVariable boolean isAtivo, Authentication authentication) {

        // Chamar serviço para buscar
        List<ClienteListResponseDTO> result = clienteService.findByIsAtivo(isAtivo, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                result
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Buscar clientes pela data de criacao.
     * @return Lista de DTOs de clientes
     * @status 200 OK - Clientes encontrados
     * @status 204 NO_CONTENT - Nenhum cliente encontrado
     */
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
        List<ClienteListResponseDTO> clientes = clienteService.findByCreatedAtBetween(dataInicioTime, dataFimTime, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (clientes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                clientes
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Buscar clientes por nome (contém, case insensitive).
     *
     * @param nome Nome ou parte do nome do cliente
     * @return Lista de DTOs de clientes
     * @status 200 OK - Clientes encontrados
     * @status 204 NO_CONTENT - Nenhum cliente encontrado
     */
    // Rota para buscar clientes por nome (contendo, case insensitive)
    @GetMapping("/findByNome/{nome}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<SuccessResponseDTO> findByNome(@PathVariable String nome, Authentication authentication) {

        // Chamar serviço para buscar
        List<ClienteListResponseDTO> result = clienteService.findByNomeContainingIgnoreCase(nome, authentication);

        // Se lista vazia, retornar 204 (No Content)
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Montar resposta padrão
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Clientes encontrados com sucesso",
                result
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
