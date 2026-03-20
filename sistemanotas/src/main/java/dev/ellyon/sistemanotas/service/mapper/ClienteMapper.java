package dev.ellyon.sistemanotas.service.mapper;

import dev.ellyon.sistemanotas.dto.cliente.ClienteListResponseDTO;
import dev.ellyon.sistemanotas.dto.cliente.ClienteResponseDTO;
import dev.ellyon.sistemanotas.model.Cliente;
import dev.ellyon.sistemanotas.utils.FormatUtils;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {
    public ClienteResponseDTO toResponseDTO(Cliente cliente){
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTipoPessoa().toString(),
                FormatUtils.formatCpfCnpj(cliente.getCpfCnpj()),
                cliente.getInscricaoEstadual(),
                cliente.getEmail(),
                FormatUtils.formatTelefone(cliente.getTelefone()),
                cliente.getEnderecoCompleto(),
                cliente.getCidade(),
                cliente.getEstadoUF(),
                FormatUtils.formatCep(cliente.getCep()),
                cliente.getBairro(),
                cliente.getAtivo(),
                cliente.getCreatedAt(),
                cliente.getUpdatedAt()
        );
    }

    public ClienteListResponseDTO toListResponseDTO(Cliente cliente){
        return new ClienteListResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTipoPessoa().toString(),
                FormatUtils.formatCpfCnpj(cliente.getCpfCnpj()),
                FormatUtils.formatTelefone(cliente.getTelefone()),
                cliente.getCidade(),
                cliente.getEstadoUF(),
                cliente.getEmail(),
                cliente.getAtivo()
        );
    }
}
