package com.jbx.econtract.controller;

import com.jbx.econtract.service.AIContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller für KI-gestützte Funktionen
 */
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "AI Features", description = "KI-gestützte Vertragsfunktionen")
@CrossOrigin(origins = "*")
public class AIController {

    private final AIContractService aiService;

    /**
     * Generiert Vertrag mit KI
     */
    @PostMapping("/generate-contract")
    @Operation(summary = "Vertrag mit KI generieren")
    public ResponseEntity<Map<String, String>> generateContract(
            @RequestBody Map<String, Object> parameters) {
        log.info("POST /api/v1/ai/generate-contract");
        
        String generatedText = aiService.generateContract(parameters);
        
        return ResponseEntity.ok(Map.of(
                "content", generatedText,
                "status", "success"
        ));
    }

    /**
     * Analysiert Vertragsrisiken
     */
    @PostMapping("/analyze-risks")
    @Operation(summary = "Vertragsrisiken analysieren")
    public ResponseEntity<Map<String, Object>> analyzeRisks(
            @RequestBody Map<String, String> request) {
        log.info("POST /api/v1/ai/analyze-risks");
        
        String contractText = request.get("contractText");
        Map<String, Object> analysis = aiService.analyzeContractRisks(contractText);
        
        return ResponseEntity.ok(analysis);
    }

    /**
     * Schlägt Klauseln vor
     */
    @GetMapping("/suggest-clauses")
    @Operation(summary = "Klauseln vorschlagen")
    public ResponseEntity<List<String>> suggestClauses(
            @RequestParam String contractType,
            @RequestParam(required = false) String context) {
        log.info("GET /api/v1/ai/suggest-clauses?contractType={}", contractType);
        
        List<String> clauses = aiService.suggestClauses(contractType, context);
        
        return ResponseEntity.ok(clauses);
    }
}

