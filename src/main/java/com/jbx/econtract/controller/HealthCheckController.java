package com.jbx.econtract.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller für Monitoring und Keep-Alive
 */
@RestController
@RequestMapping("/api/public")
@Slf4j
public class HealthCheckController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static int requestCount = 0;
    private static LocalDateTime lastCheck = LocalDateTime.now();

    /**
     * Health Check Endpoint
     * Wird von UptimeRobot alle 5 Minuten aufgerufen
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        requestCount++;
        LocalDateTime now = LocalDateTime.now();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", now.format(FORMATTER));
        response.put("service", "eContract KI");
        response.put("version", "1.0.0");
        response.put("uptime_checks", requestCount);
        response.put("last_check", lastCheck.format(FORMATTER));
        
        lastCheck = now;
        
        // Log nur jede 10. Anfrage (reduziert Log-Spam)
        if (requestCount % 10 == 0) {
            log.info("Health check #{} - Service is UP", requestCount);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Ping Endpoint (minimale Response)
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    /**
     * Status Endpoint (detaillierte Informationen)
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().format(FORMATTER));
        response.put("service", "eContract KI - Intelligente Vertragsverwaltung");
        response.put("version", "1.0.0");
        response.put("environment", "Production");
        response.put("database", "PostgreSQL");
        response.put("uptime_checks", requestCount);
        response.put("java_version", System.getProperty("java.version"));
        response.put("os", System.getProperty("os.name"));
        
        // Memory Info
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        long usedMemory = totalMemory - freeMemory;
        
        Map<String, Object> memory = new HashMap<>();
        memory.put("total_mb", totalMemory);
        memory.put("used_mb", usedMemory);
        memory.put("free_mb", freeMemory);
        response.put("memory", memory);
        
        log.info("Status check - Memory: {}MB used / {}MB total", usedMemory, totalMemory);
        
        return ResponseEntity.ok(response);
    }
}
