package com.jbx.econtract.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI Extraction Service
 * Uses OpenAI GPT-4.1-mini to extract structured data from contract documents
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIExtractionService {
    
    @Value("${OPENAI_API_KEY:}")
    private String openaiApiKey;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4.1-mini";
    
    /**
     * Extract text from file based on mime type
     */
    public String extractTextFromFile(File file, String mimeType) throws Exception {
        log.info("Extracting text from file: {} ({})", file.getName(), mimeType);
        
        if (mimeType.contains("pdf")) {
            return extractTextFromPDF(file);
        } else if (mimeType.contains("word") || mimeType.contains("document")) {
            return extractTextFromWord(file);
        } else if (mimeType.contains("text")) {
            return extractTextFromPlainText(file);
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + mimeType);
        }
    }
    
    /**
     * Extract text from PDF
     */
    private String extractTextFromPDF(File file) throws Exception {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            log.info("Extracted {} characters from PDF", text.length());
            return text;
        }
    }
    
    /**
     * Extract text from Word document
     */
    private String extractTextFromWord(File file) throws Exception {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {
            StringBuilder text = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                text.append(paragraph.getText()).append("\n");
            }
            log.info("Extracted {} characters from Word document", text.length());
            return text.toString();
        }
    }
    
    /**
     * Extract text from plain text file
     */
    private String extractTextFromPlainText(File file) throws Exception {
        return new String(java.nio.file.Files.readAllBytes(file.toPath()));
    }
    
    /**
     * Extract structured contract data using OpenAI
     */
    public Map<String, Object> extractContractData(String contractText) throws Exception {
        log.info("Extracting contract data using OpenAI GPT-4.1-mini");
        
        String prompt = buildExtractionPrompt(contractText);
        
        // Build OpenAI API request
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL);
        requestBody.put("messages", List.of(
            Map.of("role", "system", "content", "Du bist ein Experte für Vertragsanalyse. Extrahiere strukturierte Daten aus Verträgen im JSON-Format."),
            Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("temperature", 0.1);
        requestBody.put("max_tokens", 2000);
        
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        // Call OpenAI API
        ResponseEntity<String> response = restTemplate.exchange(
            OPENAI_API_URL,
            HttpMethod.POST,
            request,
            String.class
        );
        
        // Parse response
        JsonNode responseJson = objectMapper.readTree(response.getBody());
        String content = responseJson.get("choices").get(0).get("message").get("content").asText();
        
        // Extract JSON from response (remove markdown code blocks if present)
        content = content.replaceAll("```json\\s*", "").replaceAll("```\\s*$", "").trim();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> extractedData = objectMapper.readValue(content, Map.class);
        
        log.info("Successfully extracted contract data: {}", extractedData.keySet());
        return extractedData;
    }
    
    /**
     * Build extraction prompt
     */
    private String buildExtractionPrompt(String contractText) {
        return String.format("""
            Analysiere den folgenden Vertrag und extrahiere die wichtigsten Informationen im JSON-Format.
            
            Extrahiere folgende Felder:
            - contractNumber: Vertragsnummer (falls vorhanden, sonst null)
            - title: Vertragsbezeichnung/Titel
            - contractType: Vertragsart (z.B. "Dienstleistungsvertrag", "Lieferantenvertrag", "NDA", etc.)
            - partnerName: Name des Vertragspartners
            - startDate: Vertragsbeginn (Format: YYYY-MM-DD)
            - endDate: Vertragsende (Format: YYYY-MM-DD, null bei unbefristet)
            - contractValue: Gesamtvertragswert in Euro (nur Zahl, ohne Währung)
            - currency: Währung (z.B. "EUR", "USD")
            - noticePeriodDays: Kündigungsfrist in Tagen
            - autoRenewal: Automatische Verlängerung (true/false)
            - billingCycle: Abrechnungszyklus ("MONTHLY", "QUARTERLY", "YEARLY", "ONE_TIME")
            - billingAmount: Abrechnungsbetrag pro Zyklus
            - billingStartDate: Erster Abrechnungstermin (Format: YYYY-MM-DD)
            - paymentTermDays: Zahlungsziel in Tagen (Standard: 30)
            - department: Zuständige Abteilung (falls erwähnt)
            - notes: Wichtige Zusatzinformationen
            
            Vertrag:
            %s
            
            Antworte NUR mit einem validen JSON-Objekt, ohne zusätzliche Erklärungen.
            """, contractText);
    }
}
