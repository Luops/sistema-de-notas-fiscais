package dev.ellyon.sistemanotas.service.impl;

import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioRequestDTO;
import dev.ellyon.sistemanotas.dto.empresaUsuario.EmpresaUsuarioResponseDTO;
import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.exception.EntityNotFoundException;
import dev.ellyon.sistemanotas.exception.ValidationException;
import dev.ellyon.sistemanotas.model.Empresa;
import dev.ellyon.sistemanotas.model.EmpresaUsuario;
import dev.ellyon.sistemanotas.model.Usuario;
import dev.ellyon.sistemanotas.model.enums.Perfil;
import dev.ellyon.sistemanotas.repository.EmpresaRepository;
import dev.ellyon.sistemanotas.repository.EmpresaUsuarioRepository;
import dev.ellyon.sistemanotas.repository.UsuarioRepository;
import dev.ellyon.sistemanotas.service.EmpresaService;
import dev.ellyon.sistemanotas.service.EmpresaUsuarioService;
import dev.ellyon.sistemanotas.service.UsuarioService;
import dev.ellyon.sistemanotas.service.mapper.EmpresaUsuarioMapper;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmpresaUsuarioServiceImpl implements EmpresaUsuarioService {
    private final EmpresaUsuarioMapper empresaUsuarioMapper;
    private final UsuarioService usuarioService;
    private final EmpresaService empresaService;
    private final EmpresaUsuarioRepository empresaUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;

    public EmpresaUsuarioServiceImpl(EmpresaUsuarioMapper empresaUsuarioMapper, UsuarioService usuarioService, EmpresaService empresaService, EmpresaUsuarioRepository empresaUsuarioRepository, UsuarioRepository usuarioRepository, EmpresaRepository empresaRepository) {
        this.empresaUsuarioMapper = empresaUsuarioMapper;
        this.usuarioService = usuarioService;
        this.empresaService = empresaService;
        this.empresaUsuarioRepository = empresaUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
    }

    // Associar Empresa e Usuário
    @Override
    public EmpresaUsuarioResponseDTO associarEmpresaUsuario(EmpresaUsuarioRequestDTO dto, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao cliente criado (se necessário)
        String email = authentication.getName();
        Usuario usuarioLogged = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente criado (se necessário)
        List<EmpresaUsuario> empresasUsuarioLogged = empresaUsuarioRepository.findByUsuarioId(usuarioLogged.getId());

        if (empresasUsuarioLogged.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresaLogged = empresasUsuarioLogged.get(0).getEmpresa();

        // Verificar permissão ADMIN na empresa e se a empresa a ser vinculada é a mesma do usuário logado
        boolean isAdmin = empresasUsuarioLogged.stream()
                .anyMatch(eu -> eu.getEmpresa().getId().equals(empresaLogged.getId()));
        if (!isAdmin) {
            errors.put("permissao", "Usuário logado não tem permissão de ADMIN nesta empresa");
        }

        // Verifica se o usuário a ser vinculado existe
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseGet(() -> {
                    errors.put("usuarioId", "Usuário com ID " + dto.getUsuarioId() + " não encontrado");
                    return null;
                });

        // Verificar se usuário a ser vinculado está ativo
        if (usuario != null && !usuario.getIsAtivo()) {
            errors.put("usuarioId", "Usuário está inativo");
        }

        // Verifica se a empresa a ser vinculada existe
        Empresa empresa = empresaRepository.findByIdAndIsAtivo(dto.getEmpresaId(), true)
                .orElseGet(() -> {
                    errors.put("empresaId", "Empresa com ID " + dto.getEmpresaId() + " não encontrada ou inativa");
                    return null;
                });

        // Verifica se já existe uma associação entre a empresa e o usuário
        if (empresaUsuarioRepository.existsByUsuarioIdAndEmpresaId(dto.getUsuarioId(), dto.getEmpresaId())) {
            errors.put("associacao", "Usuário já está associado a esta empresa!");
        }

        // Valida o perfil
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

        // Se houver erros, lança uma exceção
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }

        // Cria a associação entre empresa e usuário
        EmpresaUsuario empresaUsuario = new EmpresaUsuario(empresa, usuario, perfil);
        EmpresaUsuario empresaUsuarioSalva = empresaUsuarioRepository.save(empresaUsuario);

        // Mapeia para DTO e retorna
        return empresaUsuarioMapper.toResponseDTO(empresaUsuarioSalva);
    }

    // Alterar perfil
    @Override
    public EmpresaUsuarioResponseDTO alterarPerfil(EmpresaUsuarioRequestDTO dto, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao cliente criado (se necessário)
        String email = authentication.getName();
        Usuario usuarioLogged = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente criado (se necessário)
        List<EmpresaUsuario> empresasUsuarioLogged = empresaUsuarioRepository.findByUsuarioId(usuarioLogged.getId());

        if (empresasUsuarioLogged.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresaLogged = empresasUsuarioLogged.get(0).getEmpresa();

        // Buscar usuario
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseGet(() -> {
                    errors.put("usuarioId", "Usuário com ID " + dto.getUsuarioId() + " não encontrado");
                    return null;
                });

        // Buscar empresa
        Empresa empresa = empresaRepository.findByIdAndIsAtivo(dto.getEmpresaId(), true)
                .orElseGet(() -> {
                    errors.put("empresaId", "Empresa com ID " + dto.getEmpresaId() + " não encontrada ou inativa");
                    return null;
                });

        // Buscar associacao
        EmpresaUsuario empresaUsuario = empresaUsuarioRepository
                .findByUsuarioIdAndEmpresaId(dto.getUsuarioId(), dto.getEmpresaId())
                .orElseGet(() -> {
                    errors.put("associacao", "Usuário não está associado a esta empresa");
                    return null;
                });

        // Verificar permissão ADMIN na empresa e se a empresa a ser vinculada é a mesma do usuário logado
        boolean isAdmin = empresasUsuarioLogged.stream()
                .anyMatch(eu -> eu.getEmpresa().getId().equals(empresaLogged.getId()));
        if (!isAdmin) {
            errors.put("permissao", "Usuário logado não tem permissão de ADMIN nesta empresa");
        }

        // Valida o perfil
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

        // Verificar se o perfil já é o mesmo
        if (empresaUsuario != null && perfil != null && empresaUsuario.getPerfil() == perfil) {
            errors.put("perfil", "O usuário já possui o perfil " + perfil.name() + " nesta empresa");
        }

        // Se houver erros, lança exceção
        if (!errors.isEmpty()) {
            throw new ValidationException("Erro de validação", errors);
        }

        // Atualizar perfil
        empresaUsuario.setPerfil(perfil);
        EmpresaUsuario empresaUsuarioAtualizada = empresaUsuarioRepository.save(empresaUsuario);

        // Retornar DTO
        return empresaUsuarioMapper.toResponseDTO(empresaUsuarioAtualizada);

    }

    // Obter usuários por empresa
    @Override
    public List<EmpresaUsuarioResponseDTO> findByEmpresaId(Long empresaId, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao cliente criado (se necessário)
        String email = authentication.getName();
        Usuario usuarioLogged = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente criado (se necessário)
        List<EmpresaUsuario> empresasUsuarioLogged = empresaUsuarioRepository.findByUsuarioId(usuarioLogged.getId());

        if (empresasUsuarioLogged.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresaLogged = empresasUsuarioLogged.get(0).getEmpresa();

        // Verificar se empresa existe
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa", empresaId));

        // Verificar permissão ADMIN na empresa e se a empresa a ser vinculada é a mesma do usuário logado
        boolean isAdmin = empresasUsuarioLogged.stream()
                .anyMatch(eu -> eu.getEmpresa().getId().equals(empresaLogged.getId()));
        if (!isAdmin) {
            errors.put("permissao", "Usuário logado não tem permissão de ADMIN nesta empresa");
        }

        // Buscar associações
        List<EmpresaUsuario> empresaUsuarios = empresaUsuarioRepository.findByEmpresaId(empresaId);

        if (empresaUsuarios.isEmpty()) {
            throw new EntityNotFoundException("Nenhum usuário encontrado para a empresa com ID: " + empresaId);
        }

        // Mapear para DTO incluindo o perfil
        return empresaUsuarios.stream().map(empresaUsuarioMapper::toResponseDTO).collect(Collectors.toList());

    }

    // Obter usuários por perfil
    @Override
    public List<EmpresaUsuarioResponseDTO> findByPerfil(String perfilStr, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao cliente criado (se necessário)
        String email = authentication.getName();
        Usuario usuarioLogged = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente criado (se necessário)
        List<EmpresaUsuario> empresasUsuarioLogged = empresaUsuarioRepository.findByUsuarioId(usuarioLogged.getId());

        if (empresasUsuarioLogged.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresaLogged = empresasUsuarioLogged.get(0).getEmpresa();

        // Converter a string do perfil para o enum Perfil
        Perfil perfil;
        try {
            perfil = Perfil.fromCodigo(perfilStr);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Perfil inválido: " + perfilStr + ". Valores permitidos: ADMIN, VENDEDOR, VISUALIZADOR");
        }

        // Buscar associações por perfil
        List<EmpresaUsuario> usuarios = empresaUsuarioRepository.findByPerfil(perfil);

        // Filtrar apenas os usuários que estão vinculados à mesma empresa do usuário logado (se necessário)
        List<EmpresaUsuario> usuariosEmpresa = usuarios.stream()
                .filter(eu -> eu.getEmpresa().getId().equals(empresaLogged.getId()))
                .collect(Collectors.toList());


        if (usuariosEmpresa.isEmpty()){
            throw new EntityNotFoundException("Nenhum usuário encontrado com o perfil: " + perfilStr);
        }

        return usuariosEmpresa.stream()
                .map(empresaUsuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Obter empresas por usuario
    @Override
    public List<EmpresaUsuarioResponseDTO> findByUsuarioId(Long usuarioId, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao cliente criado (se necessário)
        String email = authentication.getName();
        Usuario usuarioLogged = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente criado (se necessário)
        List<EmpresaUsuario> empresasUsuarioLogged = empresaUsuarioRepository.findByUsuarioId(usuarioLogged.getId());

        if (empresasUsuarioLogged.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresaLogged = empresasUsuarioLogged.get(0).getEmpresa();

        // Verificar se usuario existe
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario", usuarioId));

        // Buscar associações
        List<EmpresaUsuario> empresaUsuarios = empresaUsuarioRepository.findByUsuarioId(usuarioId);

        // Verificar se o usuario logado é o mesmo do usuarioId
        boolean isSameUser = usuarioLogged.getId().equals(usuarioId);
        if (!isSameUser) {
            throw new BusinessException("Usuário logado pode visualizar apenas suas próprias associações com empresas");
        }

        if (empresaUsuarios.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma empresa encontrada para o usuario com ID: " + usuarioId);
        }

        // Mapear para DTO incluindo o perfil
        return empresaUsuarios.stream().map(empresaUsuarioMapper::toResponseDTO).collect(Collectors.toList());

    }

    // Buscar vinculo especifico e uma empresa e usuario
    @Override
    public EmpresaUsuarioResponseDTO findByEmpresaIdUsuarioId(Long empresaId, Long usuarioId, Authentication authentication) {
        // Validações das exceções
        Map<String, String> errors = new HashMap<>();

        // Pegar o usuário logado para associar ao cliente criado (se necessário)
        String email = authentication.getName();
        Usuario usuarioLogged = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Pegar primeira empresa do usuário logado para associar ao cliente criado (se necessário)
        List<EmpresaUsuario> empresasUsuarioLogged = empresaUsuarioRepository.findByUsuarioId(usuarioLogged.getId());

        if (empresasUsuarioLogged.isEmpty()) {
            throw new BusinessException("Usuário não está vinculado a nenhuma empresa");
        }

        // Pegar a primeira empresa (você pode melhorar isso deixando o usuário escolher)
        Empresa empresaLogged = empresasUsuarioLogged.get(0).getEmpresa();

        // Verificar se empresa existe
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa", empresaId));

        // Verificar se usuario existe
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario", usuarioId));

        // Buscar associações
        EmpresaUsuario empresaUsuario = empresaUsuarioRepository
                .findByUsuarioIdAndEmpresaId(usuarioId, empresaId) // ✅ Ordem correta
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Nenhum vínculo encontrado entre o usuário '%s' e a empresa '%s'",
                                usuario.getNome(), empresa.getNomeFantasia())
                ));

        // Verificar se o usuario logado é o mesmo do usuarioId
        boolean isSameUser = usuarioLogged.getId().equals(usuarioId);
        if (!isSameUser) {
            throw new BusinessException("Usuário logado pode visualizar apenas suas próprias associações com empresas");
        }

        return empresaUsuarioMapper.toResponseDTO(empresaUsuario);
    }
}
