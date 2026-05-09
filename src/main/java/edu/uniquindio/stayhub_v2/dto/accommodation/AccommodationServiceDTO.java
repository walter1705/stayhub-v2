package edu.uniquindio.stayhub_v2.dto.accommodation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Named accommodation service with quantity")
public record AccommodationServiceDTO(
        @NotBlank
        @Size(max = 80)
        @Schema(description = "Service name", example = "Habitaciones")
        String name,

        @Min(1)
        @Schema(description = "Quantity available for the service", example = "3")
        Integer quantity
) {}
