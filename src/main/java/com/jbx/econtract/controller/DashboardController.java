package com.jbx.econtract.controller;

import com.jbx.econtract.service.ContractService;
import com.jbx.econtract.repository.ContractRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dashboard Controller für erweiterte Statistiken und Analysen
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard", description = "Dashboard und Statistiken")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final ContractService contractService;
    private final ContractRepository contractRepository;

    /**
     * Erweiterte Dashboard-Statistiken
     */
    @GetMapping("/stats")
    @Operation(summary = "Erweiterte Dashboard-Statistiken")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        log.info("GET /api/v1/dashboard/stats");
        
        Map<String, Object> stats = new HashMap<>();
        
        // Basis-Statistiken
        stats.put("total_contracts", contractRepository.count());
        stats.put("draft", contractService.countByStatus("DRAFT"));
        stats.put("in_approval", contractService.countByStatus("IN_APPROVAL"));
        stats.put("active", contractService.countByStatus("ACTIVE"));
        stats.put("expired", contractService.countByStatus("EXPIRED"));
        
        // Verträge nach Typ (deutsche Bezeichnungen aus Sample Data)
        Map<String, Long> byType = new HashMap<>();
        byType.put("Lieferantenvertrag", contractRepository.countByContractType("Lieferantenvertrag"));
        byType.put("Kundenvertrag", contractRepository.countByContractType("Kundenvertrag"));
        byType.put("Dienstleistungsvertrag", contractRepository.countByContractType("Dienstleistungsvertrag"));
        byType.put("NDA", contractRepository.countByContractType("NDA"));
        byType.put("Projektvertrag", contractRepository.countByContractType("Projektvertrag"));
        byType.put("Rahmenvertrag", contractRepository.countByContractType("Rahmenvertrag"));
        stats.put("by_type", byType);
        
        // Ablaufende Verträge (nächste 30 Tage)
        LocalDate today = LocalDate.now();
        LocalDate in30Days = today.plusDays(30);
        int expiringCount = contractRepository.findExpiringContracts(today, in30Days).size();
        stats.put("expiring_soon", expiringCount);
        
        // Gesamtwert aller aktiven Verträge
        // TODO: Implementierung mit SQL Aggregation
        stats.put("total_value", 0);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Vertrags-Trend (letzte 12 Monate)
     */
    @GetMapping("/trend")
    @Operation(summary = "Vertrags-Trend der letzten 12 Monate")
    public ResponseEntity<List<Map<String, Object>>> getContractTrend() {
        log.info("GET /api/v1/dashboard/trend");
        
        List<Map<String, Object>> trend = new ArrayList<>();
        
        // TODO: Implementierung mit SQL GROUP BY
        for (int i = 11; i >= 0; i--) {
            Map<String, Object> month = new HashMap<>();
            LocalDate date = LocalDate.now().minusMonths(i);
            month.put("month", date.getMonth().toString());
            month.put("year", date.getYear());
            month.put("count", 0);
            trend.add(month);
        }
        
        return ResponseEntity.ok(trend);
    }

    /**
     * Top Partner nach Vertragswert
     */
    @GetMapping("/top-partners")
    @Operation(summary = "Top 10 Partner nach Vertragswert")
    public ResponseEntity<List<Map<String, Object>>> getTopPartners() {
        log.info("GET /api/v1/dashboard/top-partners");
        
        List<Map<String, Object>> topPartners = new ArrayList<>();
        
        // TODO: Implementierung mit SQL Aggregation
        
        return ResponseEntity.ok(topPartners);
    }

    /**
     * Ablaufende Verträge
     */
    @GetMapping("/expiring")
    @Operation(summary = "Verträge die bald ablaufen")
    public ResponseEntity<List<Map<String, Object>>> getExpiringContracts(
            @RequestParam(defaultValue = "30") int days) {
        log.info("GET /api/v1/dashboard/expiring?days={}", days);
        
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        
        var contracts = contractRepository.findExpiringContracts(today, futureDate);
        
        List<Map<String, Object>> result = contracts.stream()
                .map(contract -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", contract.getId());
                    map.put("contractNumber", contract.getContractNumber());
                    map.put("title", contract.getTitle());
                    map.put("partnerName", contract.getPartnerName());
                    map.put("endDate", contract.getEndDate());
                    map.put("daysRemaining", java.time.temporal.ChronoUnit.DAYS.between(today, contract.getEndDate()));
                    return map;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }

    /**
     * Vertrags-Wert Übersicht
     */
    @GetMapping("/value-overview")
    @Operation(summary = "Übersicht über Vertragswerte")
    public ResponseEntity<Map<String, Object>> getValueOverview() {
        log.info("GET /api/v1/dashboard/value-overview");
        
        Map<String, Object> overview = new HashMap<>();
        
        // TODO: SQL Aggregation für Summen
        overview.put("total_value", BigDecimal.ZERO);
        overview.put("active_value", BigDecimal.ZERO);
        overview.put("draft_value", BigDecimal.ZERO);
        overview.put("average_value", BigDecimal.ZERO);
        
        return ResponseEntity.ok(overview);
    }
}

