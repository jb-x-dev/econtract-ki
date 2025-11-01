package com.jbx.econtract.controller;

import com.jbx.econtract.model.entity.QuickFilter;
import com.jbx.econtract.service.QuickFilterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/filters")
@RequiredArgsConstructor
@Tag(name = "Quick Filters", description = "Schnellfilter-Verwaltung")
public class QuickFilterController {
    
    private final QuickFilterService filterService;
    
    @PostMapping
    @Operation(summary = "Schnellfilter speichern")
    public ResponseEntity<QuickFilter> saveFilter(@RequestBody QuickFilter filter) {
        return ResponseEntity.ok(filterService.saveFilter(filter));
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Alle Filter eines Users")
    public ResponseEntity<List<QuickFilter>> getFilters(@PathVariable Long userId) {
        return ResponseEntity.ok(filterService.getFiltersByUser(userId));
    }
    
    @GetMapping("/user/{userId}/default")
    @Operation(summary = "Standard-Filter eines Users")
    public ResponseEntity<QuickFilter> getDefaultFilter(@PathVariable Long userId) {
        QuickFilter filter = filterService.getDefaultFilter(userId);
        if (filter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(filter);
    }
    
    @DeleteMapping("/{filterId}")
    @Operation(summary = "Schnellfilter löschen")
    public ResponseEntity<Void> deleteFilter(@PathVariable Long filterId) {
        filterService.deleteFilter(filterId);
        return ResponseEntity.noContent().build();
    }
}

