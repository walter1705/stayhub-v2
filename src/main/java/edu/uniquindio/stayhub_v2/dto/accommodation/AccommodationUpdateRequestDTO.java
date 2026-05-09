package edu.uniquindio.stayhub_v2.dto.accommodation;

import edu.uniquindio.stayhub_v2.model.RentalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Partial update request for an accommodation — all fields optional")
public record AccommodationUpdateRequestDTO(
        @Size(max = 100)
        String title,

        @Size(max = 1000)
        String description,

        @Min(1)
        Integer capacity,

        @Size(min = 3, max = 3)
        String currency,

        BigDecimal pricePerNight,

        String mainImage,

        Double longitude,
        Double latitude,

        @Size(max = 200)
        String locationDescription,

        @Size(max = 100)
        String city,

        List<String> images,

        Boolean available,

        @Valid
        AccommodationLegalInfoDTO legal,

        RentalType rentalType,

        @Valid
        List<AccommodationServiceDTO> services
) {}
