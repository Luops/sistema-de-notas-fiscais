package dev.ellyon.sistemanotas.nfe.service;

import dev.ellyon.sistemanotas.exception.BusinessException;
import dev.ellyon.sistemanotas.model.Cliente;
import dev.ellyon.sistemanotas.model.Empresa;
import dev.ellyon.sistemanotas.model.Nota;
import dev.ellyon.sistemanotas.model.Usuario;
import dev.ellyon.sistemanotas.model.enums.StatusNota;
import dev.ellyon.sistemanotas.model.enums.TipoNota;
import dev.ellyon.sistemanotas.nfe.dto.NFeRetornoDTO;
import dev.ellyon.sistemanotas.repository.NotaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Cancelamento de NF-e")
class NFeCancelamentoServiceTest {

    @Mock
    private NotaRepository notaRepository;

    @Mock
    private SefazWebService sefazWebService;

    @InjectMocks
    private NFeService nfeService;

    private Nota notaEmitida;
    private Nota notaRascunho;
    private Nota notaCancelada;

    @BeforeEach
    void setUp() {
        // Configurar dados de teste
        notaEmitida = criarNotaEmitida();
        notaRascunho = criarNotaRascunho();
        notaCancelada = criarNotaCancelada();
    }

    @Test
    @DisplayName("Deve cancelar NF-e com sucesso")
    void testCancelarComSucesso() throws Exception {
        // Arrange
        Long notaId = 1L;
        String justificativa = "Devolução do produto por defeito de fabricação";
        
        when(notaRepository.findById(notaId)).thenReturn(Optional.of(notaEmitida));
        when(sefazWebService.cancelarNFe(
                anyString(),
                anyString(),
                eq(justificativa)
        )).thenReturn(gerarRespostaCancelamentoSucesso());

        // Act
        NFeRetornoDTO resultado = nfeService.cancelar(notaId, justificativa);

        // Assert
        assertNotNull(resultado);
        assertEquals("135", resultado.getCodigoStatus());
        assertEquals("Cancelamento de NF-e autorizado", resultado.getMensagem());
        assertNotNull(resultado.getProtocolo());
        
        // Verificar se foi salvo
        verify(notaRepository).save(any(Nota.class));
        verify(notaRepository).findById(notaId);
    }

