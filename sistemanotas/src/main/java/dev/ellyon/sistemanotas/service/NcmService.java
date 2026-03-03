package dev.ellyon.sistemanotas.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ellyon.sistemanotas.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para consultar NCM usando API Brasil (brasilapi.com.br)
 * Fornece também sugestões de alíquotas de ICMS, PIS e COFINS
 *
 * API Endpoint: https://brasilapi.com.br/api/ncm/v1/{ncm}
 */
@Service
public class NcmService {
    private static final Logger logger = LoggerFactory.getLogger(NcmService.class);
    private static final String BRASIL_API_NCM_URL = "https://brasilapi.com.br/api/ncm/v1";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public NcmService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Consulta NCM usando API Brasil (brasilapi.com.br)
     * Endpoint: https://brasilapi.com.br/api/ncm/v1/{ncm}
     *
     * @param ncm Código NCM (8 dígitos)
     * @return Mapa com informações do NCM
     */
    public Map<String, Object> consultarNCM(String ncm) {
        Map<String, Object> resultado = new HashMap<>();

        try {
            String ncmLimpo = ncm.replaceAll("[^0-9]", "");

            if (ncmLimpo.length() != 8) {
                throw new BusinessException("NCM deve ter exatamente 8 dígitos");
            }

            logger.info("Consultando NCM: {} na API Brasil", ncmLimpo);

            // ✅ API Brasil (brasilapi.com.br) - Mais confiável e sem limite de requisições
            String url = BRASIL_API_NCM_URL + "/" + ncmLimpo;
            String response = restTemplate.getForObject(url, String.class);

            if (response == null) {
                logger.warn("NCM não encontrado na API Brasil: {}", ncmLimpo);
                throw new BusinessException("NCM não encontrado: " + ncmLimpo);
            }

            JsonNode root = objectMapper.readTree(response);

            // Verificar se houve erro na resposta
            if (root.has("status") && root.path("status").asInt() != 200) {
                logger.warn("Erro na API Brasil para NCM: {}", ncmLimpo);
                throw new BusinessException("NCM não encontrado: " + ncmLimpo);
            }

            // Extrair dados da resposta da API Brasil
            resultado.put("codigo", root.path("codigo").asText());
            resultado.put("descricao", root.path("descricao").asText());
            resultado.put("data_vigencia_inicio", root.path("data_vigencia_inicio").asText());
            resultado.put("data_vigencia_fim", root.path("data_vigencia_fim").asText());

            // Campos adicionais se disponíveis
            if (root.has("unidade_padrao")) {
                resultado.put("unidade_padrao", root.path("unidade_padrao").asText());
            }
            if (root.has("tipo")) {
                resultado.put("tipo", root.path("tipo").asText());
            }

            resultado.put("sucesso", true);
            logger.info("NCM consultado com sucesso: {} - {}", ncmLimpo, resultado.get("descricao"));
            return resultado;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao consultar NCM na API Brasil: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao consultar NCM: " + e.getMessage());
        }
    }

    /**
     * Busca alíquotas sugeridas baseado no NCM
     * Utiliza tabelas locais para maior confiabilidade
     *
     * @param ncm Código NCM (8 dígitos)
     * @return Mapa com alíquotas de ICMS, PIS e COFINS
     */
    public Map<String, BigDecimal> buscarAliquotasSugeridas(String ncm) {
        Map<String, BigDecimal> aliquotas = new HashMap<>();

        try {
            // Validar se NCM existe consultando a API Brasil
            consultarNCM(ncm);

            String ncmLimpo = ncm.replaceAll("[^0-9]", "");
            String capitulo = ncmLimpo.substring(0, 2);
            String posicao = ncmLimpo.substring(0, 4);

            logger.info("Calculando alíquotas para NCM: {} (Capítulo: {}, Posição: {})", ncmLimpo, capitulo, posicao);

            // ✅ ICMS baseado na tabela do RS (pode ajustar por estado)
            BigDecimal icms = calcularIcmsPorNCM(capitulo, posicao);
            aliquotas.put("icms", icms);

            // ✅ PIS/COFINS baseado no regime tributário
            Map<String, BigDecimal> pisCofins = calcularPisCofinsPorNCM(capitulo);
            aliquotas.put("pis", pisCofins.get("pis"));
            aliquotas.put("cofins", pisCofins.get("cofins"));

            logger.info("Alíquotas calculadas - ICMS: {}%, PIS: {}%, COFINS: {}",
                    icms, aliquotas.get("pis"), aliquotas.get("cofins"));

            return aliquotas;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao calcular alíquotas: {}", e.getMessage());
            throw new BusinessException("Erro ao calcular alíquotas: " + e.getMessage());
        }
    }

