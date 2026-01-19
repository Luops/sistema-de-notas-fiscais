package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.cliente.ClienteListResponseDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
         * CRIAÇÃO DO CLIENTE
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
        cliente.setAtivo(true); // Novo cliente sempre ativo

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

    // Atualizar um cliente por ID
    public ClienteResponseDTO update(Long id, ClienteRequestDTO dto) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Verifica se a cliente existe
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente", id));

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
        } else if (cpfCnpjLimpo.length() < 11 && cpfCnpjLimpo.length() > 14) {
                errors.put("cpfCnpj", "CPF deve ter 11 dígitos ou CNPJ deve ter 14 dígitos");
        }   else {
            // Busca cliente com esse CPF/CNPJ
            Optional<Cliente> clienteComMesmoCnpj = clienteRepository.findByCpfCnpj(cpfCnpjLimpo);

            // Se encontrou um cliente com esse CPF/CNPJ
            if (clienteComMesmoCnpj.isPresent()) {
                // Verifica se NÃO é a mesmo cliente sendo editada
                if (!clienteComMesmoCnpj.get().getId().equals(id)) {
                    errors.put("cpfCnpj", "CPF/CNPJ já cadastrado no sistema");
                }
            }
        }

        // Validação de Email
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            errors.put("email", "Email é obrigatório");
        } else if (!dto.getEmail().contains("@")) {
            errors.put("email", "Email inválido");
        } else {
            String emailNormalizado = dto.getEmail().toLowerCase().trim();

            // Busca cliente com esse email
            Optional<Cliente> clienteComMesmoEmail = clienteRepository.findByEmailContainingIgnoreCase(emailNormalizado);

            // Se encontrou um cliente com esse email
            if (clienteComMesmoEmail.isPresent()) {
                // Verifica se NÃO é o mesmo cliente sendo editado
                if (!clienteComMesmoEmail.get().getId().equals(id)) {
                    errors.put("email", "Email já cadastrado no sistema");
                }
            }
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
         * ATUALIZACAO DO CLIENTE
         * */
        clienteExistente.setNome(dto.getNome().toLowerCase().trim());
        clienteExistente.setTipoPessoa(dto.getTipoPessoa() != null ? dto.getTipoPessoa() : TipoPessoa.CONSUMIDOR_NAO_IDENTIFICADO);
        clienteExistente.setCpfCnpj(cpfCnpjLimpo);
        clienteExistente.setInscricaoEstadual(dto.getInscricaoEstadual().trim());
        clienteExistente.setEmail(emailNormalizado);
        clienteExistente.setTelefone(telefoneLimpo);
        clienteExistente.setEnderecoCompleto(dto.getEnderecoCompleto().toLowerCase().trim());
        clienteExistente.setCidade(dto.getCidade().toLowerCase().trim());
        clienteExistente.setEstadoUF(estadoUFNormalizado);
        clienteExistente.setCep(cepLimpo);
        clienteExistente.setBairro(dto.getBairro().toLowerCase().trim());
        if (dto.getAtivo() != null) {
            clienteExistente.setAtivo(dto.getAtivo());
        }

        /*
         * SALVA E RETORNA DTO DE RESPOSTA
         * */
        Cliente clienteAtualizado = clienteRepository.save(clienteExistente);
        return clienteMapper.toResponseDTO(clienteAtualizado);
    }

    // Desativar um cliente por ID (soft delete)
    @Override
    public void softDelete(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente", id));

        if (cliente.getId() == null) {
            throw new BusinessException("Cliente não existe!");
        }

        cliente.setAtivo(false);
        clienteRepository.save(cliente);

    }

    // Ativar um cliente
    @Override
    public void activate(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente", id));

        if (cliente.getId() == null) {
            throw new BusinessException("Cliente não existe!");
        }

        cliente.setAtivo(true);
        clienteRepository.save(cliente);
    }

    // Buscar um cliente por ID
    @Override
    public ClienteResponseDTO findById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente", id));
        return clienteMapper.toResponseDTO(cliente);
    }

    // Buscar todos os clientes
    @Override
    public List<ClienteListResponseDTO> findAll() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar todos os clientes com paginação
    @Override
    public Page<ClienteListResponseDTO> findAllPaged(Pageable pageable) {
        Page<Cliente> clientesPage = clienteRepository.findAll(pageable);
        if(clientesPage.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado.");
        }

        // Converte Page<Cliente> para Page<ClienteListResponseDTO>
        return clientesPage.map(clienteMapper::toListResponseDTO);
    }

    // Buscar cliente por CPF/CNPJ
    @Override
    public ClienteResponseDTO findByCpfCnpj(String cpfCnpj) {
        Optional<Cliente> cliente = clienteRepository.findByCpfCnpj(cpfCnpj);
        if (cliente.isEmpty()) {
            throw new EntityNotFoundException("Cliente com CPF/CNPJ " + cpfCnpj + " não encontrado.");
        }

        return clienteMapper.toResponseDTO(cliente.get());
    }

    // Buscar cliente pelo tipo de pessoa
    @Override
    public List<ClienteListResponseDTO> findByTipoPessoa(String tipoPessoaStr) {
        // Converter a string para o enum TipoPessoa
        TipoPessoa tipoPessoa;
        try {
            tipoPessoa = TipoPessoa.fromCodigo(tipoPessoaStr);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de pessoa inválido: " + tipoPessoaStr + ". Valores aceitos: FISICA, JURIDICA, CONSUMIDOR FINAL, CONSUMIDOR NAO IDENTIFICADO.");
        }


        List<Cliente> clientes = clienteRepository.findByTipoPessoa(tipoPessoa);

        if (clientes.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado do tipo: " + tipoPessoa);
        }

        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar clientes por email (contendo, case insensitive)
    @Override
    public List<ClienteListResponseDTO> findByEmailContainingIgnoreCase(String email) {
        Optional<Cliente> clientes = clienteRepository.findByEmailContainingIgnoreCase(email);
        if (clientes.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado com email contendo: " + email);
        }
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar clientes por telefone (contendo, case insensitive)
    @Override
    public List<ClienteListResponseDTO> findByTelefoneContainingIgnoreCase(String telefone) {
        List<Cliente> clientes = clienteRepository.findByTelefoneContainingIgnoreCase(telefone);
        if (clientes.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado com telefone contendo: " + telefone);
        }
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar clientes por cidade (contendo, case insensitive)
    @Override
    public List<ClienteListResponseDTO> findByCidadeContainingIgnoreCase(String cidade) {
        List<Cliente> clientes = clienteRepository.findByCidadeContainingIgnoreCase(cidade);
        if (clientes.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado na cidade: " + cidade);
        }
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar clientes por estado
    @Override
    public List<ClienteListResponseDTO> findByEstadoUF(String estadoUF) {
        List<Cliente> clientes = clienteRepository.findByEstadoUF(estadoUF);
        if (clientes.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado no estado: " + estadoUF);
        }
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar clientes por CEP
    @Override
    public List<ClienteListResponseDTO> findByCep(String cep) {
        List<Cliente> clientes = clienteRepository.findByCep(cep);
        if (clientes.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado no CEP: " + cep);
        }
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar clientes por status (Ativo/Inativo)
    @Override
    public List<ClienteListResponseDTO> findByIsAtivo(Boolean isAtivo) {
        List<Cliente> clientes = clienteRepository.findByIsAtivo(isAtivo);
        if (clientes.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado com status: " + (isAtivo ? "Ativo" : "Inativo"));
        }
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar clientes por nome (contendo, case insensitive)
    @Override
    public List<ClienteListResponseDTO> findByNomeContainingIgnoreCase(String nome) {
        List<Cliente> clientes = clienteRepository.findByNomeContainingIgnoreCase(nome);
        if (clientes.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado com nome contendo: " + nome);
        }
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar clientes por faixa de data de criação
    @Override
    public List<ClienteListResponseDTO> findByCreatedAtBetween(LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new BusinessException("Data de início e data de fim são obrigatórias");
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new BusinessException("Data de início não pode ser posterior à data de fim");
        }

        List<Cliente> clientes = clienteRepository.findByCreatedAtBetween(dataInicio, dataFim);

        // Formatação das datas para exibição na mensagem de erro
        if (clientes.isEmpty()) {
            // Formata a data para o padrão brasileiro
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataInicioFormatada = dataInicio.format(formatter);
            String dataFimFormatada = dataFim.format(formatter);

            throw new EntityNotFoundException(
                    String.format("Nenhum cliente encontrado entre %s e %s",
                            dataInicioFormatada, dataFimFormatada)
            );
        }
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }
}
