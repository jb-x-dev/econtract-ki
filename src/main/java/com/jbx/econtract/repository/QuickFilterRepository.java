package com.jbx.econtract.repository;

import com.jbx.econtract.model.entity.QuickFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuickFilterRepository extends JpaRepository<QuickFilter, Long> {
    
    List<QuickFilter> findByUserId(Long userId);
    
    Optional<QuickFilter> findByUserIdAndIsDefault(Long userId, Boolean isDefault);
}

