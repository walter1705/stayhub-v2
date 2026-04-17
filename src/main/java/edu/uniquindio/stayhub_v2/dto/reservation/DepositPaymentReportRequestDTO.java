package edu.uniquindio.stayhub_v2.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Report of a deposit payment made by the guest")
public record DepositPaymentReportRequestDTO(
        @NotNull
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        @Schema(description = "Paid amount", example = "200.00")
        BigDecimal amount,

        @NotNull @Size(min = 3, max = 3)
        @Schema(description = "Currency code", example = "COP")
        String currency,

        @Schema(description = "Payment reference", nullable = true, example = "TRX-009182")
        String reference,

        @Schema(description = "Payment datetime", nullable = true)
        LocalDateTime paidAt,

        @Schema(description = "Proof of payment URL", nullable = true)
        String proofUrl
) {}
