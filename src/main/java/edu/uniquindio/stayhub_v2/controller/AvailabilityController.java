package edu.uniquindio.stayhub_v2.controller;

import edu.uniquindio.stayhub_v2.dto.availability.AvailabilityCalendarResponseDTO;
import edu.uniquindio.stayhub_v2.dto.availability.AvailabilityCheckResponseDTO;
import edu.uniquindio.stayhub_v2.dto.availability.AvailabilityRuleDTO;
import edu.uniquindio.stayhub_v2.service.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Availability", description = "Availability querying and calendar management")
@RestController
@RequestMapping("/api/v2/accommodations/{id}/availability")
@RequiredArgsConstructor
@Slf4j
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @Operation(summary = "Check availability for a date range")
    @GetMapping("/check")
    public ResponseEntity<AvailabilityCheckResponseDTO> checkAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("GET /accommodations/{}/availability/check start={} end={}", id, startDate, endDate);
        return ResponseEntity.ok(availabilityService.checkAvailability(id, startDate, endDate));
    }

    @Operation(summary = "Get availability calendar", description = "Returns day-by-day status: AVAILABLE, BOOKED, BLOCKED, UNDEFINED")
    @GetMapping("/calendar")
    public ResponseEntity<AvailabilityCalendarResponseDTO> getCalendar(
            @PathVariable Long id,
            @RequestParam String month) {
        log.info("GET /accommodations/{}/availability/calendar month={}", id, month);
        return ResponseEntity.ok(availabilityService.getCalendar(id, month));
    }

    @Operation(summary = "List availability rules", description = "Returns host-defined availability/blocked periods")
    @GetMapping("/rules")
    public ResponseEntity<List<AvailabilityRuleDTO>> listRules(@PathVariable Long id) {
        log.info("GET /accommodations/{}/availability/rules", id);
        return ResponseEntity.ok(availabilityService.listRules(id));
    }

    @Operation(summary = "Replace availability rules", description = "Host replaces all availability rules for an accommodation")
    @PutMapping("/rules")
    public ResponseEntity<List<AvailabilityRuleDTO>> replaceRules(
            @PathVariable Long id,
            @Valid @RequestBody List<AvailabilityRuleDTO> rules) {
        log.info("PUT /accommodations/{}/availability/rules count={}", id, rules.size());
        return ResponseEntity.ok(availabilityService.replaceRules(id, rules));
    }
}
