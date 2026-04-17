package edu.uniquindio.stayhub_v2.controller;

import edu.uniquindio.stayhub_v2.dto.auth.MessageResponseDTO;
import edu.uniquindio.stayhub_v2.dto.room.RoomCreateRequestDTO;
import edu.uniquindio.stayhub_v2.dto.room.RoomResponseDTO;
import edu.uniquindio.stayhub_v2.dto.room.RoomUpdateRequestDTO;
import edu.uniquindio.stayhub_v2.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Rooms", description = "Room management within an accommodation")
@RestController
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "List rooms for an accommodation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rooms list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    @GetMapping("/api/v2/accommodations/{id}/rooms")
    public ResponseEntity<List<RoomResponseDTO>> listRooms(@PathVariable Long id) {
        log.info("GET /accommodations/{}/rooms", id);
        return ResponseEntity.ok(roomService.listRooms(id));
    }

    @Operation(summary = "Create a room", description = "Creates a room and generates a unique code within the accommodation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Room created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Not the accommodation owner"),
            @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    @PostMapping("/api/v2/accommodations/{id}/rooms")
    public ResponseEntity<RoomResponseDTO> createRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomCreateRequestDTO requestDTO) {
        log.info("POST /accommodations/{}/rooms", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(id, requestDTO));
    }

    @Operation(summary = "Update a room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Not the accommodation owner"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @PutMapping("/api/v2/rooms/{roomId}")
    public ResponseEntity<RoomResponseDTO> updateRoom(
            @PathVariable Long roomId,
            @Valid @RequestBody RoomUpdateRequestDTO requestDTO) {
        log.info("PUT /rooms/{}", roomId);
        return ResponseEntity.ok(roomService.updateRoom(roomId, requestDTO));
    }

    @Operation(summary = "Delete a room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Not the accommodation owner"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @DeleteMapping("/api/v2/rooms/{roomId}")
    public ResponseEntity<MessageResponseDTO> deleteRoom(@PathVariable Long roomId) {
        log.info("DELETE /rooms/{}", roomId);
        roomService.deleteRoom(roomId);
        return ResponseEntity.ok(new MessageResponseDTO("Habitación eliminada exitosamente"));
    }
}
