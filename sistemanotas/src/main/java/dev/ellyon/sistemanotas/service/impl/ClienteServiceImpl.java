package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.cliente.ClienteRequestDTO;
import dev.ellyon.sistemanotas.dto.cliente.ClienteResponseDTO;
import dev.ellyon.sistemanotas.exception.BusinessException;
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
         * VALIDAÇÕES
         * */
        if(dto.getNome() == null || dto.getNome().isBlank()) {
            errors.put("nome", "O nome do cliente é obrigatório.");
        }

        if (dto.getTipoPessoa() == null) {
            dto.setTipoPessoa(TipoPessoa.CONSUMIDOR_FINAL);
        }

        if(dto.getCpfCnpj().isEmpty() || dto.getCpfCnpj().isBlank()) {
            errors.put("cpfCnpj", "O CPF/CNPJ do cliente é obrigatório.");
        }
        if(clienteRepository.existsByCpfCnpj(dto.getCpfCnpj())) {
            errors.put("cpfCnpj", "O CPF/CNPJ do cliente já está cadastrado.");
        }

        if(dto.getEmail() == null || dto.getEmail().isBlank()) {
            errors.put("email", "O email do cliente é obrigatório.");
        } else if (!dto.getEmail().contains("@")) {
            errors.put("email", "O email do cliente é inválido.");
        }
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Já existe um cliente com este email: " + dto.getEmail());
        }

        if(dto.getTelefone().isEmpty() || dto.getTelefone().isBlank()) {
            errors.put("telefone", "O telefone do cliente é obrigatório.");
        }

        if(dto.getEnderecoCompleto() == null || dto.getEnderecoCompleto().isBlank()) {
            errors.put("endereco", "O endereço do cliente é obrigatório.");
        }

        if(dto.getCidade() == null || dto.getCidade().isBlank()) {
            errors.put("cidade", "A cidade do cliente é obrigatória.");
        }

        if(dto.getEstadoUF() == null || dto.getEstadoUF().isBlank()) {
            errors.put("estado", "O estado do cliente é obrigatório.");
        }

        if(dto.getCep() == null || dto.getCep().isBlank()) {
            errors.put("cep", "O CEP do cliente é obrigatório.");
        }

        if(dto.getBairro() == null || dto.getBairro().isBlank()) {
            errors.put("bairro", "O bairro do cliente é obrigatório.");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do produto", errors);
        }

        // Normaliza o email para minúsculas e sem espaços extras
        String emailNormalizado = dto.getEmail().toLowerCase().trim();

        /*
         * CRIAÇÃO DO PRODUTO
         * */
        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setTipoPessoa(dto.getTipoPessoa() != null ? dto.getTipoPessoa() : TipoPessoa.CONSUMIDOR_FINAL);
        cliente.setCpfCnpj(dto.getCpfCnpj());
        cliente.setInscricaoEstadual(dto.getInscricaoEstadual());
        cliente.setEmail(emailNormalizado);
        cliente.setTelefone(dto.getTelefone());
        cliente.setEnderecoCompleto(dto.getEnderecoCompleto());
        cliente.setCidade(dto.getCidade());
        cliente.setEstadoUF(dto.getEstadoUF());
        cliente.setCep(dto.getCep());
        cliente.setBairro(dto.getBairro());
        cliente.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true); // Novo cliente sempre ativo

        /*
         * SALVA E RETORNA DTO DE RESPOSTA
         * */
        Cliente clienteSalvo = clienteRepository.save(cliente);
        return clienteMapper.toResponseDTO(clienteSalvo);
    }
}
