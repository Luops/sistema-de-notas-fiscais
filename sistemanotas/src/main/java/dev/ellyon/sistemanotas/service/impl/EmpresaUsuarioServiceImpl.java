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
    public EmpresaUsuarioResponseDTO associarEmpresaUsuario(EmpresaUsuarioRequestDTO dto) {
        // Declaracao dos errors
        Map<String, String> errors = new HashMap<>();

        // Verifica se o usuário existe
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseGet(() -> {
                    errors.put("usuarioId", "Usuário com ID " + dto.getUsuarioId() + " não encontrado");
                    return null;
                });

        // Verificar se usuário está ativo
        if (usuario != null && !usuario.getIsAtivo()) {
            errors.put("usuarioId", "Usuário está inativo");
        }

        // Verifica se a empresa existe
        Empresa empresa = empresaRepository.findByIdAndIsAtivo(dto.getEmpresaId(), true)
                .orElseGet(() -> {
                    errors.put("empresaId", "Empresa com ID " + dto.getEmpresaId() + " não encontrada ou inativa");
                    return null;
                });

        // Verifica se já existe uma associação entre a empresa e o usuário
        if (empresaUsuarioRepository.existsByUsuarioIdAndEmpresaId(dto.getUsuarioId(), dto.getEmpresaId())) {
            errors.put("associacao", "Usuário já está associado a esta empresa");
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
    public EmpresaUsuarioResponseDTO alterarPerfil(EmpresaUsuarioRequestDTO dto) {
        // Declaracao dos errors
        Map<String, String> errors = new HashMap<>();

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
    public List<EmpresaUsuarioResponseDTO> findByEmpresaId(Long empresaId) {
        // Verificar se empresa existe
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa", empresaId));

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
    public List<EmpresaUsuarioResponseDTO> findByPerfil(String perfilStr) {
        // Converter a string do perfil para o enum Perfil
        Perfil perfil;
        try {
            perfil = Perfil.fromCodigo(perfilStr);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Perfil inválido: " + perfilStr + ". Valores permitidos: ADMIN, VENDEDOR, VISUALIZADOR");
        }

        List<EmpresaUsuario> usuarios = empresaUsuarioRepository.findByPerfil(perfil);

        if (usuarios.isEmpty()){
            throw new EntityNotFoundException("Nenhum usuário encontrado com o perfil: " + perfilStr);
        }

        return usuarios.stream()
                .map(empresaUsuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Obter empresas por usuario
    @Override
    public List<EmpresaUsuarioResponseDTO> findByUsuarioId(Long usuarioId) {
        // Verificar se usuario existe
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario", usuarioId));

        // Buscar associações
        List<EmpresaUsuario> empresaUsuarios = empresaUsuarioRepository.findByUsuarioId(usuarioId);

        if (empresaUsuarios.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma empresa encontrada para o usuario com ID: " + usuarioId);
        }

        // Mapear para DTO incluindo o perfil
        return empresaUsuarios.stream().map(empresaUsuarioMapper::toResponseDTO).collect(Collectors.toList());

    }

    // Buscar vinculo especifico e uma empresa e usuario
    @Override
    public EmpresaUsuarioResponseDTO findByEmpresaIdUsuarioId(Long empresaId, Long usuarioId) {
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

        return empresaUsuarioMapper.toResponseDTO(empresaUsuario);
    }
}
