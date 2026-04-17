package edu.uniquindio.stayhub_v2.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to create a review")
public record ReviewCreateRequest(
        @NotNull Long reservationId,
        @NotNull @Min(1) @Max(5) Integer rating,
        @NotBlank @Size(max = 2000) String comment
) {}
