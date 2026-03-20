package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.cliente.ClienteListResponseDTO;
import dev.ellyon.sistemanotas.dto.cliente.ClienteRequestDTO;
import dev.ellyon.sistemanotas.dto.cliente.ClienteResponseDTO;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.EntityNotFoundException;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.Cliente;
import dev.ellyon.sistemanotas.model.Empresa;
import dev.ellyon.sistemanotas.model.EmpresaUsuario;
import dev.ellyon.sistemanotas.model.Usuario;
import dev.ellyon.sistemanotas.model.enums.Perfil;
import dev.ellyon.sistemanotas.model.enums.TipoPessoa;
import dev.ellyon.sistemanotas.repository.ClienteRepository;
import dev.ellyon.sistemanotas.repository.EmpresaRepository;
import dev.ellyon.sistemanotas.repository.EmpresaUsuarioRepository;
import dev.ellyon.sistemanotas.repository.UsuarioRepository;
import dev.ellyon.sistemanotas.service.ClienteService;
import dev.ellyon.sistemanotas.service.mapper.ClienteMapper;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
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
    // Injeção de dependências
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaUsuarioRepository empresaUsuarioRepository;
    private final ClienteMapper clienteMapper;

    // Construtor para injeção de dependências
    public ClienteServiceImpl(ClienteRepository clienteRepository, UsuarioRepository usuarioRepository, EmpresaUsuarioRepository empresaUsuarioRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaUsuarioRepository = empresaUsuarioRepository;
        this.clienteMapper = clienteMapper;
    }

    // Criar um novo cliente
    @Override
    public ClienteResponseDTO create(ClienteRequestDTO dto, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao cliente criado (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente criado (se necessário)
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresa = empresasUsuario.get(0).getEmpresa();

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

        // Validacoes
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
            }  else {
                // Buscar todos clientes da empresa
                List<Cliente> clientesDaEmpresa = clienteRepository.findByEmpresaId(empresa.getId());

                // Verificar se já existe CPF/CNPJ duplicado
                boolean cpfJaExiste = clientesDaEmpresa.stream()
                        .anyMatch(c -> c.getCpfCnpj().equals(cpfCnpjLimpo));

                if (cpfJaExiste) {
                    errors.put("cpfCnpj", "O CPF/CNPJ já está cadastrado");
                }
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

        // Normalização final dos dados
        String emailNormalizado = dto.getEmail().toLowerCase().trim();
        String estadoUFNormalizado = dto.getEstadoUF().toUpperCase().trim();

        // Criação da entidade Cliente a partir do DTO
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
        cliente.setEmpresa(empresa);
        cliente.setAtivo(true); // Novo cliente sempre ativo

        // Persiste a entidade e retorna o DTO de resposta
        Cliente clienteSalvo = clienteRepository.save(cliente);
        return clienteMapper.toResponseDTO(clienteSalvo);
    }

    // Deletar um cliente por ID
    @Override
    public void delete(Long id, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Verifica se o cliente existe antes de tentar deletar
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente com ID " + id + " não encontrado."));

        // Pegar o usuário logado para verificar permissão de exclusão (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // A empresa do usuário deve ser a mesma do cliente para permitir exclusão
        Empresa empresa = empresasUsuario.get(0).getEmpresa();
        if (!cliente.getEmpresa().getId().equals(empresa.getId())) {
            throw new BusinessException("Usuário não tem permissão para deletar este cliente");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        // Se o cliente existe, realiza a exclusão
        clienteRepository.deleteById(id);
    }

    // Atualizar um cliente por ID
    public ClienteResponseDTO update(Long id, ClienteRequestDTO dto, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Verifica se a cliente existe
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente", id));

        // Pegar o usuário logado para associar ao cliente criado (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente criado (se necessário)
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // A empresa do usuário deve ser a mesma do cliente para permitir exclusão
        Empresa empresa = empresasUsuario.get(0).getEmpresa();
        if (!clienteExistente.getEmpresa().getId().equals(empresa.getId())) {
            throw new BusinessException("Usuário não tem permissão para atualizar este cliente");
        }

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

        // Validacoes
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

        // Normalização final dos dados
        String emailNormalizado = dto.getEmail().toLowerCase().trim();
        String estadoUFNormalizado = dto.getEstadoUF().toUpperCase().trim();


        // Atualiza os dados do cliente existente com os dados do DTO
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

        // Persiste as alterações e retorna o DTO de resposta
        Cliente clienteAtualizado = clienteRepository.save(clienteExistente);
        return clienteMapper.toResponseDTO(clienteAtualizado);
    }

    // Desativar um cliente por ID (soft delete)
    @Override
    public void softDelete(Long id, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Verifica se o cliente existe antes de tentar deletar
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente com ID " + id + " não encontrado."));

        // Pegar o usuário logado para verificar permissão de exclusão (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // A empresa do usuário deve ser a mesma do cliente para permitir exclusão
        Empresa empresa = empresasUsuario.get(0).getEmpresa();
        if (!cliente.getEmpresa().getId().equals(empresa.getId())) {
            throw new BusinessException("Usuário não tem permissão para desativar este cliente");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        // Se o cliente existe, realiza a desativação (soft delete) e salva a entidade
        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }

    // Ativar um cliente
    @Override
    public void activate(Long id, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Verifica se o cliente existe antes de tentar deletar
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente com ID " + id + " não encontrado."));

        // Pegar o usuário logado para verificar permissão de exclusão (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // A empresa do usuário deve ser a mesma do cliente para permitir exclusão
        Empresa empresa = empresasUsuario.get(0).getEmpresa();
        if (!cliente.getEmpresa().getId().equals(empresa.getId())) {
            throw new BusinessException("Usuário não tem permissão para ativar este cliente");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        // Se o cliente existe, realiza a ativação e salva a entidade
        cliente.setAtivo(true);
        clienteRepository.save(cliente);
    }

    // Buscar um cliente por ID
    @Override
    public ClienteResponseDTO findById(Long id, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Verifica se o cliente existe antes de tentar buscar
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente", id));

        // Pegar o usuário logado para verificar permissão de exclusão (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // A empresa do usuário deve ser a mesma do cliente para permitir exclusão
        Empresa empresa = empresasUsuario.get(0).getEmpresa();
        if (!cliente.getEmpresa().getId().equals(empresa.getId())) {
            throw new BusinessException("Usuário não tem permissão para buscar este cliente");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        // Se o cliente existe, retorna o DTO de resposta
        return clienteMapper.toResponseDTO(cliente);
    }

    // Buscar todos os clientes
    @Override
    public List<ClienteListResponseDTO> findAll(Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Lista para armazenar os clientes encontrados
        List<Cliente> clientes;

        // Pegar o usuário logado para verificar permissão de exclusão (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // A empresa do usuário retorna os clientes associados a ela
        Empresa empresa = empresasUsuario.get(0).getEmpresa();
        clientes = clienteRepository.findByEmpresaId(empresa.getId());

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        // Se a lista de clientes estiver vazia, lança uma exceção informando que nenhum cliente foi encontrado
        if (clientes.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado.");
        }

        // Converte a lista de entidades Cliente para uma lista de DTOs ClienteListResponseDTO usando o mapper e retorna a lista de DTOs
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar todos os clientes com paginação
    @Override
    public Page<ClienteListResponseDTO> findAllPaged(Pageable pageable, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para verificar permissão de exclusão (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // A empresa do usuário retorna os clientes associados a ela com paginação
        Long empresaId = empresasUsuario.get(0).getEmpresa().getId();
        Page<Cliente> clientesPage = clienteRepository.findByEmpresaId(empresaId, pageable);

        // Se a página de clientes estiver vazia, lança uma exceção informando que nenhum cliente foi encontrado
        if(clientesPage.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado.");
        }

        // Converte a página de entidades Cliente para uma página de DTOs ClienteListResponseDTO usando o mapper e retorna a página de DTOs
        return clientesPage.map(clienteMapper::toListResponseDTO);
    }

    // Buscar cliente por CPF/CNPJ
    @Override
    public ClienteResponseDTO findByCpfCnpj(String cpfCnpj, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para verificar permissão de exclusão (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Remove pontos, traços e barras do CPF/CNPJ para garantir a busca sem formatação
        String cpfCnpjLimpo = cpfCnpj != null ? cpfCnpj.replaceAll("[^0-9]", "") : "";

        // Busca o cliente no banco de dados usando o CPF/CNPJ limpo
        Optional<Cliente> cliente = clienteRepository.findByCpfCnpj(cpfCnpjLimpo);

        // Se o cliente não for encontrado, lança uma exceção informando que o cliente com o CPF/CNPJ especificado não foi encontrado
        if (cliente.isEmpty()) {
            throw new EntityNotFoundException("Cliente com CPF/CNPJ " + cpfCnpj + " não encontrado.");
        }

        // Verifica se a empresa do cliente é a mesma do usuário para garantir que o usuário tem permissão para acessar os dados do cliente
        Empresa empresa = empresasUsuario.get(0).getEmpresa();
        if (!cliente.get().getEmpresa().getId().equals(empresa.getId())) {
            throw new BusinessException("Usuário não tem permissão para acessar este cliente");
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        // Verificar se o usuário é ADMIN ou Vendedor
        /*
        if (!empresasUsuario.get(0).getPerfil().equals(Perfil.ADMIN) && !empresasUsuario.get(0).getPerfil().equals(Perfil.VENDEDOR)) {
            throw new BusinessException("Usuário não tem permissão para acessar este cliente");
        }*/

        // Se o cliente for encontrado, converte a entidade Cliente para um DTO ClienteResponseDTO usando o mapper e retorna o DTO
        return clienteMapper.toResponseDTO(cliente.get());
    }

    // Buscar cliente pelo tipo de pessoa
    @Override
    public List<ClienteListResponseDTO> findByTipoPessoa(String tipoPessoaStr, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Converter a string para o enum TipoPessoa
        TipoPessoa tipoPessoa;
        try {
            tipoPessoa = TipoPessoa.fromCodigo(tipoPessoaStr);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de pessoa inválido: " + tipoPessoaStr + ". Valores aceitos: FISICA, JURIDICA, CONSUMIDOR FINAL, CONSUMIDOR NAO IDENTIFICADO.");
        }

        // Pegar o usuário logado para verificar permissão de exclusão (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // A empresa do usuário retorna os clientes associados a ela com o tipo de pessoa especificado
        Empresa empresa = empresasUsuario.get(0).getEmpresa();
        List<Cliente> clientes = clienteRepository.findByEmpresaIdAndTipoPessoa(empresa.getId(), tipoPessoa);

        // Se a lista de clientes estiver vazia, lança uma exceção informando que nenhum cliente foi encontrado com o tipo de pessoa especificado
        if (clientes.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado com tipo de pessoa: " + tipoPessoa.getDescricao());
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        // Converte a lista de entidades Cliente para uma lista de DTOs ClienteListResponseDTO usando o mapper e retorna a lista de DTOs
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar clientes por email (contendo, case insensitive)
    @Override
    public List<ClienteListResponseDTO> findByEmailContainingIgnoreCase(String email, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para verificar permissão de exclusão (se necessário)
        String emailUsuario = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // A empresa do usuário retorna os clientes associados a ela
        Empresa empresa = empresasUsuario.get(0).getEmpresa();
        // Busca clientes no banco de dados cujo email contenha a string fornecida, ignorando maiúsculas e minúsculas
        Optional<Cliente> clientes = clienteRepository.findByEmpresaIdAndEmailContainingIgnoreCase(empresa.getId(), email);

        // Se a lista de clientes estiver vazia, lança uma exceção informando que nenhum cliente foi encontrado com o email contendo a string especificada
        if (clientes.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado com email contendo: " + email);
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        // Converte a lista de entidades Cliente para uma lista de DTOs ClienteListResponseDTO usando o mapper e retorna a lista de DTOs
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar clientes por telefone (contendo, case insensitive)
    @Override
    public List<ClienteListResponseDTO> findByTelefoneContainingIgnoreCase(String telefone, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para verificar permissão de exclusão (se necessário)
        String emailUsuario = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // A empresa do usuário retorna os clientes associados a ela
        Empresa empresa = empresasUsuario.get(0).getEmpresa();
        // Busca clientes no banco de dados cujo telefone contenha a string fornecida, ignorando maiúsculas e minúsculas
        List<Cliente> clientes = clienteRepository.findByEmpresaIdAndTelefoneContainingIgnoreCase(empresa.getId(), telefone);

        // Se a lista de clientes estiver vazia, lança uma exceção informando que nenhum cliente foi encontrado com o telefone contendo a string especificada
        if (clientes.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado com telefone contendo: " + telefone);
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        // Converte a lista de entidades Cliente para uma lista de DTOs ClienteListResponseDTO usando o mapper e retorna a lista de DTOs
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar clientes por cidade (contendo, case insensitive)
    @Override
    public List<ClienteListResponseDTO> findByCidadeContainingIgnoreCase(String cidade, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para verificar permissão de exclusão (se necessário)
        String emailUsuario = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // A empresa do usuário retorna os clientes associados a ela
        Empresa empresa = empresasUsuario.get(0).getEmpresa();
        // Busca clientes no banco de dados cuja cidade contenha a string fornecida, ignorando maiúsculas e minúsculas
        List<Cliente> clientes = clienteRepository.findByEmpresaIdAndCidadeContainingIgnoreCase(empresa.getId(), cidade);

        // Se a lista de clientes estiver vazia, lança uma exceção informando que nenhum cliente foi encontrado com a cidade contendo a string especificada
        if (clientes.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado na cidade: " + cidade);
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        // Converte a lista de entidades Cliente para uma lista de DTOs ClienteListResponseDTO usando o mapper e retorna a lista de DTOs
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar clientes por estado
    @Override
    public List<ClienteListResponseDTO> findByEstadoUF(String estadoUF, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para verificar permissão de exclusão (se necessário)
        String emailUsuario = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // A empresa do usuário retorna os clientes associados a ela
        Empresa empresa = empresasUsuario.get(0).getEmpresa();
        // Busca clientes no banco de dados cujo estadoUF seja igual à string fornecida, ignorando maiúsculas e minúsculas
        List<Cliente> clientes = clienteRepository.findByEmpresaIdAndEstadoUF(empresa.getId(), estadoUF.toUpperCase().trim());

        // Se a lista de clientes estiver vazia, lança uma exceção informando que nenhum cliente foi encontrado no estado especificado
        if (clientes.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado no estado: " + estadoUF);
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        // Converte a lista de entidades Cliente para uma lista de DTOs ClienteListResponseDTO usando o mapper e retorna a lista de DTOs
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar clientes por CEP
    @Override
    public List<ClienteListResponseDTO> findByCep(String cep, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Remove traço do CEP para garantir a busca sem formatação
        String cepLimpo = cep != null ? cep.replaceAll("[^0-9]", "") : "";

        // Pegar o usuário logado para verificar permissão de exclusão (se necessário)
        String emailUsuario = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // A empresa do usuário retorna os clientes associados a ela
        Empresa empresa = empresasUsuario.get(0).getEmpresa();
        // Busca clientes no banco de dados cujo CEP seja igual à string fornecida, ignorando formatação
        List<Cliente> clientes = clienteRepository.findByEmpresaIdAndCep(empresa.getId(), cepLimpo);

        // Se a lista de clientes estiver vazia, lança uma exceção informando que nenhum cliente foi encontrado no CEP especificado
        if (clientes.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado no CEP: " + cepLimpo);
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        // Converte a lista de entidades Cliente para uma lista de DTOs ClienteListResponseDTO usando o mapper e retorna a lista de DTOs
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar clientes por status (Ativo/Inativo)
    @Override
    public List<ClienteListResponseDTO> findByIsAtivo(Boolean isAtivo, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para verificar permissão de exclusão (se necessário)
        String emailUsuario = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // A empresa do usuário retorna os clientes associados a ela
        Empresa empresa = empresasUsuario.get(0).getEmpresa();
        // Busca clientes no banco de dados cujo status de ativo seja igual ao valor fornecido
        List<Cliente> clientes = clienteRepository.findByEmpresaIdAndIsAtivo(empresa.getId(), isAtivo);

        // Se a lista de clientes estiver vazia, lança uma exceção informando que nenhum cliente foi encontrado com o status especificado
        if (clientes.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado com status: " + (isAtivo ? "Ativo" : "Inativo"));
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        // Converte a lista de entidades Cliente para uma lista de DTOs ClienteListResponseDTO usando o mapper e retorna a lista de DTOs
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar clientes por nome (contendo, case insensitive)
    @Override
    public List<ClienteListResponseDTO> findByNomeContainingIgnoreCase(String nome, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para verificar permissão de exclusão (se necessário)
        String emailUsuario = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // A empresa do usuário retorna os clientes associados a ela
        Empresa empresa = empresasUsuario.get(0).getEmpresa();
        // Busca clientes no banco de dados cujo nome contenha a string fornecida, ignorando maiúsculas e minúsculas
        List<Cliente> clientes = clienteRepository.findByEmpresaIdAndNomeContainingIgnoreCase(empresa.getId(), nome);

        // Se a lista de clientes estiver vazia, lança uma exceção informando que nenhum cliente foi encontrado com o nome contendo a string especificada
        if (clientes.isEmpty()){
            throw new EntityNotFoundException("Nenhum cliente encontrado com nome contendo: " + nome);
        }

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        // Converte a lista de entidades Cliente para uma lista de DTOs ClienteListResponseDTO usando o mapper e retorna a lista de DTOs
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar clientes por faixa de data de criação
    @Override
    public List<ClienteListResponseDTO> findByCreatedAtBetween(LocalDateTime dataInicio, LocalDateTime dataFim, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para verificar permissão de exclusão (se necessário)
        String emailUsuario = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // A empresa do usuário retorna os clientes associados a ela
        Empresa empresa = empresasUsuario.get(0).getEmpresa();

        // Validações básicas para garantir que as datas não sejam nulas e que a data de início não seja posterior à data de fim
        if (dataInicio == null || dataFim == null) {
            throw new BusinessException("Data de início e data de fim são obrigatórias");
        }

        // Verifica se a data de início é posterior à data de fim
        if (dataInicio.isAfter(dataFim)) {
            throw new BusinessException("Data de início não pode ser posterior à data de fim");
        }

        // Busca clientes no banco de dados cuja data de criação esteja entre as datas fornecidas
        List<Cliente> clientes = clienteRepository.findByEmpresaIdAndCreatedAtBetween(empresa.getId(), dataInicio, dataFim);

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

        // Se houver erros de validação, lança ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do cliente", errors);
        }

        // Converte a lista de entidades Cliente para uma lista de DTOs ClienteListResponseDTO usando o mapper e retorna a lista de DTOs
        return clientes.stream().map(clienteMapper::toListResponseDTO).collect(Collectors.toList());
    }
}
