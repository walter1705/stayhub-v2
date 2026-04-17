package edu.uniquindio.stayhub_v2.dto.room;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Partial update request for a room — all fields optional")
public record RoomUpdateRequestDTO(
        @Size(max = 100)
        String name,

        @Min(1)
        Integer capacity,

        List<String> images
) {}
