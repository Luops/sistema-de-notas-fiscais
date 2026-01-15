package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.empresa.EmpresaRequestDTO;
import dev.ellyon.sistemanotas.dto.empresa.EmpresaResponseDTO;

public interface EmpresaService {
    // "Contratos" - O que esse serviço deve oferecer
    // Create - Update - Delete - Soft Delete - Activate
    EmpresaResponseDTO create(EmpresaRequestDTO dto); // Criar uma nova empresa
    void delete(Long id); // Deletar uma empresa por ID
    EmpresaResponseDTO update(Long id, EmpresaRequestDTO dto); // Atualizar uma empresa por ID
    void softDelete(Long id); // Desativar uma empresa por ID
    void activate(Long id); // Ativar uma empresa por ID
}
