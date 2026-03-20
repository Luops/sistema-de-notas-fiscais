package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.empresa.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

public interface EmpresaService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete - Soft Delete - Activate
    EmpresaResponseDTO create(EmpresaRequestDTO dto); // Criar uma nova empresa
    void delete(Long id, Authentication authentication); // Deletar uma empresa por ID
    EmpresaResponseDTO update(Long id, EmpresaRequestDTO dto, Authentication authentication); // Atualizar uma empresa por ID
    void softDelete(Long id, Authentication authentication); // Desativar uma empresa por ID
    void activate(Long id, Authentication authentication); // Ativar uma empresa por ID
    CertificadoResponseDTO uploadCertificado(Long empresaId, CertificadoUploadDTO dto, Authentication authentication); // Fazer upload do certificado para gerar nota fiscal
    CertificadoResponseDTO buscarCertificado(Long empresaId, Authentication authentication); // Buscar certificado
    public void removerCertificado(Long empresaId, Authentication authentication); // Remover certificado da empresa

    // Buscas
    EmpresaResponseDTO findById(Long id, Authentication authentication); // Buscar empresa por ID
    //List<EmpresaListResponseDTO> findAll(Authentication authentication); // Buscar todas as empresas
    //Page<EmpresaListResponseDTO> findAllPaged(Pageable pageable, Authentication authentication); // Buscar todas as empresas com paginação
    EmpresaResponseDTO findByCnpj(String cnpj, Authentication authentication); // Buscar empresa por CNPJ
    List<EmpresaListResponseDTO> findByRazaoSocialContainingIgnoreCase(String razaoSocial, Authentication authentication); // Buscar empresas por razão social contendo um termo
    List<EmpresaListResponseDTO> findByNomeFantasiaContainingIgnoreCase(String nomeFantasia, Authentication authentication); // Buscar empresas pelo nome fantasia contendo um termo
    List<EmpresaListResponseDTO> findByEmailContainingIgnoreCase(String email, Authentication authentication); // Buscar empresas por email
    List<EmpresaListResponseDTO> findByTelefoneContaining(String telefone, Authentication authentication) ; // Buscar empresas por telefone
    List<EmpresaListResponseDTO> findByCidadeIgnoreCase(String cidade, Authentication authentication); // Buscar empresas por cidade
    List<EmpresaListResponseDTO> findByEstadoUFIgnoreCase(String estadoUF, Authentication authentication); // Buscar empresas por estado (UF)
    List<EmpresaListResponseDTO> findByCep(String cep, Authentication authentication); // Buscar empresas por CEP
    List<EmpresaListResponseDTO> findByIsAtivo(Boolean ativo, Authentication authentication) ; // Buscar empresas por status de ativo/inativo
    List<EmpresaListResponseDTO> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fim, Authentication authentication); // Buscar empresas criadas entre duas datas


}
