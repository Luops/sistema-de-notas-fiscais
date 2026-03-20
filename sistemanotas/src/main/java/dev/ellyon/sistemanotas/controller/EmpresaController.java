package dev.ellyon.sistemanotas.controller;

import dev.ellyon.sistemanotas.dto.empresa.*;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<SuccessResponseDTO> create(@RequestBody @Valid EmpresaRequestDTO dto){
        EmpresaResponseDTO response = empresaService.create(dto);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Empresa criada com sucesso",
                response
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
    }

    // Rota para atualizar uma empresa
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> update(@PathVariable Long id, @RequestBody @Valid EmpresaRequestDTO dto, Authentication authentication) {
        EmpresaResponseDTO response = empresaService.update(id, dto, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresa atualizada com sucesso",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para deletar uma empresa
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> delete(@PathVariable Long id, Authentication authentication) {
        empresaService.delete(id, authentication);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresa deletada com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }

    // Rota para desativar uma empresa
    @PutMapping("/update/softDelete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> softDelete(@PathVariable Long id, Authentication authentication) {
        empresaService.softDelete(id, authentication);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresa desativada com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }

    // Rota para ativar uma empresa
    @PutMapping("/update/activate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> activate(@PathVariable Long id, Authentication authentication) {
        empresaService.activate(id, authentication);

        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresa ativada com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }

    // Rota para fazer upload do certificado
    @PostMapping(value = "/{empresaId}/certificado/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> upload(
            @PathVariable Long empresaId,
            @Valid @ModelAttribute CertificadoUploadDTO dto, Authentication authentication) {

        try {
            CertificadoResponseDTO response = empresaService.uploadCertificado(empresaId, dto, authentication);

            SuccessResponseDTO successResponse = new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    "Certificado digital configurado com sucesso!",
                    response
            );
            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            SuccessResponseDTO errorResponse = new SuccessResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Rota para buscar informações do certificado
    @GetMapping("/{empresaId}/certificado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> buscar(@PathVariable Long empresaId, Authentication authentication) {
        try {
            CertificadoResponseDTO response = empresaService.buscarCertificado(empresaId, authentication);

            SuccessResponseDTO successResponse = new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    "Certificado encontrado",
                    response
            );
            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            SuccessResponseDTO errorResponse = new SuccessResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // Rota para remover certificado
    @DeleteMapping("/{empresaId}/certificado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> remover(@PathVariable Long empresaId, Authentication authentication) {
        try {
            empresaService.removerCertificado(empresaId, authentication);

            SuccessResponseDTO response = new SuccessResponseDTO(
                    HttpStatus.OK.value(),
                    "Certificado removido com sucesso",
                    null
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            SuccessResponseDTO errorResponse = new SuccessResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Rota para buscar uma empresa por ID
    @GetMapping("/findById/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> findById(@PathVariable Long id, Authentication authentication) {
        EmpresaResponseDTO response = empresaService.findById(id, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresa encontrada",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar todas as empresas
    /*@GetMapping("/findAll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> findAll(Authentication authentication) {
        List<EmpresaListResponseDTO> response = empresaService.findAll(authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresas encontradas",
                response
        );

        return ResponseEntity.ok(successResponse);
    }*/


    // Rota para buscar empresas com paginação
    /*@GetMapping("/paginated")
    public ResponseEntity<Page<EmpresaListResponseDTO>> findAllPaged(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<EmpresaListResponseDTO> empresasPage = empresaService.findAllPaged(pageable);
        return ResponseEntity.ok(empresasPage);
    }*/

    // Rota para buscar empresa por CNPJ
    @GetMapping("/findByCnpj/{cnpj}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> findByCnpj(@PathVariable String cnpj, Authentication authentication) {
        EmpresaResponseDTO response = empresaService.findByCnpj(cnpj, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresas encontradas",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar empresas por razão social contendo um termo
    @GetMapping("/findByRazaoSocial")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> findByRazaoSocialContainingIgnoreCase(
            @RequestParam(name = "razaoSocial")
            @NotBlank(message = "Razão Social não pode ser vazia") String razaoSocial, Authentication authentication) {
        List<EmpresaListResponseDTO> response = empresaService.findByRazaoSocialContainingIgnoreCase(razaoSocial, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresas encontradas",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar empresas pelo nome fantasia contendo um termo
    @GetMapping("/findByNomeFantasia")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> findByNomeFantasiaContainingIgnoreCase(
            @RequestParam(name = "nomeFantasia")
            @NotBlank(message = "Nome Fantasia não pode ser vazio") String nomeFantasia, Authentication authentication) {
        List<EmpresaListResponseDTO> response = empresaService.findByNomeFantasiaContainingIgnoreCase(nomeFantasia, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresas encontradas",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar empresas por email
    @GetMapping("/findByEmail/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> findByEmailContainingIgnoreCase(
            @PathVariable String email, Authentication authentication) {
        List<EmpresaListResponseDTO> response = empresaService.findByEmailContainingIgnoreCase(email, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresas encontradas",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar empresas por telefone
    @GetMapping("/findByTelefone/{telefone}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> findByTelefoneContaining(
            @PathVariable String telefone, Authentication authentication) {
        List<EmpresaListResponseDTO> response = empresaService.findByTelefoneContaining(telefone, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresas encontradas",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar empresas por cidade
    @GetMapping("/findByCidade")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> findByCidadeIgnoreCase(
            @RequestParam(name = "cidade")
            @NotBlank(message = "Cidade não pode ser vazia") String cidade, Authentication authentication) {
        List<EmpresaListResponseDTO> response = empresaService.findByCidadeIgnoreCase(cidade, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresas encontradas",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar empresas por estado (UF)
    @GetMapping("/findByEstadoUF/{estadoUF}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> findByEstadoUFIgnoreCase(
            @PathVariable String estadoUF, Authentication authentication) {
        List<EmpresaListResponseDTO> response = empresaService.findByEstadoUFIgnoreCase(estadoUF, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresas encontradas",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar empresas por CEP
    @GetMapping("/findByCep/{cep}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> findByCep(
            @PathVariable String cep, Authentication authentication) {
        List<EmpresaListResponseDTO> response = empresaService.findByCep(cep, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresas encontradas",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar empresas por status de ativo/inativo
    @GetMapping("/findByAtivoInativo/{ativo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> findByEmpresaAtivoInativo(
            @PathVariable Boolean ativo, Authentication authentication) {
        List<EmpresaListResponseDTO> response = empresaService.findByIsAtivo(ativo, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresas encontradas",
                response
        );

        return ResponseEntity.ok(successResponse);
    }

    // Rota para buscar empresas criadas entre duas datas
    @GetMapping("/findByCreatedAtBetween")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> findByCreatedAtBetween(
            @RequestParam(name = "dataInicio")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(name = "dataFim")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim, Authentication authentication) {
        // Converter LocalDate para LocalDateTime no início do dia e fim do dia
        LocalDateTime inicioDateTime = dataInicio.atStartOfDay(); // 00:00:00
        LocalDateTime fimDateTime = dataFim.atTime(23, 59, 59); // 23:59:59

        List<EmpresaListResponseDTO> response = empresaService.findByCreatedAtBetween(inicioDateTime, fimDateTime, authentication);

        SuccessResponseDTO successResponse = new SuccessResponseDTO(
                HttpStatus.OK.value(),
                "Empresas encontradas",
                response
        );

        return ResponseEntity.ok(successResponse);
    }
}
