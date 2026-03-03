package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.empresa.*;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.EntityNotFoundException;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.Empresa;
import dev.ellyon.sistemanotas.model.EmpresaUsuario;
import dev.ellyon.sistemanotas.model.Nota;
import dev.ellyon.sistemanotas.repository.EmpresaRepository;
import dev.ellyon.sistemanotas.repository.EmpresaUsuarioRepository;
import dev.ellyon.sistemanotas.repository.NotaRepository;
import dev.ellyon.sistemanotas.repository.UsuarioRepository;
import dev.ellyon.sistemanotas.service.CriptografiaService;
import dev.ellyon.sistemanotas.service.EmpresaService;
import dev.ellyon.sistemanotas.service.mapper.EmpresaMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmpresaServiceImpl implements EmpresaService {
    private final EmpresaRepository empresaRepository;
    private final EmpresaUsuarioRepository empresaUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotaRepository notaRepository;
    private final EmpresaMapper empresaMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CriptografiaService criptografiaService;
    public EmpresaServiceImpl(EmpresaRepository empresaRepository, EmpresaUsuarioRepository empresaUsuarioRepository, UsuarioRepository usuarioRepository, NotaRepository notaRepository, EmpresaMapper empresaMapper, BCryptPasswordEncoder passwordEncoder, CriptografiaService criptografiaService) {
        this.empresaRepository = empresaRepository;
        this.empresaUsuarioRepository = empresaUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.notaRepository = notaRepository;
        this.empresaMapper = empresaMapper;
        this.passwordEncoder = passwordEncoder;
        this.criptografiaService = criptografiaService;
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
        // Declaracao dos errors
        Map<String, String> errors = new HashMap<>();

        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa com ID " + id + " não encontrada."));


        if (empresa.getId() == null) {
            throw new BusinessException("Empresa não existe!");
        }

        // Verificar se há usuário vinculados a empresa
        List<EmpresaUsuario> empresaUsuario = empresaUsuarioRepository.findByEmpresaId(id);
        if (!empresaUsuario.isEmpty()){
            errors.put("empresaUsuario", "Há vinculos dessa empresa com usuários. É recomendável somente desativar a empresa!");
        }

        // Verificar se há notas vinculaas a empresa
        List<Nota> notas = notaRepository.findByEmpresaId(id);
        if(!notas.isEmpty()){
            errors.put("notas", "Há vinculos dessa empresa com alguma nota. É recomendável somente desativar a empresa!");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Não é possível deletar a empresa", errors);
        }

        empresaRepository.delete(empresa);
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

    // Fazer upload do certificado
    @Override
    public CertificadoResponseDTO uploadCertificado(Long empresaId, CertificadoUploadDTO dto) {

        // Buscar empresa
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new BusinessException("Empresa não encontrada"));

        MultipartFile arquivo = dto.getArquivo();

        // Validar arquivo
        if (arquivo.isEmpty()) {
            throw new BusinessException("Arquivo do certificado está vazio");
        }

        if (!arquivo.getOriginalFilename().endsWith(".pfx") &&
                !arquivo.getOriginalFilename().endsWith(".p12")) {
            throw new BusinessException("Arquivo deve ser .pfx ou .p12");
        }

        if (arquivo.getSize() > 10 * 1024 * 1024) {  // 10 MB
            throw new BusinessException("Arquivo muito grande. Tamanho máximo: 10 MB");
        }

        try {
            byte[] certificadoBytes = arquivo.getBytes();
            String senha = dto.getSenha();

            // Validar certificado (tentar carregar)
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new ByteArrayInputStream(certificadoBytes), senha.toCharArray());

            // Extrair informações do certificado
            Enumeration<String> aliases = keyStore.aliases();
            if (!aliases.hasMoreElements()) {
                throw new BusinessException("Certificado não contém chaves válidas");
            }

            String alias = aliases.nextElement();
            X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);

            // Verificar validade
            cert.checkValidity();

            // Extrair CNPJ do certificado
            String cnpjCertificado = extrairCNPJ(cert.getSubjectX500Principal().getName());

            // Validar se CNPJ do certificado bate com CNPJ da empresa
            if (!cnpjCertificado.equals(empresa.getCnpj())) {
                throw new BusinessException(
                        String.format("CNPJ do certificado (%s) difere do CNPJ da empresa (%s)",
                                cnpjCertificado, empresa.getCnpj())
                );
            }

            // Extrair data de validade
            LocalDateTime validade = cert.getNotAfter().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            // CRIPTOGRAFAR SENHA COM AES-256
            String senhaCriptografada = criptografiaService.criptografar(senha);

            // Salvar certificado na empresa
            empresa.setCertificadoDigital(certificadoBytes);
            empresa.setCertificadoSenhaCriptografada(senhaCriptografada);  // ✅ Senha criptografada
            empresa.setCertificadoTipo(dto.getTipo());
            empresa.setCertificadoValidade(validade);
            empresa.setCertificadoCnpj(cnpjCertificado);
            empresa.setCertificadoAtivo(true);
            empresa.setCertificadoUploadDate(LocalDateTime.now());

            empresaRepository.save(empresa);

            System.out.println("✅ Certificado salvo para empresa: " + empresa.getNomeFantasia());
            System.out.println("   CNPJ: " + cnpjCertificado);
            System.out.println("   Validade: " + validade);
            System.out.println("   Senha: [CRIPTOGRAFADA COM AES-256]");

            // Retornar informações
            return new CertificadoResponseDTO(
                    true,
                    dto.getTipo(),
                    cnpjCertificado,
                    validade,
                    LocalDateTime.now(),
                    empresa.diasParaVencer(),
                    !empresa.isCertificadoValido(),
                    (long) certificadoBytes.length
            );

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erro ao processar certificado: " + e.getMessage());
        }
    }

    // Buscar certificado
    @Override
    public CertificadoResponseDTO buscarCertificado(Long empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new BusinessException("Empresa não encontrada"));

        if (empresa.getCertificadoDigital() == null) {
            throw new BusinessException("Empresa não possui certificado cadastrado");
        }

        return new CertificadoResponseDTO(
                empresa.getCertificadoAtivo(),
                empresa.getCertificadoTipo(),
                empresa.getCertificadoCnpj(),
                empresa.getCertificadoValidade(),
                empresa.getCertificadoUploadDate(),
                empresa.diasParaVencer(),
                !empresa.isCertificadoValido(),
                (long) empresa.getCertificadoDigital().length
        );
    }

    // Remover certificado
    @Override
    public void removerCertificado(Long empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new BusinessException("Empresa não encontrada"));

        empresa.setCertificadoDigital(null);
        empresa.setCertificadoSenhaCriptografada(null);
        empresa.setCertificadoTipo(null);
        empresa.setCertificadoValidade(null);
        empresa.setCertificadoCnpj(null);
        empresa.setCertificadoAtivo(false);
        empresa.setCertificadoUploadDate(null);

        empresaRepository.save(empresa);

        System.out.println("✅ Certificado removido da empresa: " + empresa.getNomeFantasia());
    }

    // Extrair certificado
    private String extrairCNPJ(String subject) {
        try {
            if (subject.contains("OID.2.5.4.97=")) {
                String cnpjPart = subject.split("OID.2.5.4.97=")[1].split(",")[0];
                return cnpjPart.replaceAll("[^0-9]", "");
            }

            if (subject.contains("SERIALNUMBER=")) {
                String cnpjPart = subject.split("SERIALNUMBER=")[1].split(",")[0];
                return cnpjPart.replaceAll("[^0-9]", "");
            }

            if (subject.contains("CN=") && subject.contains(":")) {
                String cnPart = subject.split("CN=")[1].split(",")[0];
                if (cnPart.contains(":")) {
                    return cnPart.split(":")[1].replaceAll("[^0-9]", "");
                }
            }

            throw new BusinessException("CNPJ não encontrado no certificado");
        } catch (Exception e) {
            throw new BusinessException("Erro ao extrair CNPJ do certificado: " + e.getMessage());
        }
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
        if(empresas.isEmpty()){
            throw new EntityNotFoundException("Nenhuma empresa encontrada.");
        }
        return empresas.stream().map(empresaMapper::toListResponseDTO).collect(Collectors.toList());
    }

    // Buscar todas as empresas com paginação
    @Override
    public Page<EmpresaListResponseDTO> findAllPaged(Pageable pageable) {
        Page<Empresa> empresasPage = empresaRepository.findAll(pageable);
        if (empresasPage.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma empresa encontrada.");
        }
        // Mapeia cada Empresa para EmpresaListResponseDTO
        return empresasPage.map(empresaMapper::toListResponseDTO);
    }

    // Buscar empresa por CNPJ
    @Override
    public EmpresaResponseDTO findByCnpj(String cnpj) {
        Empresa empresa = empresaRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new EntityNotFoundException("Empresa com CNPJ " + cnpj + " não encontrada."));
        return empresaMapper.toResponseDTO(empresa);
    }

    // Buscar empresas por razão social contendo um termo
    @Override
    public List<EmpresaListResponseDTO> findByRazaoSocialContainingIgnoreCase(String razaoSocial) {
        // Valida se o termo de busca não é vazio
        if (razaoSocial == null || razaoSocial.trim().isEmpty()) {
            throw new BusinessException("O termo de busca para razão social não pode ser vazio.");
        }

        String razaoSocialTrimmed = razaoSocial.trim();
        List<Empresa> empresas = empresaRepository.findByRazaoSocialContainingIgnoreCase(razaoSocialTrimmed);

        if(empresas.isEmpty()){
            throw new EntityNotFoundException("Nenhuma empresa encontrada com a razão social contendo: " + razaoSocialTrimmed);
        }
        return empresas.stream()
                .map(empresaMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar empresas pelo nome fantasia contendo um termo
    @Override
    public List<EmpresaListResponseDTO> findByNomeFantasiaContainingIgnoreCase(String nomeFantasia) {
        List<Empresa> empresas = empresaRepository.findByNomeFantasiaContainingIgnoreCase(nomeFantasia);
        if (empresas.isEmpty()){
            throw new EntityNotFoundException("Nenhuma empresa encontrada com o nome fantasia contendo: " + nomeFantasia);
        }

        return empresas.stream()
                .map(empresaMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar empresas por email
    @Override
    public List<EmpresaListResponseDTO> findByEmailContainingIgnoreCase(String email) {
        List<Empresa> empresas = empresaRepository.findByEmailContainingIgnoreCase(email);
        if (empresas.isEmpty()){
            throw new EntityNotFoundException("Nenhuma empresa encontrada com o email contendo: " + email);
        }
        return empresas.stream()
                .map(empresaMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar empresas por telefone
    @Override
    public List<EmpresaListResponseDTO> findByTelefoneContaining(String telefone) {
        List<Empresa> empresas = empresaRepository.findByTelefoneContaining(telefone);
        if (empresas.isEmpty()){
            throw new EntityNotFoundException("Nenhuma empresa encontrada com o telefone contendo: " + telefone);
        }
        return empresas.stream()
                .map(empresaMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar empresas por cidade
    @Override
    public List<EmpresaListResponseDTO> findByCidadeIgnoreCase(String cidade) {
        List<Empresa> empresas = empresaRepository.findByCidadeIgnoreCase(cidade);
        if (empresas.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma empresa encontrada na cidade: " + cidade);
        }
        return empresas.stream()
                .map(empresaMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar empresas por estado (UF)
    @Override
    public List<EmpresaListResponseDTO> findByEstadoUFIgnoreCase(String estadoUF) {
        List<Empresa> empresas = empresaRepository.findByEstadoUFIgnoreCase(estadoUF);
        if (empresas.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma empresa encontrada no estado (UF): " + estadoUF);
        }
        return empresas.stream()
                .map(empresaMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar empresas por CEP
    @Override
    public List<EmpresaListResponseDTO> findByCep(String cep) {
        List<Empresa> empresas = empresaRepository.findByCep(cep);
        if (empresas.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma empresa encontrada com o CEP: " + cep);
        }
        return empresas.stream()
                .map(empresaMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar empresas por status de ativo/inativo
    @Override
    public List<EmpresaListResponseDTO> findByIsAtivo(Boolean ativo) {
        List<Empresa> empresas = empresaRepository.findByIsAtivo(ativo);
        if (empresas.isEmpty()) {
            String status = ativo ? "ativas" : "inativas";
            throw new EntityNotFoundException("Nenhuma empresa " + status + " encontrada.");
        }
        return empresas.stream()
                .map(empresaMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar empresas criadas entre duas datas
    @Override
    public List<EmpresaListResponseDTO> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            throw new BusinessException("Data de início e data de fim são obrigatórias");
        }

        if (inicio.isAfter(fim)) {
            throw new BusinessException("Data de início não pode ser posterior à data de fim");
        }

        List<Empresa> empresas = empresaRepository.findByCreatedAtBetween(inicio, fim);

        // Formatação das datas para exibição na mensagem de erro
        if (empresas.isEmpty()) {
            // Formata a data para o padrão brasileiro
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataInicioFormatada = inicio.format(formatter);
            String dataFimFormatada = fim.format(formatter);

            throw new EntityNotFoundException(
                    String.format("Nenhuma empresa encontrada entre %s e %s",
                            dataInicioFormatada, dataFimFormatada)
            );
        }
        return empresas.stream()
                .map(empresaMapper::toListResponseDTO)
                .collect(Collectors.toList());
    }
}