    /**
     * Tabela de ICMS por NCM (Rio Grande do Sul)
     * Fonte: RICMS/RS - Anexo II
     * Pode ser customizada por estado
     *
     * @param capitulo Primeiros 2 dígitos do NCM
     * @param posicao Primeiros 4 dígitos do NCM
     * @return Alíquota de ICMS sugerida
     */
    private BigDecimal calcularIcmsPorNCM(String capitulo, String posicao) {
        logger.debug("Calculando ICMS para capítulo: {}, posição: {}", capitulo, posicao);

        // Alimentos básicos - 12%
        if (capitulo.matches("01|02|03|04|07|08|09|10|11|12|15|16|17|18|19|20|21|22|23")) {
            return new BigDecimal("12.00");
        }

        // Medicamentos - 12%
        if (capitulo.equals("30")) {
            return new BigDecimal("12.00");
        }

        // Livros e jornais - ISENTO
        if (capitulo.equals("49")) {
            return BigDecimal.ZERO;
        }

        // Energia elétrica residencial - 12%
        if (posicao.equals("2716")) {
            return new BigDecimal("12.00");
        }

        // Combustíveis - 25% (substituição tributária)
        if (capitulo.equals("27")) {
            return new BigDecimal("25.00");
        }

        // Bebidas alcoólicas e cigarro - 25%
        if (capitulo.matches("22|24")) {
            return new BigDecimal("25.00");
        }

        // Cosméticos e perfumaria - 25%
        if (capitulo.equals("33")) {
            return new BigDecimal("25.00");
        }

        // Veículos - 12%
        if (capitulo.equals("87")) {
            return new BigDecimal("12.00");
        }

        // Padrão geral - 18%
        return new BigDecimal("18.00");
    }

    /**
     * Tabela de PIS/COFINS por NCM
     * Fonte: Receita Federal
     * Diferencia entre regime Cumulativo e Não-Cumulativo
     *
     * @param capitulo Primeiros 2 dígitos do NCM
     * @return Mapa com alíquotas de PIS e COFINS
     */
    private Map<String, BigDecimal> calcularPisCofinsPorNCM(String capitulo) {
        Map<String, BigDecimal> resultado = new HashMap<>();

        logger.debug("Calculando PIS/COFINS para capítulo: {}", capitulo);

        // Monofásico (combustíveis, bebidas, medicamentos)
        if (capitulo.matches("27|22|24|30|33")) {
            logger.debug("Produto monofásico detectado - aplicando alíquota zero");
            // Alíquota zero na saída (já tributado na entrada)
            resultado.put("pis", BigDecimal.ZERO);
            resultado.put("cofins", BigDecimal.ZERO);
            return resultado;
        }

        // Regime Cumulativo (Simples Nacional, Lucro Presumido)
        // PIS: 0,65% | COFINS: 3%
        boolean regimeCumulativo = false; // Ajustar conforme regime da empresa

        if (regimeCumulativo) {
            logger.debug("Aplicando regime cumulativo");
            resultado.put("pis", new BigDecimal("0.65"));
            resultado.put("cofins", new BigDecimal("3.00"));
        } else {
            // Regime Não-Cumulativo (Lucro Real)
            // PIS: 1,65% | COFINS: 7,6%
            logger.debug("Aplicando regime não-cumulativo");
            resultado.put("pis", new BigDecimal("1.65"));
            resultado.put("cofins", new BigDecimal("7.60"));
        }

        return resultado;
    }

    /**
     * Valida se NCM existe consultando a API Brasil
     *
     * @param ncm Código NCM (8 dígitos)
     * @return true se NCM é válido e encontrado, false caso contrário
     */
    public boolean validarNCM(String ncm) {
        try {
            consultarNCM(ncm);
            return true;
        } catch (Exception e) {
            logger.warn("NCM inválido ou não encontrado: {}", ncm);
            return false;
        }
    }
}