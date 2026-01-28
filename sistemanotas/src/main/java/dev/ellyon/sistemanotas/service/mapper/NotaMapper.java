package dev.ellyon.sistemanotas.service.mapper;

import dev.ellyon.sistemanotas.dto.cliente.ClienteSimpleResponseDTO;
import dev.ellyon.sistemanotas.dto.empresa.EmpresaSimpleResponseDTO;
import dev.ellyon.sistemanotas.dto.itemNota.ItemNotaResponseDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaListResponseDTO;
import dev.ellyon.sistemanotas.dto.nota.NotaResponseDTO;
import dev.ellyon.sistemanotas.dto.usuario.UsuarioSimpleResponseDTO;
import dev.ellyon.sistemanotas.model.Cliente;
import dev.ellyon.sistemanotas.model.Empresa;
import dev.ellyon.sistemanotas.model.ItemNota;
import dev.ellyon.sistemanotas.model.Nota;
import org.springframework.stereotype.Component;

@Component
public class NotaMapper {
    private final ItemNotaMapper itemNotaMapper;
    public NotaMapper(ItemNotaMapper itemNotaMapper) {
        this.itemNotaMapper = itemNotaMapper;
    }

    public NotaResponseDTO toResponseDTO(Nota nota) {
        if (nota == null) {
            return null;
        }
        // Mapear empresa
        EmpresaSimpleResponseDTO empresaDTO = new EmpresaSimpleResponseDTO(
                nota.getEmpresa().getId(),
                nota.getEmpresa().getNomeFantasia(),
                nota.getEmpresa().getCnpj()
        );

        // Mapear Cliente
        ClienteSimpleResponseDTO clienteDTO = null;
        if (nota.getCliente() != null) {
            clienteDTO = new ClienteSimpleResponseDTO(
                    nota.getCliente().getId(),
                    nota.getCliente().getNome(),
                    nota.getCliente().getCpfCnpj()
            );
        }

        // Mapear Itens
        ItemNotaResponseDTO[] itensDTO = new ItemNotaResponseDTO[0];
        if (nota.getItens() != null && !nota.getItens().isEmpty()) {
            itensDTO = nota.getItens().stream()
                    .map(itemNotaMapper::toResponseDTO)
                    .toArray(ItemNotaResponseDTO[]::new);
        }

        UsuarioSimpleResponseDTO createdByDTO = null;
        if (nota.getCreatedBy() != null) {
            createdByDTO = new UsuarioSimpleResponseDTO(
                    nota.getCreatedBy().getId(),
                    nota.getCreatedBy().getNome()
            );
        }

        return new NotaResponseDTO(
                nota.getId(),
                nota.getNumero(),
                nota.getSerie(),
                nota.getTipo().toString(),
                nota.getStatus().toString(),
                empresaDTO,
                clienteDTO,
                nota.getDataEmissao(),
                nota.getDataCancelamento(),
                itensDTO,
                nota.getValorProdutos(),
                nota.getValorImpostosTotal(),
                nota.getValorTotal(),
                nota.getObservacoes(),
                createdByDTO,
                nota.getCreatedAt(),
                nota.getUpdatedAt()
        );
    }

    public NotaListResponseDTO toListResponseDTO(Nota nota) {
        if (nota == null) {
            return null;
        }
        return new NotaListResponseDTO(
                nota.getId(),
                nota.getNumero(),
                nota.getTipo().toString(),
                nota.getStatus().toString(),
                nota.getCliente().getNome().trim(),
                nota.getCliente().getTipoPessoa().toString(),
                nota.getDataEmissao()
        );
    }
}
