package com.jbx.econtract.controller;

import com.jbx.econtract.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller für Workflow-Management
 */
@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Workflow Management", description = "Genehmigungsworkflows")
@CrossOrigin(origins = "*")
public class WorkflowController {

    private final WorkflowService workflowService;

    /**
     * Startet Workflow für Vertrag
     */
    @PostMapping("/{contractId}/start")
    @Operation(summary = "Workflow starten")
    public ResponseEntity<Map<String, Object>> startWorkflow(@PathVariable Long contractId) {
        log.info("POST /api/v1/workflows/{}/start", contractId);
        
        try {
            workflowService.startApprovalWorkflow(contractId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Workflow gestartet"
            ));
        } catch (Exception e) {
            log.error("Error starting workflow", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Genehmigt Workflow-Schritt
     */
    @PostMapping("/{contractId}/approve")
    @Operation(summary = "Workflow-Schritt genehmigen")
    public ResponseEntity<Map<String, Object>> approveStep(
            @PathVariable Long contractId,
            @RequestBody Map<String, Object> request) {
        log.info("POST /api/v1/workflows/{}/approve", contractId);
        
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            String comment = (String) request.get("comment");
            
            workflowService.approveStep(contractId, userId, comment);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Schritt genehmigt"
            ));
        } catch (Exception e) {
            log.error("Error approving step", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Lehnt Workflow-Schritt ab
     */
    @PostMapping("/{contractId}/reject")
    @Operation(summary = "Workflow-Schritt ablehnen")
    public ResponseEntity<Map<String, Object>> rejectStep(
            @PathVariable Long contractId,
            @RequestBody Map<String, Object> request) {
        log.info("POST /api/v1/workflows/{}/reject", contractId);
        
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            String reason = (String) request.get("reason");
            
            workflowService.rejectStep(contractId, userId, reason);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Schritt abgelehnt"
            ));
        } catch (Exception e) {
            log.error("Error rejecting step", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}

