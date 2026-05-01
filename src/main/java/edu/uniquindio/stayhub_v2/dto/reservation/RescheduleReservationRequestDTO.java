package edu.uniquindio.stayhub_v2.dto.reservation;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record RescheduleReservationRequestDTO(
        @NotNull @Future LocalDateTime startDate,
        @NotNull LocalDateTime endDate
) {}
