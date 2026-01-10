package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.cliente.ClienteRequestDTO;
import dev.ellyon.sistemanotas.dto.cliente.ClienteResponseDTO;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.EntityNotFoundException;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.Cliente;
import dev.ellyon.sistemanotas.model.enums.TipoPessoa;
import dev.ellyon.sistemanotas.repository.ClienteRepository;
import dev.ellyon.sistemanotas.service.ClienteService;
import dev.ellyon.sistemanotas.service.mapper.ClienteMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteServiceImpl(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    // Criar um novo cliente
    @Override
    public ClienteResponseDTO create(ClienteRequestDTO dto) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        /*
         * NORMALIZAÇÃO DE DADOS (REMOVER FORMATAÇÃO)
         * */
        // Remove pontos, traços e barras do CPF/CNPJ
        String cpfCnpjLimpo = dto.getCpfCnpj() != null
                ? dto.getCpfCnpj().replaceAll("[^0-9]", "")
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
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            errors.put("nome", "O nome do cliente é obrigatório");
        }

        if (dto.getTipoPessoa() == null) {
            dto.setTipoPessoa(TipoPessoa.CONSUMIDOR_FINAL);
        }

        // Validação de CPF/CNPJ
        if (cpfCnpjLimpo.isEmpty()) {
            errors.put("cpfCnpj", "O CPF/CNPJ do cliente é obrigatório");
        } else {
            // Valida tamanho: CPF = 11 dígitos, CNPJ = 14 dígitos
            if (cpfCnpjLimpo.length() < 11 && cpfCnpjLimpo.length() > 14) {
                errors.put("cpfCnpj", "CPF deve ter 11 dígitos ou CNPJ deve ter 14 dígitos");
            } else if (clienteRepository.existsByCpfCnpj(cpfCnpjLimpo)) {
                errors.put("cpfCnpj", "O CPF/CNPJ já está cadastrado");
            }
        }

        // Validação de Email
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            errors.put("email", "O email do cliente é obrigatório");
        } else if (!dto.getEmail().contains("@")) {
            errors.put("email", "O email do cliente é inválido");
        } else if (clienteRepository.existsByEmail(dto.getEmail().toLowerCase().trim())) {
            errors.put("email", "Já existe um cliente com este email");
        }

        // Validação de Telefone
        if (telefoneLimpo.isEmpty()) {
            errors.put("telefone", "O telefone do cliente é obrigatório");
        } else if (telefoneLimpo.length() < 10 && telefoneLimpo.length() > 15) {
            errors.put("telefone", "Telefone deve ter 10 ou 11 dígitos");
        }

        // Validação de Endereço
        if (dto.getEnderecoCompleto() == null || dto.getEnderecoCompleto().isBlank()) {
            errors.put("endereco", "O endereço do cliente é obrigatório");
        }

        // Validação de Cidade
        if (dto.getCidade() == null || dto.getCidade().isBlank()) {
            errors.put("cidade", "A cidade do cliente é obrigatória");
        }

        // Validação de Estado
        if (dto.getEstadoUF() == null || dto.getEstadoUF().isBlank()) {
            errors.put("estado", "O estado do cliente é obrigatório");
        } else if (dto.getEstadoUF().trim().length() != 2) {
            errors.put("estado", "Estado deve ter 2 caracteres (UF)");
        }

        // Validação de CEP
        if (cepLimpo.isEmpty()) {
            errors.put("cep", "O CEP do cliente é obrigatório");
        } else if (cepLimpo.length() < 8 && cepLimpo.length() > 9) {
            errors.put("cep", "CEP deve ter entre 8 e 9 dígitos");
        }

        // Validação de Bairro
        if (dto.getBairro() == null || dto.getBairro().isBlank()) {
            errors.put("bairro", "O bairro do cliente é obrigatório");
        } else if (dto.getBairro().trim().length() < 3 && dto.getBairro().trim().length() > 255) {
            errors.put("bairro", "O bairro do cliente deve ter entre 3 e 255 caracteres");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        /*
         * NORMALIZAÇÃO FINAL DOS DADOS
         * */
        String emailNormalizado = dto.getEmail().toLowerCase().trim();
        String estadoUFNormalizado = dto.getEstadoUF().toUpperCase().trim();


        /*
         * CRIAÇÃO DO PRODUTO
         * */
        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setTipoPessoa(dto.getTipoPessoa() != null ? dto.getTipoPessoa() : TipoPessoa.CONSUMIDOR_FINAL);
        cliente.setCpfCnpj(cpfCnpjLimpo);
        cliente.setInscricaoEstadual(dto.getInscricaoEstadual());
        cliente.setEmail(emailNormalizado);
        cliente.setTelefone(telefoneLimpo);
        cliente.setEnderecoCompleto(dto.getEnderecoCompleto());
        cliente.setCidade(dto.getCidade());
        cliente.setEstadoUF(estadoUFNormalizado);
        cliente.setCep(cepLimpo);
        cliente.setBairro(dto.getBairro());
        cliente.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true); // Novo cliente sempre ativo

        /*
         * SALVA E RETORNA DTO DE RESPOSTA
         * */
        Cliente clienteSalvo = clienteRepository.save(cliente);
        return clienteMapper.toResponseDTO(clienteSalvo);
    }

    // Deletar um cliente por ID
    @Override
    public void delete(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente com ID " + id + " não encontrado."));

        if (cliente.getId() == null) {
            throw new BusinessException("Cliente não existe!");
        }

        clienteRepository.deleteById(id);
    }
}
