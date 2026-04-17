package edu.uniquindio.stayhub_v2.dto.availability;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Visual calendar of availability for a given month")
public record AvailabilityCalendarResponseDTO(
        @Schema(description = "Accommodation ID")
        Long accommodationId,

        @Schema(description = "Month in YYYY-MM format", example = "2026-05")
        String month,

        @Schema(description = "Day-by-day status list")
        List<AvailabilityCalendarDayDTO> days
) {}
