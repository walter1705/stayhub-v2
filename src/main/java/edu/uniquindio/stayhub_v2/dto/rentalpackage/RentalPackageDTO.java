package edu.uniquindio.stayhub_v2.dto.rentalpackage;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Rental package details")
public record RentalPackageDTO(
        @Schema(description = "Package ID") Long id,
        @Schema(description = "Accommodation ID") Long accommodationId,
        @Schema(description = "Package name", example = "Semana completa") String name,
        @Schema(description = "Package description", nullable = true) String description,
        @Schema(description = "Minimum nights", example = "7") int minNights,
        @Schema(description = "Maximum nights", nullable = true) Integer maxNights,
        @Schema(description = "Package price", example = "700000") BigDecimal price,
        @Schema(description = "Currency code", example = "COP") String currency,
        @Schema(description = "Whether the package is active") boolean active
) {}
