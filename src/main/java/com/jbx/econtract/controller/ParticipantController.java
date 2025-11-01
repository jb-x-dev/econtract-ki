package com.jbx.econtract.controller;

import com.jbx.econtract.model.entity.ContractParticipant;
import com.jbx.econtract.service.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/participants")
@RequiredArgsConstructor
@Tag(name = "Participants", description = "Beteiligte-Verwaltung")
public class ParticipantController {
    
    private final ParticipantService participantService;
    
    @PostMapping
    @Operation(summary = "Beteiligten hinzufügen")
    public ResponseEntity<ContractParticipant> addParticipant(
            @RequestBody ContractParticipant participant) {
        return ResponseEntity.ok(participantService.addParticipant(participant));
    }
    
    @GetMapping("/contract/{contractId}")
    @Operation(summary = "Alle Beteiligte eines Vertrags")
    public ResponseEntity<List<ContractParticipant>> getParticipants(
            @PathVariable Long contractId) {
        return ResponseEntity.ok(participantService.getParticipantsByContract(contractId));
    }
    
    @GetMapping("/contract/{contractId}/responsible")
    @Operation(summary = "Zuständige eines Vertrags")
    public ResponseEntity<List<ContractParticipant>> getResponsibleUsers(
            @PathVariable Long contractId) {
        return ResponseEntity.ok(participantService.getResponsibleUsers(contractId));
    }
    
    @DeleteMapping("/{participantId}")
    @Operation(summary = "Beteiligten entfernen")
    public ResponseEntity<Void> removeParticipant(@PathVariable Long participantId) {
        participantService.removeParticipant(participantId);
        return ResponseEntity.noContent().build();
    }
}