    @Test
    @DisplayName("Deve rejeitar cancelamento com justificativa menor que 15 caracteres")
    void testCancelarComJustificativaInsuficiente() {
        // Arrange
        Long notaId = 1L;
        String justificativaInvalida = "Motivo curto";
        
        when(notaRepository.findById(notaId)).thenReturn(Optional.of(notaEmitida));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            nfeService.cancelar(notaId, justificativaInvalida);
        }, "Justificativa deve ter no mínimo 15 caracteres");
    }

    @Test
    @DisplayName("Deve rejeitar cancelamento de nota não encontrada")
    void testCancelarNotaNaoEncontrada() {
        // Arrange
        Long notaId = 999L;
        String justificativa = "Devolução do produto por defeito de fabricação";
        
        when(notaRepository.findById(notaId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            nfeService.cancelar(notaId, justificativa);
        });
    }

    @Test
    @DisplayName("Deve rejeitar cancelamento de nota não emitida")
    void testCancelarNotaNaoEmitida() {
        // Arrange
        Long notaId = 2L;
        String justificativa = "Devolução do produto por defeito de fabricação";
        
        when(notaRepository.findById(notaId)).thenReturn(Optional.of(notaRascunho));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            nfeService.cancelar(notaId, justificativa);
        }, "Apenas notas emitidas podem ser canceladas");
    }

    @Test
    @DisplayName("Deve rejeitar cancelamento com prazo expirado")
    void testCancelarComPrazoExpirado() {
        // Arrange
        Long notaId = 1L;
        String justificativa = "Devolução do produto por defeito de fabricação";
        
        // Nota emitida há mais de 24 horas
        Nota notaComPrazoExpirado = criarNotaEmitida();
        notaComPrazoExpirado.setDataEmissao(LocalDateTime.now().minusHours(25));
        
        when(notaRepository.findById(notaId)).thenReturn(Optional.of(notaComPrazoExpirado));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            nfeService.cancelar(notaId, justificativa);
        }, "Prazo de cancelamento expirado");
    }

    @Test
    @DisplayName("Deve atualizar nota com dados de cancelamento")
    void testAtualizarNotaAposCancelamento() throws Exception {
        // Arrange
        Long notaId = 1L;
        String justificativa = "Devolução do produto por defeito de fabricação";
        
        when(notaRepository.findById(notaId)).thenReturn(Optional.of(notaEmitida));
        when(sefazWebService.cancelarNFe(
                anyString(),
                anyString(),
                eq(justificativa)
        )).thenReturn(gerarRespostaCancelamentoSucesso());

        // Act
        nfeService.cancelar(notaId, justificativa);

        // Assert
        verify(notaRepository).save(argThat(nota ->
                nota.getStatus() == StatusNota.CANCELADA &&
                nota.getJustificativaCancelamento().equals(justificativa) &&
                nota.getProtocoloCancelamento() != null &&
                nota.getDataCancelamento() != null
        ));
    }

    @Test
    @DisplayName("Deve conter chave de acesso na resposta")
    void testRespostaContemChaveAcesso() throws Exception {
        // Arrange
        Long notaId = 1L;
        String justificativa = "Devolução do produto por defeito de fabricação";
        
        when(notaRepository.findById(notaId)).thenReturn(Optional.of(notaEmitida));
        when(sefazWebService.cancelarNFe(
                anyString(),
                anyString(),
                eq(justificativa)
        )).thenReturn(gerarRespostaCancelamentoSucesso());

        // Act
        NFeRetornoDTO resultado = nfeService.cancelar(notaId, justificativa);

        // Assert
        assertNotNull(resultado.getChaveAcesso());
        assertEquals(notaEmitida.getChaveAcesso(), resultado.getChaveAcesso());
    }

    // Métodos auxiliares para criação de dados de teste

    private Nota criarNotaEmitida() {
        Nota nota = new Nota();
        nota.setId(1L);
        nota.setNumero("123");
        nota.setSerie("1");
        nota.setTipo(TipoNota.VENDA);
        nota.setStatus(StatusNota.EMITIDA);
        nota.setChaveAcesso("35230211234567000001550010000123451234567890");
        nota.setProtocoloAutorizacao("352350001234567");
        nota.setDataEmissao(LocalDateTime.now().minusHours(2));
        nota.setValorTotal(BigDecimal.valueOf(1000.00));
        nota.setValorProdutos(BigDecimal.valueOf(1000.00));
        nota.setValorImpostosTotal(BigDecimal.ZERO);
        
        Empresa empresa = new Empresa();
        empresa.setId(1L);
        nota.setEmpresa(empresa);
        
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        nota.setCliente(cliente);
        
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        nota.setCreatedBy(usuario);
        
        return nota;
    }

    private Nota criarNotaRascunho() {
        Nota nota = criarNotaEmitida();
        nota.setId(2L);
        nota.setStatus(StatusNota.RASCUNHO);
        nota.setChaveAcesso(null);
        nota.setProtocoloAutorizacao(null);
        return nota;
    }

    private Nota criarNotaCancelada() {
        Nota nota = criarNotaEmitida();
        nota.setId(3L);
        nota.setStatus(StatusNota.CANCELADA);
        nota.setProtocoloCancelamento("352350001234568");
        nota.setJustificativaCancelamento("Cancelamento anterior");
        nota.setDataCancelamento(LocalDateTime.now().minusHours(12));
        return nota;
    }

    private String gerarRespostaCancelamentoSucesso() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<retCancNFe>" +
                "<infCanc>" +
                "<cStat>135</cStat>" +
                "<xMotivo>Cancelamento de NF-e autorizado</xMotivo>" +
                "<nProt>352350001234567</nProt>" +
                "</infCanc>" +
                "</retCancNFe>" +
                "</soap:Body>" +
                "</soap:Envelope>";
    }
}
