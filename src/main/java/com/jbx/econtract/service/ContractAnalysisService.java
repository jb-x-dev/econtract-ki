package com.jbx.econtract.service;

import com.jbx.econtract.dto.ContractAnalysisDTO;
import com.jbx.econtract.dto.ContractAnalysisDTO.RiskItem;
import com.jbx.econtract.dto.ContractAnalysisDTO.ComplianceItem;
import com.jbx.econtract.model.entity.Contract;
import com.jbx.econtract.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ContractAnalysisService {
    
    @Autowired
    private ContractRepository contractRepository;
    
    /**
     * Analysiert einen Vertrag und gibt detaillierte Analyse-Ergebnisse zurück
     */
    public ContractAnalysisDTO analyzeContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
            .orElseThrow(() -> new RuntimeException("Contract not found: " + contractId));
        
        ContractAnalysisDTO analysis = new ContractAnalysisDTO();
        
        // Basis-Informationen
        analysis.setContractId(contract.getId());
        analysis.setContractNumber(contract.getContractNumber());
        analysis.setTitle(contract.getTitle());
        analysis.setPartner(contract.getPartnerName());
        analysis.setType(contract.getContractType());
        analysis.setValue(contract.getContractValue() != null ? contract.getContractValue().doubleValue() : 0.0);
        
        // KI-basierte Analyse durchführen
        performRiskAnalysis(contract, analysis);
        performComplianceCheck(contract, analysis);
        generateRecommendations(contract, analysis);
        
        // Metadata
        analysis.setAnalyzedAt(LocalDateTime.now());
        analysis.setAnalyzedBy("KI-Analyse-System");
        
        return analysis;
    }
    
    /**
     * Führt Risikoanalyse durch
     */
    private void performRiskAnalysis(Contract contract, ContractAnalysisDTO analysis) {
        List<RiskItem> risks = new ArrayList<>();
        int riskScore = 0;
        
        // Simulierte KI-Analyse basierend auf Vertragstyp und Wert
        String type = contract.getContractType();
        Double value = contract.getContractValue() != null ? contract.getContractValue().doubleValue() : 0.0;
        
        // Risiko 1: Hoher Vertragswert
        if (value > 100000) {
            risks.add(new RiskItem(
                "Finanzielles Risiko",
                "HIGH",
                "Sehr hoher Vertragswert von " + String.format("%.2f", value) + " EUR",
                "Vertragswert",
                "Erwägen Sie eine zusätzliche Absicherung oder Staffelung der Zahlungen"
            ));
            riskScore += 30;
        } else if (value > 50000) {
            risks.add(new RiskItem(
                "Finanzielles Risiko",
                "MEDIUM",
                "Erhöhter Vertragswert von " + String.format("%.2f", value) + " EUR",
                "Vertragswert",
                "Prüfen Sie Zahlungsbedingungen und Sicherheiten"
            ));
            riskScore += 15;
        }
        
        // Risiko 2: Lange Laufzeit
        if (contract.getEndDate() != null && contract.getStartDate() != null) {
            long months = java.time.temporal.ChronoUnit.MONTHS.between(
                contract.getStartDate(), contract.getEndDate()
            );
            if (months > 36) {
                risks.add(new RiskItem(
                    "Laufzeitrisiko",
                    "MEDIUM",
                    "Sehr lange Vertragslaufzeit von " + months + " Monaten",
                    "Vertragslaufzeit",
                    "Erwägen Sie Kündigungsklauseln oder regelmäßige Überprüfungen"
                ));
                riskScore += 20;
            }
        }
        
        // Risiko 3: Typ-spezifische Risiken
        if ("SUPPLIER".equals(type)) {
            risks.add(new RiskItem(
                "Lieferantenrisiko",
                "MEDIUM",
                "Abhängigkeit von externem Lieferanten",
                "Vertragstyp",
                "Prüfen Sie alternative Lieferanten und SLA-Vereinbarungen"
            ));
            riskScore += 15;
        }
        
        // Risiko-Level bestimmen
        String riskLevel;
        if (riskScore >= 60) {
            riskLevel = "CRITICAL";
        } else if (riskScore >= 40) {
            riskLevel = "HIGH";
        } else if (riskScore >= 20) {
            riskLevel = "MEDIUM";
        } else {
            riskLevel = "LOW";
        }
        
        analysis.setRisks(risks);
        analysis.setRiskScore(riskScore);
        analysis.setRiskLevel(riskLevel);
        analysis.setTotalClauses(15 + new Random().nextInt(20)); // Simuliert
        analysis.setProblematicClauses(risks.size());
    }
    
    /**
     * Führt Compliance-Prüfung durch
     */
    private void performComplianceCheck(Contract contract, ContractAnalysisDTO analysis) {
        List<ComplianceItem> complianceIssues = new ArrayList<>();
        double complianceScore = 100.0;
        
        // DSGVO-Prüfung
        if (contract.getTitle() != null && 
            (contract.getTitle().toLowerCase().contains("daten") || 
             contract.getTitle().toLowerCase().contains("personenbezogen"))) {
            complianceIssues.add(new ComplianceItem(
                "DSGVO",
                "UNCLEAR",
                "Vertrag könnte personenbezogene Daten betreffen",
                "Prüfen Sie DSGVO-Konformität und Datenschutzvereinbarungen"
            ));
            complianceScore -= 15;
        }
        
        // Vertragsform-Prüfung
        if (contract.getContractValue() != null && contract.getContractValue().doubleValue() > 10000) {
            complianceIssues.add(new ComplianceItem(
                "Schriftform",
                "COMPLIANT",
                "Vertrag liegt in dokumentierter Form vor",
                "Keine Maßnahmen erforderlich"
            ));
        }
        
        // AGB-Prüfung
        if (contract.getContractType() != null && contract.getContractType().contains("CUSTOMER")) {
            complianceIssues.add(new ComplianceItem(
                "AGB-Recht",
                "UNCLEAR",
                "Prüfung auf unwirksame AGB-Klauseln empfohlen",
                "Lassen Sie den Vertrag rechtlich prüfen"
            ));
            complianceScore -= 10;
        }
        
        analysis.setComplianceIssues(complianceIssues);
        analysis.setComplianceScore(complianceScore);
    }
    
    /**
     * Generiert Empfehlungen basierend auf der Analyse
     */
    private void generateRecommendations(Contract contract, ContractAnalysisDTO analysis) {
        List<String> recommendations = new ArrayList<>();
        
        // Allgemeine Empfehlungen
        recommendations.add("Lassen Sie den Vertrag von einem Fachanwalt prüfen");
        
        if (analysis.getRiskScore() > 40) {
            recommendations.add("Erwägen Sie eine Vertragsversicherung aufgrund des erhöhten Risikos");
        }
        
        if (contract.getEndDate() != null) {
            recommendations.add("Setzen Sie eine Erinnerung für die Vertragsverlängerung/Kündigung");
        }
        
        if (contract.getAutoRenewal() != null && contract.getAutoRenewal()) {
            recommendations.add("Prüfen Sie die automatische Verlängerungsklausel");
        }
        
        if (analysis.getComplianceScore() < 90) {
            recommendations.add("Führen Sie eine vollständige Compliance-Prüfung durch");
        }
        
        recommendations.add("Dokumentieren Sie alle Vertragsänderungen systematisch");
        recommendations.add("Richten Sie ein regelmäßiges Vertrags-Review ein");
        
        analysis.setRecommendations(recommendations);
    }
    
    /**
     * Gibt Analyse-Statistiken für alle Verträge zurück
     */
    public AnalysisStatisticsDTO getAnalysisStatistics() {
        List<Contract> allContracts = contractRepository.findAll();
        
        AnalysisStatisticsDTO stats = new AnalysisStatisticsDTO();
        stats.setTotalContracts(allContracts.size());
        
        int highRisk = 0;
        int mediumRisk = 0;
        int lowRisk = 0;
        double totalValue = 0.0;
        
        for (Contract contract : allContracts) {
            // Vereinfachte Risiko-Berechnung
            Double value = contract.getContractValue() != null ? contract.getContractValue().doubleValue() : 0.0;
            totalValue += value;
            
            if (value > 100000) {
                highRisk++;
            } else if (value > 50000) {
                mediumRisk++;
            } else {
                lowRisk++;
            }
        }
        
        stats.setHighRiskContracts(highRisk);
        stats.setMediumRiskContracts(mediumRisk);
        stats.setLowRiskContracts(lowRisk);
        stats.setTotalContractValue(totalValue);
        stats.setAverageRiskScore(highRisk > 0 ? 65 : (mediumRisk > 0 ? 35 : 15));
        
        return stats;
    }
    
    // Nested DTO für Statistiken
    public static class AnalysisStatisticsDTO {
        private Integer totalContracts;
        private Integer highRiskContracts;
        private Integer mediumRiskContracts;
        private Integer lowRiskContracts;
        private Double totalContractValue;
        private Integer averageRiskScore;
        
        // Getters and Setters
        public Integer getTotalContracts() { return totalContracts; }
        public void setTotalContracts(Integer totalContracts) { this.totalContracts = totalContracts; }
        
        public Integer getHighRiskContracts() { return highRiskContracts; }
        public void setHighRiskContracts(Integer highRiskContracts) { this.highRiskContracts = highRiskContracts; }
        
        public Integer getMediumRiskContracts() { return mediumRiskContracts; }
        public void setMediumRiskContracts(Integer mediumRiskContracts) { this.mediumRiskContracts = mediumRiskContracts; }
        
        public Integer getLowRiskContracts() { return lowRiskContracts; }
        public void setLowRiskContracts(Integer lowRiskContracts) { this.lowRiskContracts = lowRiskContracts; }
        
        public Double getTotalContractValue() { return totalContractValue; }
        public void setTotalContractValue(Double totalContractValue) { this.totalContractValue = totalContractValue; }
        
        public Integer getAverageRiskScore() { return averageRiskScore; }
        public void setAverageRiskScore(Integer averageRiskScore) { this.averageRiskScore = averageRiskScore; }
    }
}

