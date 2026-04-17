package edu.uniquindio.stayhub_v2.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Payment summary for a reservation — deposit details and deadline")
public record ReservationPaymentSummaryDTO(
        @Schema(description = "Reservation ID")
        Long reservationId,

        @Schema(description = "Booking number", example = "SH-2026-000001")
        String bookingNumber,

        @Schema(description = "Total price", example = "1000.00")
        BigDecimal totalPrice,

        @Schema(description = "Currency code", example = "COP")
        String currency,

        @Schema(description = "Required deposit (20% of total)", example = "200.00")
        BigDecimal depositAmount,

        @Schema(description = "Whether the deposit has been paid")
        boolean depositPaid,

        @Schema(description = "Deposit payment deadline")
        LocalDateTime paymentDeadline,

        @Schema(description = "Bank account number to transfer the deposit", example = "3001234567890")
        String bankAccountNumber,

        @Schema(description = "Whether the payment deadline has passed without deposit being paid")
        boolean overdue
) {}
