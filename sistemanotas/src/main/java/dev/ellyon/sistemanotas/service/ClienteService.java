package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.cliente.ClienteListResponseDTO;
import dev.ellyon.sistemanotas.dto.cliente.ClienteRequestDTO;
import dev.ellyon.sistemanotas.dto.cliente.ClienteResponseDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoListResponseDTO;
import dev.ellyon.sistemanotas.dto.produto.ProdutoResponseDTO;
import dev.ellyon.sistemanotas.model.enums.TipoPessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ClienteService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete - Soft Delete - Activate
    ClienteResponseDTO create(ClienteRequestDTO dto); // Criar um novo cliente
    void delete(Long id); // Deletar um cliente por ID
    ClienteResponseDTO update(Long id, ClienteRequestDTO dto); // Atualizar um cliente por ID
    void softDelete(Long id); // Desativar um cliente por ID
    void activate(Long id); // Ativar um cliente por ID

    // Buscas
    ClienteResponseDTO findById(Long id); // Buscar um cliente por ID
    List<ClienteListResponseDTO> findAll(); // Buscar todos os cliente
    Page<ClienteListResponseDTO> findAllPaged(Pageable pageable); // Buscar todos os cliente com paginação
    ClienteResponseDTO findByCpfCnpj(String cpfCnpj); // Buscar cliente por CPF/CNPJ
    List<ClienteListResponseDTO> findByTipoPessoa(String tipoPessoa); // Buscar cliente por tipo (Nome)
    List<ClienteListResponseDTO> findByEmailContainingIgnoreCase(String email); // Buscar clientes por email (contendo, case insensitive)
    List<ClienteListResponseDTO> findByTelefoneContainingIgnoreCase(String telefone); // Buscar clientes por telefone (contendo, case insensitive)
    List<ClienteListResponseDTO> findByCidadeContainingIgnoreCase(String cidade); // Buscar clientes por cidade (contendo, case insensitive)
    List<ClienteListResponseDTO> findByEstadoUF(String estadoUF); // Buscar clientes por estado
    List<ClienteListResponseDTO> findByCep(String cep); // Buscar clientes por CEP
    List<ClienteListResponseDTO> findByIsAtivo(Boolean ativo); // Buscar clientes por status (Ativo/Inativo)
    List<ClienteListResponseDTO> findByNomeContainingIgnoreCase(String nome); // Buscar clientes por nome (contendo, case insensitive)
    List<ClienteListResponseDTO> findByCreatedAtBetween(LocalDateTime dataInicio, LocalDateTime dataFim); // Buscar clientes por faixa de data de criação

}
