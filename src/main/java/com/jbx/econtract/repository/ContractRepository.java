package com.jbx.econtract.repository;

import com.jbx.econtract.model.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository für Contract Entity
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    /**
     * Findet Vertrag nach Vertragsnummer
     */
    Optional<Contract> findByContractNumber(String contractNumber);

    /**
     * Findet Verträge nach Status
     */
    Page<Contract> findByStatus(Contract.ContractStatus status, Pageable pageable);

    /**
     * Findet Verträge nach Typ
     */
    Page<Contract> findByContractType(String contractType, Pageable pageable);

    /**
     * Findet Verträge nach Status und Typ
     */
    Page<Contract> findByStatusAndContractType(
            Contract.ContractStatus status, 
            String contractType, 
            Pageable pageable);

    /**
     * Findet Verträge eines Besitzers
     */
    Page<Contract> findByOwnerUserId(Long ownerUserId, Pageable pageable);

    /**
     * Findet Verträge einer Abteilung
     */
    Page<Contract> findByDepartment(String department, Pageable pageable);

    /**
     * Findet Verträge, die bald ablaufen
     */
    @Query("SELECT c FROM Contract c WHERE c.endDate BETWEEN :startDate AND :endDate " +
           "AND c.status IN ('APPROVED', 'ACTIVE')")
    List<Contract> findExpiringContracts(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Findet abgelaufene Verträge
     */
    @Query("SELECT c FROM Contract c WHERE c.endDate < :today " +
           "AND c.status = 'ACTIVE'")
    List<Contract> findExpiredContracts(@Param("today") LocalDate today);

    /**
     * Zählt Verträge nach Status
     */
    Long countByStatus(Contract.ContractStatus status);

    /**
     * Zählt Verträge nach Typ
     */
    Long countByContractType(String contractType);

    /**
     * Sucht Verträge nach Titel (LIKE)
     */
    @Query("SELECT c FROM Contract c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Contract> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Sucht Verträge nach Partner (LIKE)
     */
    @Query("SELECT c FROM Contract c WHERE LOWER(c.partnerName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Contract> searchByPartner(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Findet alle Einzelverträge eines Rahmenvertrags
     */
    List<Contract> findByFrameworkContractId(Long frameworkContractId);
}

