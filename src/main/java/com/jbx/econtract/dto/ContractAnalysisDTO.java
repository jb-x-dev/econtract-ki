package com.jbx.econtract.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ContractAnalysisDTO {
    
    private Long contractId;
    private String contractNumber;
    private String title;
    private String partner;
    private String type;
    private Double value;
    
    // Analyse-Ergebnisse
    private Integer riskScore; // 0-100
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL
    private List<RiskItem> risks;
    private List<ComplianceItem> complianceIssues;
    private List<String> recommendations;
    
    // Statistiken
    private Integer totalClauses;
    private Integer problematicClauses;
    private Double complianceScore; // 0-100
    
    // Metadata
    private LocalDateTime analyzedAt;
    private String analyzedBy;
    
    // Nested Classes
    public static class RiskItem {
        private String category;
        private String severity; // LOW, MEDIUM, HIGH, CRITICAL
        private String description;
        private String location; // Abschnitt/Klausel
        private String recommendation;
        
        public RiskItem() {}
        
        public RiskItem(String category, String severity, String description, String location, String recommendation) {
            this.category = category;
            this.severity = severity;
            this.description = description;
            this.location = location;
            this.recommendation = recommendation;
        }
        
        // Getters and Setters
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    }
    
    public static class ComplianceItem {
        private String regulation;
        private String status; // COMPLIANT, NON_COMPLIANT, UNCLEAR
        private String description;
        private String recommendation;
        
        public ComplianceItem() {}
        
        public ComplianceItem(String regulation, String status, String description, String recommendation) {
            this.regulation = regulation;
            this.status = status;
            this.description = description;
            this.recommendation = recommendation;
        }
        
        // Getters and Setters
        public String getRegulation() { return regulation; }
        public void setRegulation(String regulation) { this.regulation = regulation; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    }
    
    // Constructors
    public ContractAnalysisDTO() {}
    
    // Getters and Setters
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    
    public String getContractNumber() { return contractNumber; }
    public void setContractNumber(String contractNumber) { this.contractNumber = contractNumber; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getPartner() { return partner; }
    public void setPartner(String partner) { this.partner = partner; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    
    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
    
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    
    public List<RiskItem> getRisks() { return risks; }
    public void setRisks(List<RiskItem> risks) { this.risks = risks; }
    
    public List<ComplianceItem> getComplianceIssues() { return complianceIssues; }
    public void setComplianceIssues(List<ComplianceItem> complianceIssues) { this.complianceIssues = complianceIssues; }
    
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    
    public Integer getTotalClauses() { return totalClauses; }
    public void setTotalClauses(Integer totalClauses) { this.totalClauses = totalClauses; }
    
    public Integer getProblematicClauses() { return problematicClauses; }
    public void setProblematicClauses(Integer problematicClauses) { this.problematicClauses = problematicClauses; }
    
    public Double getComplianceScore() { return complianceScore; }
    public void setComplianceScore(Double complianceScore) { this.complianceScore = complianceScore; }
    
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
    
    public String getAnalyzedBy() { return analyzedBy; }
    public void setAnalyzedBy(String analyzedBy) { this.analyzedBy = analyzedBy; }
}

