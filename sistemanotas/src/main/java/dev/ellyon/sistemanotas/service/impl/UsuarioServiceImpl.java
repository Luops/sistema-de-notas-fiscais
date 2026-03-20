package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioResponseDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioRequestDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioResponseDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioUpdateRequestDTO;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.EntityNotFoundException;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.Empresa;
import dev.ellyon.sistemanotas.model.EmpresaUsuario;
import dev.ellyon.sistemanotas.model.Usuario;
import dev.ellyon.sistemanotas.model.enums.Perfil;
import dev.ellyon.sistemanotas.repository.EmpresaRepository;
import dev.ellyon.sistemanotas.repository.EmpresaUsuarioRepository;
import dev.ellyon.sistemanotas.repository.NotaRepository;
import dev.ellyon.sistemanotas.repository.UsuarioRepository;
import dev.ellyon.sistemanotas.service.UsuarioService;
import dev.ellyon.sistemanotas.service.mapper.EmpresaUsuarioMapper;
import dev.ellyon.sistemanotas.service.mapper.UsuarioMapper;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final EmpresaUsuarioRepository empresaUsuarioRepository;
    private final NotaRepository notaRepository;
    private final UsuarioMapper usuarioMapper;
    private final EmpresaUsuarioMapper empresaUsuarioMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, EmpresaRepository empresaRepository, EmpresaUsuarioRepository empresaUsuarioRepository, NotaRepository notaRepository, UsuarioMapper usuarioMapper, EmpresaUsuarioMapper empresaUsuarioMapper, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.empresaUsuarioRepository = empresaUsuarioRepository;
        this.notaRepository = notaRepository;
        this.usuarioMapper = usuarioMapper;
        this.empresaUsuarioMapper = empresaUsuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // Criar um novo usuário
    @Override
    public UsuarioResponseDTO create(UsuarioRequestDTO dto) {
        Map<String, String> errors = new HashMap<>();

        // ========================================
        // 1. VALIDAÇÕES
        // ========================================

        // Verificar unicidade do email
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            errors.put("email", "Email já cadastrado no sistema");
        }

        // Verificar se empresa existe e está ativa
        Empresa empresa = empresaRepository.findByIdAndIsAtivo(dto.getEmpresaId(), true)
                .orElseGet(() -> {
                    errors.put("empresaId", "Empresa não encontrada ou inativa");
                    return null;
                });

        // Validar e converter perfil de String para Enum
        Perfil perfil = null;
        if (dto.getPerfil() == null || dto.getPerfil().isBlank()) {
            errors.put("perfil", "Perfil é obrigatório");
        } else {
            try {
                perfil = Perfil.valueOf(dto.getPerfil().toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.put("perfil", "Perfil inválido. Valores permitidos: ADMIN, VENDEDOR, VISUALIZADOR");
            }
        }

        // Verificar quantos usuários a empresa já possui
        int qtdUsuariosEmpresa = empresaUsuarioRepository.countEmpresaUsuarioByEmpresaId(dto.getEmpresaId());

        // Se já existe usuário na empresa, não pode criar outro ADMIN
        if (perfil == Perfil.ADMIN && qtdUsuariosEmpresa > 0) {
            errors.put("perfil", "Esta empresa já possui usuários. Não é possível criar outro ADMIN.");
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do usuário", errors);
        }

        // ========================================
        // 2. CRIAR USUÁRIO
        // ========================================
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha())); // Criptografar senha
        usuario.setIsAtivo(true);

        // Salvar usuário
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // ========================================
        // 3. ASSOCIAR USUÁRIO À EMPRESA
        // ========================================
        //  Se é o PRIMEIRO usuário da empresa → SEMPRE será ADMIN
        //  Se JÁ EXISTEM usuários → usa o perfil informado (mas não pode ser ADMIN, validado acima)

        Perfil perfilFinal;
        if (qtdUsuariosEmpresa == 0) {
            // Primeiro usuário da empresa SEMPRE é ADMIN
            perfilFinal = Perfil.ADMIN;
        } else {
            // Usuários subsequentes usam o perfil informado
            perfilFinal = perfil;
        }

        // Verificar se já existe vínculo (evita duplicação)
        boolean vinculoExiste = empresaUsuarioRepository.existsByUsuarioIdAndEmpresaId(
                usuarioSalvo.getId(),
                empresa.getId()
        );

        if (!vinculoExiste) {
            EmpresaUsuario empresaUsuario = new EmpresaUsuario(empresa, usuarioSalvo, perfilFinal);
            empresaUsuarioRepository.save(empresaUsuario);
        } else {
            // Se por algum motivo já existe, apenas atualiza o perfil
            EmpresaUsuario empresaUsuarioExistente = empresaUsuarioRepository
                    .findByUsuarioIdAndEmpresaId(usuarioSalvo.getId(), empresa.getId())
                    .orElseThrow(() -> new BusinessException("Erro ao buscar vínculo empresa-usuário"));

            empresaUsuarioExistente.setPerfil(perfilFinal);
            empresaUsuarioRepository.save(empresaUsuarioExistente);
        }

        // ========================================
        // 4. RETORNAR DTO (SEM SENHA)
        // ========================================
        return usuarioMapper.toResponseDTO(usuarioSalvo);
    }

    // Atualizar um usuário existente
    @Override
    public UsuarioResponseDTO update(Long id, UsuarioUpdateRequestDTO dto, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresa = empresasUsuario.get(0).getEmpresa();

        // Buscar usuário existente
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Usuário não encontrado com ID: " + id, null));

        // Verificar se o usuário logado é da mesma empresa
        boolean isSameCompany = empresaUsuarioRepository.findByUsuarioIdAndEmpresaId(usuario.getId(), empresa.getId())
                .isPresent();
        if (!isSameCompany) {
            errors.put("authorization", "Acesso negado. Você só pode desativar usuários da sua própria empresa.");
            throw new ValidationException("Erro de autorização", errors);
        }

        // Verificar se usuário está ativo
        if (!usuarioExistente.getIsAtivo()) {
            errors.put("usuario", "Não é possível atualizar usuário inativo");
        }

        // Verificar unicidade do email (se alterado)
        if (!usuarioExistente.getEmail().equals(dto.getEmail()) &&
                usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            errors.put("email", "Email já cadastrado no sistema");
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do usuário", errors);
        }

        // Atualizar dados do usuário, pessoais
        usuarioExistente.setNome(dto.getNome());
        usuarioExistente.setEmail(dto.getEmail());

        // Atualizar senha apenas se fornecida
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuarioExistente.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        // Salvar usuário atualizado
        Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);
        return usuarioMapper.toResponseDTO(usuarioAtualizado);
    }

    // Deletar um usuário
    @Override
    public void delete(Long id, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresa = empresasUsuario.get(0).getEmpresa();

        // Buscar usuário existente
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Usuário não encontrado com ID: " + id, null));

        // Verificar se o usuário logado é da mesma empresa
        boolean isSameCompany = empresaUsuarioRepository.findByUsuarioIdAndEmpresaId(usuario.getId(), empresa.getId())
                .isPresent();
        if (!isSameCompany) {
            errors.put("authorization", "Acesso negado. Você só pode desativar usuários da sua própria empresa.");
            throw new ValidationException("Erro de autorização", errors);
        }

        // Verificar se usuário está ativo
        if (usuarioExistente.getIsAtivo()) {
            errors.put("usuario", "Não é possível deletar um usuário ativo. Desative-o primeiro.");
            throw new ValidationException("Erro de validação", errors);
        }

        // Verificar se usuário possui notas vinculadas
        long qtdNotasVinculadas = notaRepository.countByCreatedById(id);

        if (qtdNotasVinculadas > 0) {
            errors.put("notas", String.format(
                    "Não é possível deletar o usuário. Existem %d nota(s) vinculada(s) a ele. " +
                            "Considere manter o usuário inativo em vez de deletá-lo.",
                    qtdNotasVinculadas
            ));
            throw new ValidationException("Erro de validação", errors);
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do usuário", errors);
        }

        // Deletar associações EmpresaUsuario
        List<EmpresaUsuario> associacoes = empresaUsuarioRepository.findByUsuarioId(id);
        if (!associacoes.isEmpty()) {
            empresaUsuarioRepository.deleteAll(associacoes);
        }

        // Deletar usuário
        usuarioRepository.delete(usuarioExistente);
    }

    // Desativar um usuário
    @Override
    public void softDelete(Long id, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresa = empresasUsuario.get(0).getEmpresa();

        // Buscar usuário existente
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Usuário não encontrado com ID: " + id, null));

        // Verificar se o usuário logado é da mesma empresa
        boolean isSameCompany = empresaUsuarioRepository.findByUsuarioIdAndEmpresaId(usuario.getId(), empresa.getId())
                .isPresent();
        if (!isSameCompany) {
            errors.put("authorization", "Acesso negado. Você só pode desativar usuários da sua própria empresa.");
            throw new ValidationException("Erro de autorização", errors);
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do usuário", errors);
        }

        usuarioExistente.setIsAtivo(false);
        usuarioRepository.save(usuarioExistente);
    }

    // Ativar um usuário
    @Override
    public void activate(Long id, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuario = empresaUsuarioRepository.findByUsuarioId(usuario.getId());

        if (empresasUsuario.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresa = empresasUsuario.get(0).getEmpresa();

        // Buscar usuário existente
        Usuario usuarioExistente = usuarioRepository.findById(id).orElseThrow(() -> new ValidationException("Usuário não encontrado com ID: " + id, null));

        // Verificar se o usuário logado é da mesma empresa
        boolean isSameCompany = empresaUsuarioRepository.findByUsuarioIdAndEmpresaId(usuario.getId(), empresa.getId())
                .isPresent();
        if (!isSameCompany) {
            errors.put("authorization", "Acesso negado. Você só pode ativar usuários da sua própria empresa.");
            throw new ValidationException("Erro de autorização", errors);
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do usuário", errors);
        }

        usuarioExistente.setIsAtivo(true);
        usuarioRepository.save(usuarioExistente);
    }

    // Obter um usuário por ID
    @Override
    public UsuarioResponseDTO findById(Long id, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String email = authentication.getName();
        Usuario usuarioLogged = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuarioLogged = empresaUsuarioRepository.findByUsuarioId(usuarioLogged.getId());
        if (empresasUsuarioLogged.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresaLogged = empresasUsuarioLogged.get(0).getEmpresa();

        // Buscar usuário existente
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Usuário não encontrado com ID: " + id, null));

        // Buscar empresas do usuário buscado
        List<EmpresaUsuario> empresasUsuarioBuscado = empresaUsuarioRepository.findByUsuarioId(usuarioLogged.getId());

        // Verificar se o usuário logado é da mesma empresa do usuário buscado, se nao tiver nenhuma empresa vinculada deixa passar so se for admin ou ele mesmo
        boolean isSameCompany = empresasUsuarioBuscado.stream()
                .anyMatch(eu -> eu.getEmpresa().getId().equals(empresaLogged.getId()));
        boolean isAdmin = empresasUsuarioLogged.stream().anyMatch(eu -> eu.getPerfil() == Perfil.ADMIN);
        boolean isOwnProfile = usuario.getEmail().equals(email);
        if (!isSameCompany && !isAdmin && !isOwnProfile) {
            errors.put("authorization", "Acesso negado. Você só pode visualizar usuários da sua própria empresa ou se for ADMIN.");
            throw new ValidationException("Erro de autorização", errors);
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do usuário", errors);
        }

        return usuarioMapper.toResponseDTO(usuario);
    }

    // Obter todos os usuários
    @Override
    public List<UsuarioResponseDTO> findAll(Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String email = authentication.getName();
        Usuario usuarioLogged = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuarioLogged = empresaUsuarioRepository.findByUsuarioId(usuarioLogged.getId());
        if (empresasUsuarioLogged.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresaLogged = empresasUsuarioLogged.get(0).getEmpresa();

        // Buscar todos os usuários da mesma empresa
        List<Usuario> usuario = usuarioRepository.findAll();
        List<Usuario> usuariosMesmaEmpresa = usuario.stream()
                .filter(u -> empresaUsuarioRepository.findByUsuarioIdAndEmpresaId(u.getId(), empresaLogged.getId()).isPresent())
                .collect(Collectors.toList());
        if (usuariosMesmaEmpresa.isEmpty()) {
            throw new EntityNotFoundException("Nenhum usuário encontrado para a empresa: " + empresaLogged.getNomeFantasia());
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do usuário", errors);
        }

        return usuariosMesmaEmpresa.stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Obter usuários por email
    @Override
    public List<UsuarioResponseDTO> findByEmail(String email, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String emailLogged = authentication.getName();
        Usuario usuarioLogged = usuarioRepository.findByEmail(emailLogged)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuarioLogged = empresaUsuarioRepository.findByUsuarioId(usuarioLogged.getId());
        if (empresasUsuarioLogged.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresaLogged = empresasUsuarioLogged.get(0).getEmpresa();

        // Buscar usuário por email somente se for da mesma empresa, se nao tiver nenhuma empresa vinculada deixa passar so se for admin ou ele mesmo
        List<Usuario> usuarios = usuarioRepository.findByEmailContainingIgnoreCase(email);
        List<Usuario> usuariosMesmaEmpresa = usuarios.stream()
                .filter(u -> empresaUsuarioRepository.findByUsuarioIdAndEmpresaId(u.getId(), empresaLogged.getId()).isPresent())
                .collect(Collectors.toList());
        boolean isAdmin = empresasUsuarioLogged.stream().anyMatch(eu -> eu.getPerfil() == Perfil.ADMIN);
        boolean isOwnProfile = usuarios.stream().anyMatch(u -> u.getEmail().equals(emailLogged));
        if (usuariosMesmaEmpresa.isEmpty() && !isAdmin && !isOwnProfile) {
            errors.put("authorization", "Acesso negado. Você só pode visualizar usuários da sua própria empresa ou se for ADMIN.");
            throw new ValidationException("Erro de autorização", errors);
        }

        if (usuariosMesmaEmpresa.isEmpty()) {
            throw new EntityNotFoundException("Nenhum usuário encontrado com o email: " + email);
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do usuário", errors);
        }

        return usuariosMesmaEmpresa.stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Obter usuários por nome
    @Override
    public List<UsuarioResponseDTO> findByNome(String nome, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String emailLogged = authentication.getName();
        Usuario usuarioLogged = usuarioRepository.findByEmail(emailLogged)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuarioLogged = empresaUsuarioRepository.findByUsuarioId(usuarioLogged.getId());
        if (empresasUsuarioLogged.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresaLogged = empresasUsuarioLogged.get(0).getEmpresa();

        // Buscar usuário por nome somente se for da mesma empresa, se nao tiver nenhuma empresa vinculada deixa passar so se for admin ou ele mesmo
        List<Usuario> usuarios = usuarioRepository.findByNomeContainingIgnoreCase(nome);
        List<Usuario> usuariosMesmaEmpresa = usuarios.stream()
                .filter(u -> empresaUsuarioRepository.findByUsuarioIdAndEmpresaId(u.getId(), empresaLogged.getId()).isPresent())
                .collect(Collectors.toList());
        boolean isAdmin = empresasUsuarioLogged.stream().anyMatch(eu -> eu.getPerfil() == Perfil.ADMIN);
        boolean isOwnProfile = usuarios.stream().anyMatch(u -> u.getEmail().equals(emailLogged));
        if (usuariosMesmaEmpresa.isEmpty() && !isAdmin && !isOwnProfile) {
            errors.put("authorization", "Acesso negado. Você só pode visualizar usuários da sua própria empresa ou se for ADMIN.");
            throw new ValidationException("Erro de autorização", errors);
        }

        if (usuariosMesmaEmpresa.isEmpty()) {
            throw new EntityNotFoundException("Nenhum usuário encontrado com o nome: " + nome);
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação nos dados do usuário", errors);
        }

        return usuariosMesmaEmpresa.stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Obter usuários por status de ativo
    @Override
    public List<UsuarioResponseDTO> findByAtivo(boolean ativo, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao produto criado (se necessário)
        String emailLogged = authentication.getName();
        Usuario usuarioLogged = usuarioRepository.findByEmail(emailLogged)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao produto criado (se necessário)
        List<EmpresaUsuario> empresasUsuarioLogged = empresaUsuarioRepository.findByUsuarioId(usuarioLogged.getId());
        if (empresasUsuarioLogged.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresaLogged = empresasUsuarioLogged.get(0).getEmpresa();

        // Verificar se é ADMIN
        boolean isAdmin = empresasUsuarioLogged.stream()
                .anyMatch(eu -> eu.getPerfil() == Perfil.ADMIN);

        // Se for admin Retorna TODOS da empresa com aquele status
        if (isAdmin) {
            List<Usuario> usuariosMesmaEmpresa = usuarioRepository.findByIsAtivo(ativo).stream()
                    .filter(u -> empresaUsuarioRepository
                            .findByUsuarioIdAndEmpresaId(u.getId(), empresaLogged.getId())
                            .isPresent())
                    .collect(Collectors.toList());

            if (usuariosMesmaEmpresa.isEmpty()) {
                throw new EntityNotFoundException("Nenhum usuário " + (ativo ? "ativo" : "inativo") + " encontrado na empresa");
            }

            return usuariosMesmaEmpresa.stream()
                    .map(usuarioMapper::toResponseDTO)
                    .collect(Collectors.toList());
        }

        // Se nao for admin (se o status bater)
        if (usuarioLogged.getIsAtivo().equals(ativo)) {
            return List.of(usuarioMapper.toResponseDTO(usuarioLogged));
        }

        // Status não bate com o usuário logado
        throw new BusinessException(
                "Você não tem permissão para visualizar outros usuários. " +
                        "Apenas ADMIN pode buscar todos os usuários."
        );
    }

    public boolean isOwnProfile(Long userId) {
        // Obter email do usuário autenticado do SecurityContext
        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();

        // Obter usuário que quer editar
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("Usuário não encontrado com ID: " + userId, null));

        // Comparar emails
        return usuario.getEmail().equals(emailAutenticado);
    }

    public boolean isOwnProfileByEmail(String email) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        String emailLogado = auth.getName();

        // Comparação case-insensitive
        return emailLogado.equalsIgnoreCase(email);
    }

}
