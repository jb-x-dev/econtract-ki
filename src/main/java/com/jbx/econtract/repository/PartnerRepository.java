package com.jbx.econtract.repository;

import com.jbx.econtract.model.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Partner Entity
 */
@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {

    /**
     * Find partner by name
     */
    Optional<Partner> findByName(String name);

    /**
     * Find partner by partner number
     */
    Optional<Partner> findByPartnerNumber(String partnerNumber);

    /**
     * Find all active partners
     */
    List<Partner> findByIsActiveTrue();

    /**
     * Find partners by type
     */
    List<Partner> findByPartnerType(Partner.PartnerType partnerType);

    /**
     * Find active partners by type
     */
    List<Partner> findByPartnerTypeAndIsActiveTrue(Partner.PartnerType partnerType);

    /**
     * Search partners by name (case-insensitive)
     */
    List<Partner> findByNameContainingIgnoreCase(String name);

    /**
     * Count partners by type
     */
    long countByPartnerType(Partner.PartnerType partnerType);

    /**
     * Count active partners
     */
    long countByIsActiveTrue();
}
