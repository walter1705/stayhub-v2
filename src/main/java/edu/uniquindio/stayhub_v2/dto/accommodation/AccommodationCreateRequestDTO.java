package edu.uniquindio.stayhub_v2.dto.accommodation;

import edu.uniquindio.stayhub_v2.model.RentalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Request body for creating a new accommodation")
public record AccommodationCreateRequestDTO(
        @NotBlank @Size(max = 100)
        @Schema(description = "Title", example = "Cabana en el Quindio")
        String title,

        @NotBlank @Size(max = 1000)
        @Schema(description = "Description")
        String description,

        @NotNull @Min(1)
        @Schema(description = "Maximum guests capacity", example = "6")
        Integer capacity,

        @NotBlank @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
        @Schema(description = "Currency ISO code", example = "COP")
        String currency,

        @NotNull
        @Schema(description = "Price per night", example = "120000")
        BigDecimal pricePerNight,

        @Schema(description = "Main image URL", nullable = true)
        String mainImage,

        @NotNull
        @Schema(description = "Longitude", example = "-75.6811")
        Double longitude,

        @NotNull
        @Schema(description = "Latitude", example = "4.5339")
        Double latitude,

        @NotBlank @Size(max = 200)
        @Schema(description = "Location description", example = "A dos cuadras del parque principal.")
        String locationDescription,

        @NotBlank @Size(max = 100)
        @Schema(description = "City", example = "Armenia")
        String city,

        @Schema(description = "Gallery image URLs", nullable = true)
        List<String> images,

        @Schema(description = "Whether accommodation is available on creation", defaultValue = "true")
        Boolean available,

        @NotNull @Valid
        @Schema(description = "Legal information")
        AccommodationLegalInfoDTO legal,

        @Schema(description = "Rental type", example = "CASA_ENTERA", nullable = true)
        RentalType rentalType
) {}
