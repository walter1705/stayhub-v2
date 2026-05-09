package edu.uniquindio.stayhub_v2.dto.accommodation;

import edu.uniquindio.stayhub_v2.dto.user.UserPublicDTO;
import edu.uniquindio.stayhub_v2.model.RentalType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Full accommodation detail including host, location, legal, and media")
public record AccommodationDetailResponseDTO(
        Long id,
        String code,
        String title,
        String city,
        Integer capacity,
        String currency,
        BigDecimal pricePerNight,
        String mainImage,
        boolean available,
        String description,
        UserPublicDTO host,
        Double longitude,
        Double latitude,
        String locationDescription,
        List<String> images,
        AccommodationLegalInfoDTO legal,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        RentalType rentalType,
        List<AccommodationServiceDTO> services
) {}
