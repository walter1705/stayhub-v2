package edu.uniquindio.stayhub_v2.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Optional cancellation reason")
public record CancelReservationRequestDTO(
        @Size(max = 200)
        @Schema(description = "Reason for cancellation", nullable = true)
        String reason
) {}
