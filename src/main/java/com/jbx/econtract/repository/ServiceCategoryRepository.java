package com.jbx.econtract.repository;

import com.jbx.econtract.model.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ServiceCategory entity.
 * 
 * @author jb-x Development Team
 * @version 1.0
 */
@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {

    /**
     * Find service category by code.
     *
     * @param code the category code
     * @return optional service category
     */
    Optional<ServiceCategory> findByCode(String code);

    /**
     * Find all active service categories.
     *
     * @param isActive the active status
     * @return list of active service categories
     */
    List<ServiceCategory> findByIsActive(Boolean isActive);

    /**
     * Check if a service category with the given code exists.
     *
     * @param code the category code
     * @return true if exists, false otherwise
     */
    boolean existsByCode(String code);
}
