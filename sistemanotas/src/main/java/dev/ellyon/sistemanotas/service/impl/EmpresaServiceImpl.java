package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.empresa.EmpresaRequestDTO;
import dev.ellyon.sistemanotas.dto.empresa.EmpresaResponseDTO;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.Empresa;
import dev.ellyon.sistemanotas.repository.EmpresaRepository;
import dev.ellyon.sistemanotas.service.EmpresaService;
import dev.ellyon.sistemanotas.service.mapper.EmpresaMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class EmpresaServiceImpl implements EmpresaService {
    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;
    public EmpresaServiceImpl(EmpresaRepository empresaRepository, EmpresaMapper empresaMapper) {
        this.empresaRepository = empresaRepository;
        this.empresaMapper = empresaMapper;
    }

    // Criar uma empresa
    @Override
    public EmpresaResponseDTO create(EmpresaRequestDTO dto) {
        // Declaracao dos errors
        Map<String, String> errors = new HashMap<>();

        /*
         * NORMALIZAÇÃO DE DADOS (REMOVER FORMATAÇÃO)
         * */
        // Remove pontos, traços e barras do CNPJ
        String cpfCnpjLimpo = dto.getCnpj() != null
                ? dto.getCnpj().replaceAll("[^0-9]", "")
                : "";

        // Remove traço do CEP
        String cepLimpo = dto.getCep() != null
                ? dto.getCep().replaceAll("[^0-9]", "")
                : "";

        // Remove caracteres especiais do telefone (mantém apenas números)
        String telefoneLimpo = dto.getTelefone() != null
                ? dto.getTelefone().replaceAll("[^0-9]", "")
                : "";

        /*
         * VALIDAÇÕES
         * */
        if(empresaRepository.existsByCnpj(cpfCnpjLimpo)){
            errors.put("cnpj", "CNPJ já cadastrado no sistema.");
        }

        if(empresaRepository.existsByEmail(dto.getEmail())){
            errors.put("email", "Email já cadastrado no sistema.");
        }

        if(!dto.getInscricaoEstadual().isEmpty()){
            if(dto.getInscricaoEstadual().length() < 8 || dto.getInscricaoEstadual().length() > 20){
                errors.put("inscricaoEstadual", "Inscrição Estadual deve ter entre 8 e 20 caracteres.");
            }
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        String emailNormalizado = dto.getEmail().toLowerCase().trim();
        String estadoUFNormalizado = dto.getEstadoUF().toUpperCase().trim();

        /*
        * CRIAÇÃO DA EMPRESA
        * */

        Empresa empresa = new Empresa();
        empresa.setRazaoSocial(dto.getRazaoSocial());
        empresa.setNomeFantasia(dto.getNomeFantasia());
        empresa.setCnpj(cpfCnpjLimpo);
        empresa.setInscricaoEstadual(dto.getInscricaoEstadual());
        empresa.setEnderecoCompleto(dto.getEnderecoCompleto());
        empresa.setCidade(dto.getCidade());
        empresa.setEstadoUF(estadoUFNormalizado);
        empresa.setCep(cepLimpo);
        empresa.setTelefone(telefoneLimpo);
        empresa.setEmail(emailNormalizado);
        empresa.setLogoUrl(dto.getLogoUrl());
        empresa.setAtivo(true); // Define como ativo por padrão

        /* Salva a empresa no banco de dados */
        Empresa empresaSalva = empresaRepository.save(empresa);
        return empresaMapper.toResponseDTO(empresaSalva);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public EmpresaResponseDTO update(Long id, EmpresaRequestDTO dto) {
        return null;
    }

    @Override
    public void softDelete(Long id) {

    }

    @Override
    public void activate(Long id) {

    }
}
