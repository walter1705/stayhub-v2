package edu.uniquindio.stayhub_v2.dto.accommodation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Legal information required to publish an accommodation")
public record AccommodationLegalInfoDTO(
        @NotBlank(message = "Registration number is required")
        @Schema(description = "Legal registration/license number", example = "RNT-123456")
        String registrationNumber,

        @NotBlank(message = "Address line 1 is required")
        @Schema(description = "Physical address", example = "Calle 10 # 20-30")
        String addressLine1,

        @Schema(description = "Postal code", nullable = true, example = "630001")
        String postalCode,

        @NotBlank(message = "Country is required")
        @Size(min = 2, max = 2, message = "Country must be a 2-letter ISO code")
        @Schema(description = "2-letter ISO country code", example = "CO")
        String country,

        @Schema(description = "When terms were accepted", nullable = true)
        LocalDateTime acceptedTermsAt
) {}
