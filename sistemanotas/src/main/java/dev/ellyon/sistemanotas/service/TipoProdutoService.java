package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoRequestDTO;
import dev.ellyon.sistemanotas.dto.tipoProduto.TipoProdutoResponseDTO;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

public interface TipoProdutoService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete - Soft Delete - Activate
    TipoProdutoResponseDTO create(TipoProdutoRequestDTO dto, Authentication authentication); // Criar um novo tipo de produto
    void delete(Long id, Authentication authentication); // Deletar um tipo de produto por ID
    TipoProdutoResponseDTO update(Long id, TipoProdutoRequestDTO dto, Authentication authentication); // Atualizar um tipo de produto por ID
    void softDelete(Long id, Authentication authentication); // Desativar um tipo de produto por ID
    void activate(Long id, Authentication authentication); // Ativar um tipo de produto por ID

    // Buscas
    TipoProdutoResponseDTO findById(Long id, Authentication authentication); // Buscar um tipo de produto por ID
    List<TipoProdutoResponseDTO> findAll(Authentication authentication); // Buscar todos os tipos de produtos
    List<TipoProdutoResponseDTO> findByAtivoInativo(Boolean ativo, Authentication authentication); // Buscar tipos de produtos por status (ativo/inativo)
    List<TipoProdutoResponseDTO> findByNomeContainingIgnoreCase(String nome, Authentication authentication); // Buscar tipos de produtos por nome (contendo, case insensitive)
    List<TipoProdutoResponseDTO> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fim, Authentication authentication); // Buscar tipos de produtos criados entre duas datas
}
