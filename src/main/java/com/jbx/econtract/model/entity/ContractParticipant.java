package com.jbx.econtract.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "contract_participants")
@Data
public class ContractParticipant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "contract_id", nullable = false)
    private Long contractId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "participant_type", nullable = false, columnDefinition = "VARCHAR(50)")
    private ParticipantType participantType;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "external_name")
    private String externalName;
    
    @Column(name = "external_email")
    private String externalEmail;
    
    @Column(name = "external_phone")
    private String externalPhone;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ParticipantType {
        RESPONSIBLE,      // Zuständiger
        INFOUSER,        // Infouser (mit System-Zugang)
        EXTERNAL_INFOUSER // Externer Infouser (ohne System-Zugang)
    }
}

