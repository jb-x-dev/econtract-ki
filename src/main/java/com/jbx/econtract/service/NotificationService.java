package com.jbx.econtract.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Notification Service für E-Mail und In-App Benachrichtigungen
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    /**
     * Benachrichtigt Genehmiger über neuen Vertrag
     */
    public void notifyApprover(Long contractId, Long approverId, String message) {
        log.info("Notifying approver {} about contract {}: {}", approverId, contractId, message);
        
        // TODO: E-Mail senden
        // TODO: In-App Benachrichtigung erstellen
        
        sendEmail(approverId, "Vertrag zur Genehmigung", message);
    }

    /**
     * Benachrichtigt Vertragsbesitzer
     */
    public void notifyContractOwner(Long contractId, String message) {
        log.info("Notifying contract owner about contract {}: {}", contractId, message);
        
        // TODO: E-Mail senden
        // TODO: In-App Benachrichtigung erstellen
    }

    /**
     * Sendet E-Mail (Platzhalter)
     */
    private void sendEmail(Long userId, String subject, String body) {
        log.info("Sending email to user {}: {}", userId, subject);
        
        // TODO: SMTP Integration
        // Beispiel mit Spring Mail:
        // MimeMessage message = mailSender.createMimeMessage();
        // MimeMessageHelper helper = new MimeMessageHelper(message, true);
        // helper.setTo(userEmail);
        // helper.setSubject(subject);
        // helper.setText(body, true);
        // mailSender.send(message);
    }

    /**
     * Sendet Frist-Erinnerung
     */
    public void sendDeadlineReminder(Long contractId, String contractTitle, int daysRemaining) {
        log.info("Sending deadline reminder for contract {}: {} days remaining", contractId, daysRemaining);
        
        String message = String.format(
                "Erinnerung: Der Vertrag '%s' läuft in %d Tagen ab.",
                contractTitle, daysRemaining);
        
        notifyContractOwner(contractId, message);
    }
}

