package edu.uniquindio.stayhub_v2.dto.availability;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Result of an availability check for a date range")
public record AvailabilityCheckResponseDTO(
        @Schema(description = "Accommodation ID")
        Long accommodationId,

        @Schema(description = "Requested start date")
        LocalDateTime startDate,

        @Schema(description = "Requested end date")
        LocalDateTime endDate,

        @Schema(description = "Whether the accommodation is available for the entire range")
        boolean available,

        @Schema(description = "Reason if not available", nullable = true)
        String reason
) {}
