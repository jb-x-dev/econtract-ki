package com.jbx.econtract.service;

import com.jbx.econtract.model.entity.Contract;
import com.jbx.econtract.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Workflow Service für Genehmigungsprozesse
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {

    private final ContractRepository contractRepository;
    private final NotificationService notificationService;

    /**
     * Startet Genehmigungsworkflow für einen Vertrag
     */
    @Transactional
    public void startApprovalWorkflow(Long contractId) {
        log.info("Starting approval workflow for contract: {}", contractId);
        
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
        
        // Workflow-Definition basierend auf Vertragstyp und Wert
        List<ApprovalStep> steps = defineWorkflowSteps(contract);
        
        // Ersten Schritt aktivieren
        if (!steps.isEmpty()) {
            ApprovalStep firstStep = steps.get(0);
            notificationService.notifyApprover(contractId, firstStep.getApproverId(), 
                    "Neuer Vertrag zur Genehmigung: " + contract.getTitle());
        }
        
        log.info("Workflow started with {} steps", steps.size());
    }

    /**
     * Definiert Workflow-Schritte basierend auf Vertrag
     */
    private List<ApprovalStep> defineWorkflowSteps(Contract contract) {
        List<ApprovalStep> steps = new ArrayList<>();
        
        // Schritt 1: Fachabteilung
        steps.add(new ApprovalStep(1, "Fachabteilung", 2L, 3));
        
        // Schritt 2: Rechtsabteilung (bei Wert > 50.000 EUR)
        if (contract.getContractValue() != null && 
            contract.getContractValue().doubleValue() > 50000) {
            steps.add(new ApprovalStep(2, "Rechtsabteilung", 3L, 5));
        }
        
        // Schritt 3: Finanzabteilung
        steps.add(new ApprovalStep(3, "Finanzabteilung", 4L, 3));
        
        // Schritt 4: Geschäftsführung (bei Wert > 100.000 EUR)
        if (contract.getContractValue() != null && 
            contract.getContractValue().doubleValue() > 100000) {
            steps.add(new ApprovalStep(4, "Geschäftsführung", 5L, 7));
        }
        
        return steps;
    }

    /**
     * Genehmigt einen Workflow-Schritt
     */
    @Transactional
    public void approveStep(Long contractId, Long userId, String comment) {
        log.info("Approving step for contract {} by user {}", contractId, userId);
        
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
        
        // Nächsten Schritt aktivieren oder Workflow abschließen
        // TODO: Implementierung der Schritt-Logik
        
        notificationService.notifyContractOwner(contractId, 
                "Ihr Vertrag wurde genehmigt: " + contract.getTitle());
    }

    /**
     * Lehnt einen Workflow-Schritt ab
     */
    @Transactional
    public void rejectStep(Long contractId, Long userId, String reason) {
        log.info("Rejecting step for contract {} by user {}", contractId, userId);
        
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
        
        contract.setStatus(Contract.ContractStatus.DRAFT);
        contractRepository.save(contract);
        
        notificationService.notifyContractOwner(contractId, 
                "Ihr Vertrag wurde abgelehnt: " + reason);
    }

    /**
     * Inner class für Approval Step
     */
    private static class ApprovalStep {
        private final int stepNumber;
        private final String stepName;
        private final Long approverId;
        private final int dueDays;

        public ApprovalStep(int stepNumber, String stepName, Long approverId, int dueDays) {
            this.stepNumber = stepNumber;
            this.stepName = stepName;
            this.approverId = approverId;
            this.dueDays = dueDays;
        }

        public Long getApproverId() {
            return approverId;
        }
    }
}

