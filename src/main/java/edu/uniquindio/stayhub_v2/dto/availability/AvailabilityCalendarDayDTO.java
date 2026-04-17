package edu.uniquindio.stayhub_v2.dto.availability;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record AvailabilityCalendarDayDTO(
        @Schema(description = "Date", example = "2026-05-01")
        LocalDate date,

        @Schema(description = "Day status: AVAILABLE, BOOKED, BLOCKED, UNDEFINED")
        String status,

        @Schema(description = "Reservation ID if BOOKED", nullable = true)
        Long reservationId
) {}
