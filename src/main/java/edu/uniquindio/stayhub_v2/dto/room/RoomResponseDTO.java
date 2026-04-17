package edu.uniquindio.stayhub_v2.dto.room;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Room detail response")
public record RoomResponseDTO(
        @Schema(description = "Room ID", example = "10")
        Long id,

        @Schema(description = "Accommodation ID", example = "1")
        Long accommodationId,

        @Schema(description = "Unique room code", example = "ARM-A1B2C3D4-R01")
        String code,

        @Schema(description = "Room name", example = "Habitacion principal")
        String name,

        @Schema(description = "Maximum capacity", example = "2")
        Integer capacity,

        @Schema(description = "Gallery image URLs")
        List<String> images
) {}
