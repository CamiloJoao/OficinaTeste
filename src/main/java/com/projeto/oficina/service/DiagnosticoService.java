package com.projeto.oficina.service;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.JsonNode;
import com.projeto.oficina.model.Servico;

// Envia as informações técnicas da manutenção à Gemini API e devolve a sugestão de diagnóstico.
// Nunca envia dados do cliente (RNF11); a chave só vem da variável de ambiente GEMINI_API_KEY.
@Service
public class DiagnosticoService {

    private static final Logger log = LoggerFactory.getLogger(DiagnosticoService.class);

    // Instruções do Apêndice E (sem a frase sobre pesquisar) mais o pedido de texto simples
    private static final String INSTRUCOES_BASE = """
            Você é um assistente de diagnóstico para técnicos de uma oficina de manutenção de computadores. A partir da descrição do problema e dos dados do equipamento, pesquise em manuais de fabricantes, fóruns técnicos, blogs e vídeos e responda em português, neste formato:
            1. Causas prováveis (da mais para a menos provável), com breve justificativa
            2. Testes recomendados para confirmar cada causa
            3. Peças possivelmente envolvidas
            4. Nível de confiança (alto, médio ou baixo)
            Não invente informações. Se os dados forem insuficientes, diga quais informações faltam.
            Escreva em texto simples, sem formatação markdown (sem asteriscos nem cerquilhas).""";

    // Acrescentado quando a pesquisa no Google está ligada
    private static final String COM_PESQUISA = """
            Sempre pesquise na web antes de responder, priorizando manuais e páginas de suporte do fabricante, fóruns técnicos e vídeos de reparo. Nunca responda apenas com conhecimento próprio.""";

    // Acrescentado quando a pesquisa está desligada (plano gratuito)
    private static final String SEM_PESQUISA = """
            Nesta consulta você não tem acesso à internet, então desconsidere o pedido de pesquisa acima. Responda com base no seu conhecimento, deixe claro quando não tiver certeza e não cite links, páginas ou vídeos, porque eles não poderão ser conferidos.""";

    @Value("${oficina.ia.chave}")
    private String chave;

    @Value("${oficina.ia.modelo}")
    private String modelo;

    @Value("${oficina.ia.tempo-limite-segundos}")
    private int tempoLimiteSegundos;

    @Value("${oficina.ia.pesquisa}")
    private boolean pesquisa;

    private RestClient restClient;

    // Cria o cliente HTTP uma vez, depois que o Spring preencheu os @Value
    @PostConstruct
    public void iniciar() {
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofSeconds(10));
        fabrica.setReadTimeout(Duration.ofSeconds(tempoLimiteSegundos));

        restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .requestFactory(fabrica)
                .build();
    }

    public boolean configurado() {
        return !chave.isBlank();
    }

    public SugestaoDiagnostico gerar(Servico servico) {
        if (!configurado()) {
            throw new DiagnosticoException("O apoio ao diagnóstico não está configurado neste computador.");
        }

        String instrucoes;
        if (pesquisa) {
            instrucoes = INSTRUCOES_BASE + "\n" + COM_PESQUISA;
        } else {
            instrucoes = INSTRUCOES_BASE + "\n" + SEM_PESQUISA;
        }

        String descricao = montarDescricao(servico);

        // Corpo do generateContent: cada Map.of vira { } e cada List.of vira [ ] no JSON
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("system_instruction", Map.of("parts", List.of(Map.of("text", instrucoes))));
        corpo.put("contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", descricao)))));
        if (pesquisa) {
            corpo.put("tools", List.of(Map.of("google_search", Map.of())));
        }

        long inicio = System.currentTimeMillis();
        JsonNode resposta = enviar(corpo);
        long segundos = (System.currentTimeMillis() - inicio) / 1000;

        String texto = lerTexto(resposta);
        if (texto.isBlank()) {
            throw new DiagnosticoException("A IA não retornou uma sugestão. Revise a descrição e tente novamente.");
        }

        // Texto, links e HTML vão como vieram: os termos do Google não permitem alterar o resultado.
        // O groundingMetadata só vem quando a pesquisa no Google foi usada.
        JsonNode dadosPesquisa = resposta.path("candidates").path(0).path("groundingMetadata");
        List<FonteDiagnostico> fontes = lerFontes(dadosPesquisa);

        String sugestoesPesquisa = null;
        JsonNode html = dadosPesquisa.path("searchEntryPoint").path("renderedContent");
        if (html.isTextual()) {
            sugestoesPesquisa = html.asText();
        }

        return new SugestaoDiagnostico(texto, fontes, sugestoesPesquisa, segundos);
    }

    // Envia o corpo à Gemini API e troca os erros técnicos por mensagens para o usuário.
    // O log não mostra a chave: ela vai no cabeçalho, e não na URL.
    private JsonNode enviar(Map<String, Object> corpo) {
        try {
            return restClient.post()
                    .uri("/models/{modelo}:generateContent", modelo)
                    .header("x-goog-api-key", chave)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(corpo)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (ResourceAccessException e) {
            // Tempo limite ou sem internet
            log.warn("A IA não respondeu: {}", e.getMessage());
            throw new DiagnosticoException("O serviço de IA não respondeu a tempo. Tente novamente em alguns minutos.");
        } catch (HttpClientErrorException e) {
            log.warn("A IA recusou a consulta: {}", e.getMessage());
            int status = e.getStatusCode().value();
            if (status == 429) {
                String mensagem = "O limite de uso da IA foi atingido. Tente mais tarde.";
                if (pesquisa) {
                    mensagem += " Se a chave for do plano gratuito, a pesquisa no Google não está liberada.";
                }
                throw new DiagnosticoException(mensagem);
            }
            if (status == 400 || status == 401 || status == 403) {
                throw new DiagnosticoException("A chave da IA é inválida ou não tem permissão para a pesquisa.");
            }
            throw new DiagnosticoException("Não foi possível gerar a sugestão agora. Tente novamente.");
        } catch (RestClientException e) {
            log.warn("Erro ao consultar a IA: {}", e.getMessage());
            // No POST, o tempo limite de leitura chega aqui (e não como ResourceAccessException)
            if (e.contains(SocketTimeoutException.class)) {
                throw new DiagnosticoException("O serviço de IA não respondeu a tempo. Tente novamente em alguns minutos.");
            }
            throw new DiagnosticoException("Não foi possível gerar a sugestão agora. Tente novamente.");
        }
    }

    // Texto no formato da avaliação da monografia: só dados do equipamento e do problema,
    // nunca do cliente (RNF11)
    private String montarDescricao(Servico s) {
        String texto = "Equipamento: " + valorOuNaoInformado(s.getEquipamento()) + "\n"
                + "Marca/modelo: " + valorOuNaoInformado(s.getMarcaModelo()) + "\n"
                + "Sintomas: " + valorOuNaoInformado(s.getDescricaoProblema()) + "\n"
                + "O que já foi testado: " + valorOuNaoInformado(s.getTestesRealizados());
        log.debug("Texto enviado à IA:\n{}", texto);
        return texto;
    }

    private String valorOuNaoInformado(String valor) {
        if (valor == null) {
            return "não informado";
        }
        return valor;
    }

    // Junta o texto de todas as partes da resposta, menos as de raciocínio ("thought": true)
    private String lerTexto(JsonNode resposta) {
        String texto = "";
        // Resposta sem corpo
        if (resposta == null) {
            return texto;
        }
        for (JsonNode parte : resposta.path("candidates").path(0).path("content").path("parts")) {
            if (!parte.path("thought").asBoolean()) {
                texto += parte.path("text").asText();
            }
        }
        return texto;
    }

    // Uma fonte para cada página que a pesquisa no Google usou (pula as que vierem sem link)
    private List<FonteDiagnostico> lerFontes(JsonNode dadosPesquisa) {
        List<FonteDiagnostico> fontes = new ArrayList<>();
        for (JsonNode trecho : dadosPesquisa.path("groundingChunks")) {
            String url = trecho.path("web").path("uri").asText("");
            if (!url.isEmpty()) {
                String titulo = trecho.path("web").path("title").asText("");
                fontes.add(new FonteDiagnostico(titulo, url));
            }
        }
        return fontes;
    }
}
