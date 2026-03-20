package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.cliente.ClienteListResponseDTO;
import dev.ellyon.sistemanotas.dto.cliente.ClienteRequestDTO;
import dev.ellyon.sistemanotas.dto.cliente.ClienteResponseDTO;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ClienteService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete - Soft Delete - Activate
    ClienteResponseDTO create(ClienteRequestDTO dto, Authentication authentication); // Criar um novo cliente
    void delete(Long id, Authentication authentication); // Deletar um cliente por ID
    ClienteResponseDTO update(Long id, ClienteRequestDTO dto, Authentication authentication); // Atualizar um cliente por ID
    void softDelete(Long id, Authentication authentication); // Desativar um cliente por ID
    void activate(Long id, Authentication authentication); // Ativar um cliente por ID

    // Buscas
    ClienteResponseDTO findById(Long id, Authentication authentication); // Buscar um cliente por ID
    List<ClienteListResponseDTO> findAll(Authentication authentication); // Buscar todos os cliente
    Page<ClienteListResponseDTO> findAllPaged(Pageable pageable, Authentication authentication); // Buscar todos os cliente com paginação
    ClienteResponseDTO findByCpfCnpj(String cpfCnpj, Authentication authentication); // Buscar cliente por CPF/CNPJ
    List<ClienteListResponseDTO> findByTipoPessoa(String tipoPessoa, Authentication authentication); // Buscar cliente por tipo (Nome)
    List<ClienteListResponseDTO> findByEmailContainingIgnoreCase(String email, Authentication authentication); // Buscar clientes por email (contendo, case insensitive)
    List<ClienteListResponseDTO> findByTelefoneContainingIgnoreCase(String telefone, Authentication authentication); // Buscar clientes por telefone (contendo, case insensitive)
    List<ClienteListResponseDTO> findByCidadeContainingIgnoreCase(String cidade, Authentication authentication); // Buscar clientes por cidade (contendo, case insensitive)
    List<ClienteListResponseDTO> findByEstadoUF(String estadoUF, Authentication authentication); // Buscar clientes por estado
    List<ClienteListResponseDTO> findByCep(String cep, Authentication authentication); // Buscar clientes por CEP
    List<ClienteListResponseDTO> findByIsAtivo(Boolean isAtivo, Authentication authentication); // Buscar clientes por status (Ativo/Inativo)
    List<ClienteListResponseDTO> findByNomeContainingIgnoreCase(String nome, Authentication authentication); // Buscar clientes por nome (contendo, case insensitive)
    List<ClienteListResponseDTO> findByCreatedAtBetween(LocalDateTime dataInicio, LocalDateTime dataFim, Authentication authentication); // Buscar clientes por faixa de data de criação

}
