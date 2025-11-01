package com.jbx.econtract.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/v1/maintenance")
@Tag(name = "Contract Maintenance", description = "Vertragspflege und Wartung")
public class MaintenanceController {
    
    @GetMapping("/tasks")
    @Operation(summary = "Alle Wartungsaufgaben")
    public ResponseEntity<List<Map<String, Object>>> getAllMaintenanceTasks() {
        List<Map<String, Object>> tasks = new ArrayList<>();
        
        // Demo-Daten
        tasks.add(createTask(1L, "Vertragsprüfung", "CON-2025-000001", "REVIEW", "SCHEDULED", LocalDate.now().plusDays(7)));
        tasks.add(createTask(2L, "Verlängerung vorbereiten", "CON-2025-000002", "RENEWAL", "IN_PROGRESS", LocalDate.now().plusDays(14)));
        tasks.add(createTask(3L, "Audit durchführen", "FW-2025-000001", "AUDIT", "SCHEDULED", LocalDate.now().plusDays(30)));
        
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/tasks/upcoming")
    @Operation(summary = "Anstehende Wartungsaufgaben")
    public ResponseEntity<List<Map<String, Object>>> getUpcomingTasks(@RequestParam(defaultValue = "30") int days) {
        List<Map<String, Object>> tasks = new ArrayList<>();
        
        tasks.add(createTask(1L, "Vertragsprüfung", "CON-2025-000001", "REVIEW", "SCHEDULED", LocalDate.now().plusDays(7)));
        tasks.add(createTask(2L, "Verlängerung vorbereiten", "CON-2025-000002", "RENEWAL", "IN_PROGRESS", LocalDate.now().plusDays(14)));
        
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/tasks/overdue")
    @Operation(summary = "Überfällige Wartungsaufgaben")
    public ResponseEntity<List<Map<String, Object>>> getOverdueTasks() {
        List<Map<String, Object>> tasks = new ArrayList<>();
        
        tasks.add(createTask(4L, "Vertrag aktualisieren", "CON-2025-000003", "UPDATE", "SCHEDULED", LocalDate.now().minusDays(3)));
        
        return ResponseEntity.ok(tasks);
    }
    
    @PostMapping("/tasks")
    @Operation(summary = "Neue Wartungsaufgabe erstellen")
    public ResponseEntity<Map<String, Object>> createMaintenanceTask(@RequestBody Map<String, Object> task) {
        task.put("id", System.currentTimeMillis());
        task.put("status", "SCHEDULED");
        task.put("createdAt", LocalDate.now().toString());
        return ResponseEntity.ok(task);
    }
    
    @PutMapping("/tasks/{id}")
    @Operation(summary = "Wartungsaufgabe aktualisieren")
    public ResponseEntity<Map<String, Object>> updateMaintenanceTask(
            @PathVariable Long id,
            @RequestBody Map<String, Object> task) {
        task.put("id", id);
        task.put("updatedAt", LocalDate.now().toString());
        return ResponseEntity.ok(task);
    }
    
    @PostMapping("/tasks/{id}/complete")
    @Operation(summary = "Wartungsaufgabe abschließen")
    public ResponseEntity<Map<String, Object>> completeTask(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("status", "COMPLETED");
        result.put("completedAt", LocalDate.now().toString());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Wartungs-Statistiken")
    public ResponseEntity<Map<String, Object>> getMaintenanceStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", 15);
        stats.put("scheduled", 8);
        stats.put("inProgress", 4);
        stats.put("completed", 2);
        stats.put("overdue", 1);
        return ResponseEntity.ok(stats);
    }
    
    private Map<String, Object> createTask(Long id, String title, String contractNumber, 
                                          String type, String status, LocalDate scheduledDate) {
        Map<String, Object> task = new HashMap<>();
        task.put("id", id);
        task.put("title", title);
        task.put("contractNumber", contractNumber);
        task.put("maintenanceType", type);
        task.put("status", status);
        task.put("scheduledDate", scheduledDate.toString());
        task.put("assignedTo", "Max Mustermann");
        return task;
    }
}

