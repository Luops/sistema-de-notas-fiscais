package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.empresa.EmpresaListResponseDTO;
import dev.ellyon.sistemanotas.dto.empresa.EmpresaRequestDTO;
import dev.ellyon.sistemanotas.dto.empresa.EmpresaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface EmpresaService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete - Soft Delete - Activate
    EmpresaResponseDTO create(EmpresaRequestDTO dto); // Criar uma nova empresa
    void delete(Long id); // Deletar uma empresa por ID
    EmpresaResponseDTO update(Long id, EmpresaRequestDTO dto); // Atualizar uma empresa por ID
    void softDelete(Long id); // Desativar uma empresa por ID
    void activate(Long id); // Ativar uma empresa por ID

    // Buscas
    EmpresaResponseDTO findById(Long id); // Buscar empresa por ID
    List<EmpresaListResponseDTO> findAll(); // Buscar todas as empresas
    Page<EmpresaListResponseDTO> findAllPaged(Pageable pageable); // Buscar todas as empresas com paginação
    EmpresaResponseDTO findByCnpj(String cnpj); // Buscar empresa por CNPJ
    List<EmpresaListResponseDTO> findByRazaoSocialContainingIgnoreCase(String razaoSocial); // Buscar empresas por razão social contendo um termo
    List<EmpresaListResponseDTO> findByNomeFantasiaContainingIgnoreCase(String nomeFantasia); // Buscar empresas pelo nome fantasia contendo um termo
    List<EmpresaListResponseDTO> findByEmailContainingIgnoreCase(String email); // Buscar empresas por email
    List<EmpresaListResponseDTO> findByTelefoneContaining(String telefone) ; // Buscar empresas por telefone
    List<EmpresaListResponseDTO> findByCidadeIgnoreCase(String cidade); // Buscar empresas por cidade
    List<EmpresaListResponseDTO> findByEstadoUFIgnoreCase(String estadoUF); // Buscar empresas por estado (UF)
    List<EmpresaListResponseDTO> findByCep(String cep); // Buscar empresas por CEP
    List<EmpresaListResponseDTO> findByIsAtivo(Boolean ativo) ; // Buscar empresas por status de ativo/inativo
    List<EmpresaListResponseDTO> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fim); // Buscar empresas criadas entre duas datas


}
