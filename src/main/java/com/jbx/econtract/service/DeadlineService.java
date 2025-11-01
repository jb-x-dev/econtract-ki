package com.jbx.econtract.service;

import com.jbx.econtract.model.entity.Contract;
import com.jbx.econtract.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service für Fristen-Management und automatische Erinnerungen
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeadlineService {

    private final ContractRepository contractRepository;
    private final NotificationService notificationService;

    /**
     * Prüft täglich ablaufende Verträge (läuft jeden Tag um 9:00 Uhr)
     */
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional(readOnly = true)
    public void checkExpiringContracts() {
        log.info("Checking expiring contracts...");
        
        LocalDate today = LocalDate.now();
        
        // Prüfe Verträge die in 30, 14, 7 oder 1 Tag ablaufen
        int[] reminderDays = {30, 14, 7, 1};
        
        for (int days : reminderDays) {
            LocalDate targetDate = today.plusDays(days);
            List<Contract> expiringContracts = contractRepository.findExpiringContracts(
                    targetDate, targetDate);
            
            for (Contract contract : expiringContracts) {
                sendExpiryReminder(contract, days);
            }
        }
        
        // Prüfe abgelaufene Verträge
        List<Contract> expiredContracts = contractRepository.findExpiredContracts(today);
        for (Contract contract : expiredContracts) {
            markContractAsExpired(contract);
        }
        
        log.info("Deadline check completed");
    }

    /**
     * Sendet Ablauf-Erinnerung
     */
    private void sendExpiryReminder(Contract contract, int daysRemaining) {
        log.info("Sending expiry reminder for contract {} ({} days)", 
                contract.getContractNumber(), daysRemaining);
        
        notificationService.sendDeadlineReminder(
                contract.getId(), 
                contract.getTitle(), 
                daysRemaining);
    }

    /**
     * Markiert Vertrag als abgelaufen
     */
    @Transactional
    public void markContractAsExpired(Contract contract) {
        log.info("Marking contract as expired: {}", contract.getContractNumber());
        
        contract.setStatus(Contract.ContractStatus.EXPIRED);
        contractRepository.save(contract);
        
        notificationService.notifyContractOwner(
                contract.getId(),
                "Vertrag abgelaufen: " + contract.getTitle());
    }

    /**
     * Berechnet Tage bis Ablauf
     */
    public long calculateDaysUntilExpiry(Contract contract) {
        if (contract.getEndDate() == null) {
            return -1;
        }
        
        LocalDate today = LocalDate.now();
        return ChronoUnit.DAYS.between(today, contract.getEndDate());
    }

    /**
     * Prüft ob Kündigungsfrist erreicht ist
     */
    public boolean isNoticePeriodReached(Contract contract) {
        if (contract.getEndDate() == null || contract.getNoticePeriodDays() == null) {
            return false;
        }
        
        LocalDate noticePeriodDate = contract.getEndDate().minusDays(contract.getNoticePeriodDays());
        return LocalDate.now().isAfter(noticePeriodDate);
    }
}

