package com.jbx.econtract.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@RestController
@RequestMapping("/api/v1/calendar")
@Tag(name = "Calendar", description = "Kalender für Fristenmanagement")
public class CalendarController {
    
    @GetMapping("/events")
    @Operation(summary = "Alle Kalender-Events")
    public ResponseEntity<List<Map<String, Object>>> getAllEvents(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now();
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : start.plusMonths(3);
        
        List<Map<String, Object>> events = generateEvents(start, end);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/events/month")
    @Operation(summary = "Events für einen bestimmten Monat")
    public ResponseEntity<Map<String, Object>> getMonthEvents(
            @RequestParam int year,
            @RequestParam int month) {
        
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        
        List<Map<String, Object>> events = generateEvents(start, end);
        
        Map<String, Object> result = new HashMap<>();
        result.put("year", year);
        result.put("month", month);
        result.put("monthName", yearMonth.getMonth().toString());
        result.put("daysInMonth", yearMonth.lengthOfMonth());
        result.put("events", events);
        result.put("eventsCount", events.size());
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/events/today")
    @Operation(summary = "Heutige Events")
    public ResponseEntity<List<Map<String, Object>>> getTodayEvents() {
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> events = generateEvents(today, today);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/events/upcoming")
    @Operation(summary = "Anstehende Events")
    public ResponseEntity<List<Map<String, Object>>> getUpcomingEvents(
            @RequestParam(defaultValue = "30") int days) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(days);
        List<Map<String, Object>> events = generateEvents(start, end);
        return ResponseEntity.ok(events);
    }
    
    @PostMapping("/events")
    @Operation(summary = "Neues Event erstellen")
    public ResponseEntity<Map<String, Object>> createEvent(@RequestBody Map<String, Object> event) {
        event.put("id", System.currentTimeMillis());
        event.put("createdAt", LocalDate.now().toString());
        return ResponseEntity.ok(event);
    }
    
    @PutMapping("/events/{id}")
    @Operation(summary = "Event aktualisieren")
    public ResponseEntity<Map<String, Object>> updateEvent(
            @PathVariable Long id,
            @RequestBody Map<String, Object> event) {
        event.put("id", id);
        event.put("updatedAt", LocalDate.now().toString());
        return ResponseEntity.ok(event);
    }
    
    @DeleteMapping("/events/{id}")
    @Operation(summary = "Event löschen")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/events/by-type")
    @Operation(summary = "Events nach Typ gruppiert")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getEventsByType() {
        Map<String, List<Map<String, Object>>> grouped = new HashMap<>();
        
        List<Map<String, Object>> deadlines = new ArrayList<>();
        deadlines.add(createEvent(1L, "Vertragsende CON-2025-000001", "DEADLINE", LocalDate.now().plusDays(5), "HIGH"));
        deadlines.add(createEvent(2L, "Kündigungsfrist CON-2025-000002", "DEADLINE", LocalDate.now().plusDays(12), "MEDIUM"));
        grouped.put("DEADLINE", deadlines);
        
        List<Map<String, Object>> renewals = new ArrayList<>();
        renewals.add(createEvent(3L, "Verlängerung FW-2025-000001", "RENEWAL", LocalDate.now().plusDays(20), "MEDIUM"));
        grouped.put("RENEWAL", renewals);
        
        List<Map<String, Object>> reviews = new ArrayList<>();
        reviews.add(createEvent(4L, "Vertragsprüfung CON-2025-000003", "REVIEW", LocalDate.now().plusDays(15), "LOW"));
        grouped.put("REVIEW", reviews);
        
        return ResponseEntity.ok(grouped);
    }
    
    private List<Map<String, Object>> generateEvents(LocalDate start, LocalDate end) {
        List<Map<String, Object>> events = new ArrayList<>();
        
        // Generiere Demo-Events
        LocalDate current = start;
        long id = 1;
        
        while (!current.isAfter(end)) {
            if (current.getDayOfMonth() % 5 == 0) {
                events.add(createEvent(id++, "Vertragsende CON-2025-" + String.format("%06d", id), 
                    "DEADLINE", current, "HIGH"));
            }
            if (current.getDayOfMonth() % 7 == 0) {
                events.add(createEvent(id++, "Kündigungsfrist CON-2025-" + String.format("%06d", id), 
                    "CANCELLATION", current, "MEDIUM"));
            }
            if (current.getDayOfMonth() % 10 == 0) {
                events.add(createEvent(id++, "Verlängerung FW-2025-" + String.format("%06d", id), 
                    "RENEWAL", current, "MEDIUM"));
            }
            if (current.getDayOfMonth() % 15 == 0) {
                events.add(createEvent(id++, "Vertragsprüfung CON-2025-" + String.format("%06d", id), 
                    "REVIEW", current, "LOW"));
            }
            
            current = current.plusDays(1);
        }
        
        return events;
    }
    
    private Map<String, Object> createEvent(Long id, String title, String type, 
                                           LocalDate date, String priority) {
        Map<String, Object> event = new HashMap<>();
        event.put("id", id);
        event.put("title", title);
        event.put("eventType", type);
        event.put("eventDate", date.toString());
        event.put("priority", priority);
        event.put("status", "SCHEDULED");
        event.put("allDay", true);
        return event;
    }
}

