package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.empresa.EmpresaListResponseDTO;
import dev.ellyon.sistemanotas.dto.empresa.EmpresaRequestDTO;
import dev.ellyon.sistemanotas.dto.empresa.EmpresaResponseDTO;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.EntityNotFoundException;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.Cliente;
import dev.ellyon.sistemanotas.model.Empresa;
import dev.ellyon.sistemanotas.repository.EmpresaRepository;
import dev.ellyon.sistemanotas.service.EmpresaService;
import dev.ellyon.sistemanotas.service.mapper.EmpresaMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // Deletar uma empresa
    @Override
    public void delete(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa com ID " + id + " não encontrada."));

        if (empresa.getId() == null) {
            throw new BusinessException("Empresa não existe!");
        }

        empresaRepository.deleteById(id);
    }

    // Atualizar uma empresa
    @Override
    public EmpresaResponseDTO update(Long id, EmpresaRequestDTO dto) {
        // Declaracao dos errors
        Map<String, String> errors = new HashMap<>();

        // Verifica se a empresa existe
        Empresa empresaExistente = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa", id));

        /*
         * NORMALIZAÇÃO DE DADOS (REMOVER FORMATAÇÃO)
         * */
        // Remove pontos, traços e barras do CNPJ
        String cnpjLimpo = dto.getCnpj() != null
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

        // Valida CNPJ
        if (cnpjLimpo.isEmpty()) {
            errors.put("cnpj", "CNPJ é obrigatório");
        } else if (cnpjLimpo.length() != 14) {
            errors.put("cnpj", "CNPJ deve ter exatamente 14 dígitos");
        } else {
            // Busca empresa com esse CNPJ
            Optional<Empresa> empresaComMesmoCnpj = empresaRepository.findByCnpj(cnpjLimpo);

            // Se encontrou uma empresa com esse CNPJ
            if (empresaComMesmoCnpj.isPresent()) {
                // Verifica se NÃO é a mesma empresa sendo editada
                if (!empresaComMesmoCnpj.get().getId().equals(id)) {
                    errors.put("cnpj", "CNPJ já cadastrado no sistema");
                }
            }
        }

        // Valida email
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            errors.put("email", "Email é obrigatório");
        } else if (!dto.getEmail().contains("@")) {
            errors.put("email", "Email inválido");
        } else {
            String emailNormalizado = dto.getEmail().toLowerCase().trim();

            // Busca empresa com esse email
            Optional<Empresa> empresaComMesmoEmail = empresaRepository.findByEmail(emailNormalizado);

            // Se encontrou uma empresa com esse email
            if (empresaComMesmoEmail.isPresent()) {
                // Verifica se NÃO é a mesma empresa sendo editada
                if (!empresaComMesmoEmail.get().getId().equals(id)) {
                    errors.put("email", "Email já cadastrado no sistema");
                }
            }
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        String emailNormalizado = dto.getEmail().toLowerCase().trim();
        String estadoUFNormalizado = dto.getEstadoUF().toUpperCase().trim();

        /*
         * ATUALIZAR EMPRESA
         * */

        empresaExistente.setRazaoSocial(dto.getRazaoSocial().trim());
        empresaExistente.setNomeFantasia(dto.getNomeFantasia().trim());
        empresaExistente.setCnpj(cnpjLimpo);
        empresaExistente.setInscricaoEstadual(dto.getInscricaoEstadual().trim());
        empresaExistente.setEnderecoCompleto(dto.getEnderecoCompleto().trim());
        empresaExistente.setCidade(dto.getCidade().trim());
        empresaExistente.setEstadoUF(estadoUFNormalizado);
        empresaExistente.setCep(cepLimpo);
        empresaExistente.setTelefone(telefoneLimpo);
        empresaExistente.setEmail(emailNormalizado);
        empresaExistente.setLogoUrl(dto.getLogoUrl());
        if (dto.getAtivo() != null) {
            empresaExistente.setAtivo(dto.getAtivo());
        }

        /* Salva a empresa no banco de dados */
        Empresa empresaAtualizada = empresaRepository.save(empresaExistente);
        return empresaMapper.toResponseDTO(empresaAtualizada);
    }

    // Desativar uma empresa
    @Override
    public void softDelete(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa", id));

        if (empresa.getId() == null) {
            throw new BusinessException("Empresa não existe!");
        }

        empresa.setAtivo(false);
        empresaRepository.save(empresa);
    }

    // Ativar uma empresa
    @Override
    public void activate(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa", id));

        if (empresa.getId() == null) {
            throw new BusinessException("Empresa não existe!");
        }

        empresa.setAtivo(true);
        empresaRepository.save(empresa);
    }

    // Buscar empresa por ID
    @Override
    public EmpresaResponseDTO findById(Long id) {
        Empresa empresa = empresaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Empresa", id));
        return empresaMapper.toResponseDTO(empresa);
    }

    // Buscar todas as empresas
    @Override
    public List<EmpresaListResponseDTO> findAll() {
        List<Empresa> empresas = empresaRepository.findAll();
        return empresas.stream().map(empresaMapper::toListResponseDTO).collect(Collectors.toList());
    }

    @Override
    public Page<EmpresaListResponseDTO> findAllPaged(Pageable pageable) {
        return null;
    }

    @Override
    public EmpresaResponseDTO findByCnpj(String cnpj) {
        return null;
    }

    @Override
    public List<EmpresaListResponseDTO> findByRazaoSocialContaining(String razaoSocial) {
        return List.of();
    }

    @Override
    public List<EmpresaListResponseDTO> findByNomeFantasiaContaining(String nomeFantasia) {
        return List.of();
    }

    @Override
    public List<EmpresaListResponseDTO> findByEmail(String email) {
        return List.of();
    }

    @Override
    public List<EmpresaListResponseDTO> findByTelefone(String telefone) {
        return List.of();
    }

    @Override
    public List<EmpresaListResponseDTO> findByCidade(String cidade) {
        return List.of();
    }

    @Override
    public List<EmpresaListResponseDTO> findByEstadoUF(String estadoUF) {
        return List.of();
    }

    @Override
    public List<EmpresaListResponseDTO> findByCep(String cep) {
        return List.of();
    }

    @Override
    public List<EmpresaListResponseDTO> findByAtivo(Boolean ativo) {
        return List.of();
    }

    @Override
    public List<EmpresaListResponseDTO> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fim) {
        return List.of();
    }
}
