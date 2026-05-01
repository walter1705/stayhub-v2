package edu.uniquindio.stayhub_v2.dto.accommodation;

import edu.uniquindio.stayhub_v2.model.RentalType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Accommodation summary for list and search results")
public record AccommodationSummaryResponseDTO(
        @Schema(description = "Accommodation ID", example = "1")
        Long id,

        @Schema(description = "Unique accommodation code", example = "ARM-A1B2C3D4")
        String code,

        @Schema(description = "Title", example = "Cabana en el Quindio")
        String title,

        @Schema(description = "City", example = "Armenia")
        String city,

        @Schema(description = "Maximum capacity", example = "6")
        Integer capacity,

        @Schema(description = "Currency code", example = "COP")
        String currency,

        @Schema(description = "Price per night", example = "120000.00")
        BigDecimal pricePerNight,

        @Schema(description = "Main image URL", nullable = true)
        String mainImage,

        @Schema(description = "Whether the accommodation is available for booking")
        boolean available,

        @Schema(description = "Rental type", example = "CASA_ENTERA")
        RentalType rentalType
) {}
