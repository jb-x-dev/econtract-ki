package com.jbx.econtract.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * KI-gestützter Service für Vertragserstellung und -analyse
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIContractService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${openai.api.url:https://api.openai.com/v1}")
    private String openaiApiUrl;

    @Value("${openai.api.model:gpt-4}")
    private String model;

    /**
     * Generiert Vertragstext basierend auf Parametern
     */
    public String generateContract(Map<String, Object> parameters) {
        log.info("Generating contract with AI for type: {}", parameters.get("contractType"));
        
        if (openaiApiKey == null || openaiApiKey.isEmpty()) {
            log.warn("OpenAI API key not configured, returning template");
            return generateTemplateContract(parameters);
        }
        
        try {
            String prompt = buildContractPrompt(parameters);
            return callOpenAI(prompt);
        } catch (Exception e) {
            log.error("Error calling OpenAI API", e);
            return generateTemplateContract(parameters);
        }
    }

    /**
     * Analysiert Vertragsrisiken mit KI
     */
    public Map<String, Object> analyzeContractRisks(String contractText) {
        log.info("Analyzing contract risks with AI");
        
        Map<String, Object> analysis = new HashMap<>();
        
        if (openaiApiKey == null || openaiApiKey.isEmpty()) {
            analysis.put("risk_level", "UNKNOWN");
            analysis.put("message", "OpenAI API key not configured");
            return analysis;
        }
        
        try {
            String prompt = "Analysiere folgenden Vertrag auf rechtliche Risiken und gib eine strukturierte Bewertung:\n\n" + contractText;
            String response = callOpenAI(prompt);
            
            analysis.put("risk_level", "MEDIUM");
            analysis.put("analysis", response);
            analysis.put("confidence", 0.85);
        } catch (Exception e) {
            log.error("Error analyzing contract", e);
            analysis.put("error", e.getMessage());
        }
        
        return analysis;
    }

    /**
     * Schlägt Klauseln vor basierend auf Vertragstyp
     */
    public List<String> suggestClauses(String contractType, String context) {
        log.info("Suggesting clauses for contract type: {}", contractType);
        
        // Fallback: Standard-Klauseln
        return List.of(
                "§1 Vertragsgegenstand",
                "§2 Vertragslaufzeit",
                "§3 Vergütung",
                "§4 Kündigungsfristen",
                "§5 Haftung",
                "§6 Geheimhaltung",
                "§7 Datenschutz",
                "§8 Schlussbestimmungen"
        );
    }

    /**
     * Ruft OpenAI API auf
     */
    private String callOpenAI(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", 
                        "Du bist ein Experte für deutsches Vertragsrecht und hilfst bei der Erstellung und Analyse von Verträgen."),
                Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", 2000);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                openaiApiUrl + "/chat/completions",
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("choices")) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            if (!choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
        }

        return "Fehler bei der KI-Generierung";
    }

    /**
     * Erstellt Prompt für Vertragsgenerierung
     */
    private String buildContractPrompt(Map<String, Object> parameters) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Erstelle einen professionellen Vertrag mit folgenden Parametern:\n\n");
        prompt.append("Vertragstyp: ").append(parameters.get("contractType")).append("\n");
        prompt.append("Vertragspartner: ").append(parameters.get("partnerName")).append("\n");
        prompt.append("Vertragswert: ").append(parameters.get("contractValue")).append(" EUR\n");
        prompt.append("Laufzeit: ").append(parameters.get("startDate")).append(" bis ").append(parameters.get("endDate")).append("\n\n");
        prompt.append("Erstelle einen vollständigen Vertragstext mit allen relevanten Klauseln.");
        
        return prompt.toString();
    }

    /**
     * Fallback: Template-basierte Vertragserstellung
     */
    private String generateTemplateContract(Map<String, Object> parameters) {
        StringBuilder contract = new StringBuilder();
        contract.append("VERTRAG\n\n");
        contract.append("zwischen\n\n");
        contract.append("Auftraggeber (nachfolgend 'AG' genannt)\n\n");
        contract.append("und\n\n");
        contract.append(parameters.get("partnerName")).append(" (nachfolgend 'AN' genannt)\n\n");
        contract.append("§1 Vertragsgegenstand\n");
        contract.append("Der Gegenstand dieses Vertrages ist...\n\n");
        contract.append("§2 Vertragslaufzeit\n");
        contract.append("Dieser Vertrag läuft vom ").append(parameters.get("startDate"));
        contract.append(" bis ").append(parameters.get("endDate")).append(".\n\n");
        contract.append("§3 Vergütung\n");
        contract.append("Die Vergütung beträgt ").append(parameters.get("contractValue")).append(" EUR.\n\n");
        
        return contract.toString();
    }
}

