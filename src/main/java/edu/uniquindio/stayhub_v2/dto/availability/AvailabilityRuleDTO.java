package edu.uniquindio.stayhub_v2.dto.availability;

import edu.uniquindio.stayhub_v2.model.AvailabilityRuleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Availability or blocked period rule defined by the host")
public record AvailabilityRuleDTO(
        @Schema(description = "Rule ID (only in responses)", nullable = true)
        Long id,

        @NotNull
        @Schema(description = "Rule type: AVAILABLE or BLOCKED")
        AvailabilityRuleType type,

        @NotNull
        @Schema(description = "Start date", example = "2026-05-01")
        LocalDate startDate,

        @NotNull
        @Schema(description = "End date (inclusive)", example = "2026-05-31")
        LocalDate endDate,

        @Schema(description = "Optional note", nullable = true, example = "Maintenance week")
        String note,

        @Schema(description = "Creation timestamp (only in responses)", nullable = true)
        LocalDateTime createdAt
) {}
