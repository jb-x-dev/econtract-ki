package com.jbx.econtract.service;

import com.jbx.econtract.model.entity.QuickFilter;
import com.jbx.econtract.repository.QuickFilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuickFilterService {
    
    private final QuickFilterRepository filterRepository;
    
    @Transactional
    public QuickFilter saveFilter(QuickFilter filter) {
        // Wenn als Standard markiert, andere Standard-Filter deaktivieren
        if (filter.getIsDefault() != null && filter.getIsDefault()) {
            filterRepository.findByUserId(filter.getUserId())
                .forEach(f -> {
                    f.setIsDefault(false);
                    filterRepository.save(f);
                });
        }
        
        return filterRepository.save(filter);
    }
    
    public List<QuickFilter> getFiltersByUser(Long userId) {
        return filterRepository.findByUserId(userId);
    }
    
    public QuickFilter getDefaultFilter(Long userId) {
        return filterRepository.findByUserIdAndIsDefault(userId, true)
            .orElse(null);
    }
    
    @Transactional
    public void deleteFilter(Long filterId) {
        filterRepository.deleteById(filterId);
    }
}

