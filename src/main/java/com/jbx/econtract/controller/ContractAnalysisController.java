package com.jbx.econtract.controller;

import com.jbx.econtract.dto.ContractAnalysisDTO;
import com.jbx.econtract.service.ContractAnalysisService;
import com.jbx.econtract.service.ContractAnalysisService.AnalysisStatisticsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analysis")
public class ContractAnalysisController {
    
    @Autowired
    private ContractAnalysisService analysisService;
    
    /**
     * Analysiert einen spezifischen Vertrag
     * GET /api/v1/analysis/contract/{id}
     */
    @GetMapping("/contract/{id}")
    public ResponseEntity<ContractAnalysisDTO> analyzeContract(@PathVariable Long id) {
        try {
            ContractAnalysisDTO analysis = analysisService.analyzeContract(id);
            return ResponseEntity.ok(analysis);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Gibt Analyse-Statistiken für alle Verträge zurück
     * GET /api/v1/analysis/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<AnalysisStatisticsDTO> getStatistics() {
        AnalysisStatisticsDTO stats = analysisService.getAnalysisStatistics();
        return ResponseEntity.ok(stats);
    }
}

