package edu.uniquindio.stayhub_v2.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Review details")
public record ReviewDTO(
        @Schema(description = "Review ID") Long id,
        @Schema(description = "Accommodation ID") Long accommodationId,
        @Schema(description = "Reservation ID") Long reservationId,
        @Schema(description = "Review author") UserPublicDTO author,
        @Schema(description = "Rating (1-5)", minimum = "1", maximum = "5") int rating,
        @Schema(description = "Review comment") String comment,
        @Schema(description = "Creation timestamp") LocalDateTime createdAt,
        @Schema(description = "Host response", nullable = true) String hostResponse,
        @Schema(description = "Host response timestamp", nullable = true) LocalDateTime hostResponseAt
) {}
