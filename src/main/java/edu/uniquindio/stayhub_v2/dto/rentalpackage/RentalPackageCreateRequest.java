package edu.uniquindio.stayhub_v2.dto.rentalpackage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Request to create a rental package")
public record RentalPackageCreateRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 500) String description,
        @NotNull @Min(1) Integer minNights,
        @Min(1) Integer maxNights,
        @NotNull @Positive BigDecimal price,
        @NotBlank String currency,
        boolean active
) {}
