package dev.ellyon.sistemanotas.service.mapper;

import dev.ellyon.sistemanotas.dto.cliente.ClienteListResponseDTO;
import dev.ellyon.sistemanotas.dto.cliente.ClienteResponseDTO;
import dev.ellyon.sistemanotas.model.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {
    public ClienteResponseDTO toResponseDTO(Cliente cliente){
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTipoPessoa().toString(),
                cliente.getCpfCnpj(),
                cliente.getInscricaoEstadual(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getEnderecoCompleto(),
                cliente.getCidade(),
                cliente.getEstadoUF(),
                cliente.getCep(),
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
                cliente.getCpfCnpj(),
                cliente.getCidade(),
                cliente.getTelefone(),
                cliente.getEstadoUF(),
                cliente.getAtivo()
        );
    }
}
