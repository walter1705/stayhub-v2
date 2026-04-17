package edu.uniquindio.stayhub_v2.dto.metrics;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Host metrics summary")
public record HostMetricsResponse(
        @Schema(description = "Start date filter (inclusive)", nullable = true) LocalDate from,
        @Schema(description = "End date filter (inclusive)", nullable = true) LocalDate to,
        @Schema(description = "Total accommodations owned") int accommodationsCount,
        @Schema(description = "Total reservations in period") int reservationsCount,
        @Schema(description = "Occupancy rate (0.0 - 1.0)", example = "0.62") double occupancyRate,
        @Schema(description = "Total revenue in period", example = "4500000") double revenueTotal,
        @Schema(description = "Currency code", example = "COP") String currency,
        @Schema(description = "Average review rating", nullable = true) Double averageRating
) {}
