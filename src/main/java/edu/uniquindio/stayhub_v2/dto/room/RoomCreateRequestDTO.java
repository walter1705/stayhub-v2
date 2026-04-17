package edu.uniquindio.stayhub_v2.dto.room;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Request body for creating a room")
public record RoomCreateRequestDTO(
        @NotBlank @Size(max = 100)
        @Schema(description = "Room name", example = "Habitacion principal")
        String name,

        @NotNull @Min(1)
        @Schema(description = "Maximum capacity", example = "2")
        Integer capacity,

        @Schema(description = "Gallery image URLs", nullable = true)
        List<String> images
) {}
