package com.totvs.automacao.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChatController {

    @Value("${gemini.api.keys}")
    private String[] apiKeys;

    private int keyIndex = 0;

    private synchronized String getNextApiKey() {
        if (apiKeys == null || apiKeys.length == 0) {
            throw new IllegalStateException("Nenhuma chave configurada.");
        }
        String key = apiKeys[keyIndex].trim();
        keyIndex = (keyIndex + 1) % apiKeys.length;
        return key;
    }

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/analisar-chat")
    public ResponseEntity<String> analisarChat(@RequestBody Map<String, String> payload) {
        String conversaChat = payload.get("conversa");

        if (conversaChat == null || conversaChat.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Conversa não enviada.");
        }

        String url = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + getNextApiKey();

        String systemPrompt = "Você é um assistente de CRM de pré-vendas. Analise o histórico de chat fornecido e extraia as informações estritamente no formato JSON abaixo. "
                + "Não adicione nenhuma introdução, explicação ou bloco de código em markdown (como ```json). Devolva APENAS o objeto JSON limpo.\n\n"
                + "Formato esperado:\n"
                + "{\n"
                + "  \"razaoSocial\": \"\",\n"
                + "  \"cnpj\": \"\",\n"
                + "  \"nome\": \"\",\n"
                + "  \"cargo\": \"\",\n"
                + "  \"telefone\": \"\",\n"
                + "  \"email\": \"\",\n"
                + "  \"segmento\": \"\",\n"
                + "  \"colaboradores\": \"\",\n"
                + "  \"faturamento\": \"\",\n"
                + "  \"necessidade\": \"\",\n"
                + "  \"sistemaAtual\": \"\"\n"
                + "}\n\n"
                + "Histórico do Chat:\n" + conversaChat;

        Map<String, Object> textMap = new HashMap<>();
        textMap.put("text", systemPrompt);

        Map<String, Object> partsMap = new HashMap<>();
        partsMap.put("parts", List.of(textMap));

        Map<String, Object> contentsMap = new HashMap<>();
        contentsMap.put("contents", List.of(partsMap));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(contentsMap, headers);

        try {
            System.out.println("Enviando requisição para o Gemini...");
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            System.out.println("Resposta recebida do Google com sucesso!");

            List candidates = (List) response.getBody().get("candidates");
            Map firstCandidate = (Map) candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List parts = (List) content.get("parts");
            Map firstPart = (Map) parts.get(0);
            String textoJsonDaIA = (String) firstPart.get("text");

            return ResponseEntity.ok(textoJsonDaIA.trim());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Se o Google devolver erro (400, 403, 429), esse bloco captura e mostra o motivo real
            System.err.println("❌ ERRO DA API DO GOOGLE GEMINI:");
            System.err.println("Status Code: " + e.getStatusCode());
            System.err.println("Corpo do Erro: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());

        } catch (Exception e) {
            // Captura erros de código (ex: mapeamento do JSON quebrado)
            System.err.println("❌ ERRO INTERNO NO CODIGO JAVA:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno: " + e.getMessage());
        }
    }
}
