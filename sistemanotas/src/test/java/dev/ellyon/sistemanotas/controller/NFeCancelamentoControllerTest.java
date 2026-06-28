package dev.ellyon.sistemanotas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ellyon.sistemanotas.nfe.dto.CancelamentoNFeDTORequest;
import dev.ellyon.sistemanotas.nfe.dto.NFeResponseDTO;
import dev.ellyon.sistemanotas.nfe.service.NFeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(controllers = NFeController.class)
@DisplayName("Testes do Controlador de Cancelamento de NF-e")
class NFeCancelamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NFeService nfeService;

    @MockitoBean
    private dev.ellyon.sistemanotas.nfe.service.ChaveAcessoService chaveAcessoService;

    @MockitoBean
    private dev.ellyon.sistemanotas.nfe.service.CertificadoService certificadoService;

    @MockitoBean
    private dev.ellyon.sistemanotas.nfe.service.AssinaturaDigitalService assinaturaService;

    @MockitoBean
    private dev.ellyon.sistemanotas.nfe.xml.NFeXmlGenerator xmlGenerator;

    @MockitoBean
    private dev.ellyon.sistemanotas.repository.NotaRepository notaRepository;

    @MockitoBean
    private dev.ellyon.sistemanotas.nfe.config.NFeConfig nfeConfig;

    private CancelamentoNFeDTORequest cancelamentoRequest;
    private NFeResponseDTO nfeRetorno;

    @BeforeEach
    void setUp() {
        cancelamentoRequest = new CancelamentoNFeDTORequest();
        cancelamentoRequest.setJustificativa("Devolução do produto por defeito de fabricação");

        nfeRetorno = new NFeResponseDTO();
        nfeRetorno.setCodigoStatus("135");
        nfeRetorno.setMensagem("Cancelamento de NF-e autorizado");
        nfeRetorno.setProtocolo("352350001234567");
        nfeRetorno.setChaveAcesso("35230211234567000001550010000123451234567890");
    }

    @Test
    @DisplayName("Deve retornar 200 ao cancelar NF-e com sucesso")
    void testCancelarComSucesso() throws Exception {
        // Arrange
        when(nfeService.cancelar(anyLong(), anyString())).thenReturn(nfeRetorno);

        // Act
        ResultActions result = mockMvc.perform(post("/api/v1/nfe/cancelar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelamentoRequest)));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("NF-e cancelada com sucesso!")))
                .andExpect(jsonPath("$.data.codigoStatus", is("135")))
                .andExpect(jsonPath("$.data.protocolo", is("352350001234567")));
    }

    @Test
    @DisplayName("Deve rejeitar requisição com justificativa vazia")
    void testCancelarComJustificativaVazia() throws Exception {
        // Arrange
        CancelamentoNFeDTORequest invalidRequest = new CancelamentoNFeDTORequest();
        invalidRequest.setJustificativa("");

        // Act
        ResultActions result = mockMvc.perform(post("/api/v1/nfe/cancelar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)));

        // Assert
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve rejeitar requisição com justificativa menor que 15 caracteres")
    void testCancelarComJustificativaInsuficiente() throws Exception {
        // Arrange
        CancelamentoNFeDTORequest invalidRequest = new CancelamentoNFeDTORequest();
        invalidRequest.setJustificativa("Motivo curto");

        // Act
        ResultActions result = mockMvc.perform(post("/api/v1/nfe/cancelar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)));

        // Assert
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve rejeitar requisição sem justificativa")
    void testCancelarSemJustificativa() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(post("/api/v1/nfe/cancelar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));

        // Assert
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar erro quando NF-e não encontrada")
    void testCancelarNFenaoEncontrada() throws Exception {
        // Arrange
        when(nfeService.cancelar(anyLong(), anyString()))
                .thenThrow(new RuntimeException("Nota não encontrada"));

        // Act
        ResultActions result = mockMvc.perform(post("/api/v1/nfe/cancelar/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelamentoRequest)));

        // Assert
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Erro ao cancelar NF-e")));
    }

    @Test
    @DisplayName("Deve incluir timestamp na resposta")
    void testRespostaContemTimestamp() throws Exception {
        // Arrange
        when(nfeService.cancelar(anyLong(), anyString())).thenReturn(nfeRetorno);

        // Act
        ResultActions result = mockMvc.perform(post("/api/v1/nfe/cancelar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelamentoRequest)));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Deve retornar dados completos do cancelamento")
    void testRespostaComDadosCompletos() throws Exception {
        // Arrange
        when(nfeService.cancelar(anyLong(), anyString())).thenReturn(nfeRetorno);

        // Act
        ResultActions result = mockMvc.perform(post("/api/v1/nfe/cancelar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelamentoRequest)));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.codigoStatus").exists())
                .andExpect(jsonPath("$.data.mensagem").exists())
                .andExpect(jsonPath("$.data.protocolo").exists())
                .andExpect(jsonPath("$.data.chaveAcesso").exists());
    }
}
