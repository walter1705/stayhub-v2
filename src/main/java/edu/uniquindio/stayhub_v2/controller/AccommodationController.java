package edu.uniquindio.stayhub_v2.controller;

import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationCreateRequestDTO;
import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationDetailResponseDTO;
import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationGetByIdResponseDTO;
import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationSummaryResponseDTO;
import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationUpdateRequestDTO;
import edu.uniquindio.stayhub_v2.dto.auth.MessageResponseDTO;
import edu.uniquindio.stayhub_v2.service.AccommodationService;
import edu.uniquindio.stayhub_v2.service.JWTService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Tag(name = "Accommodation management", description = "Endpoints for managing accommodations")
@RestController
@RequestMapping("/api/v2/accommodations")
@RequiredArgsConstructor
@Slf4j
public class AccommodationController {

    private final AccommodationService accommodationService;
    private final JWTService jwtService;

    @Operation(summary = "Create an accommodation", description = "Host creates a new rural house listing. The backend generates a unique code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Accommodation created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<AccommodationDetailResponseDTO> createAccommodation(
            @Valid @RequestBody AccommodationCreateRequestDTO requestDTO) {
        log.info("POST /accommodations - creating accommodation");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accommodationService.createAccommodation(requestDTO));
    }

    @Operation(summary = "Search accommodations", description = "Search by city, free text, capacity and date availability")
    @GetMapping
    public ResponseEntity<Page<AccommodationSummaryResponseDTO>> searchAccommodations(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer guests,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /accommodations - search city={} q={} guests={}", city, q, guests);
        return ResponseEntity.ok(
                accommodationService.searchAccommodations(city, q, guests, startDate, endDate, page, size));
    }

    @Operation(summary = "Get accommodation by code", description = "Retrieves an accommodation by its unique code")
    @GetMapping("/by-code/{code}")
    public ResponseEntity<AccommodationDetailResponseDTO> getAccommodationByCode(
            @PathVariable String code) {
        log.info("GET /accommodations/by-code/{}", code);
        return ResponseEntity.ok(accommodationService.getAccommodationByCode(code));
    }

    @Operation(summary = "Update accommodation", description = "Host updates their accommodation. Validates ownership.")
    @PutMapping("/{id}")
    public ResponseEntity<AccommodationDetailResponseDTO> updateAccommodation(
            @PathVariable Long id,
            @Valid @RequestBody AccommodationUpdateRequestDTO requestDTO,
            @RequestHeader("Authorization") String token) {
        String requesterEmail = jwtService.getEmailFromToken(token);
        log.info("PUT /accommodations/{} by {}", id, requesterEmail);
        return ResponseEntity.ok(accommodationService.updateAccommodation(id, requestDTO, requesterEmail));
    }

    @Operation(summary = "Get accommodation by ID", description = "Retrieves an accommodation by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accommodation retrieved successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AccommodationGetByIdResponseDTO.class),
                        examples = @ExampleObject(
                                name = "Accommodation retrieved successfully",
                                value = "{\\\"host\\\": {\\\"id\\\": 1, \\\"name\\\": \\\"Carlos Ramírez\\\", \\\"email\\\": \\\"carlos.ramirez@email.com\\\"}, \\\"title\\\": \\\"Acogedor apartamento en el centro histórico\\\", \\\"description\\\": \\\"Hermoso apartamento completamente amoblado con vista a la plaza principal.\\\", \\\"capacity\\\": 3, \\\"pricePerNight\\\": 120000.00, \\\"mainImage\\\": \\\"https://images.example.com/accommodations/main/apt-centro-001.jpg\\\", \\\"locationDescription\\\": \\\"A dos cuadras del parque principal, cerca de restaurantes y tiendas\\\", \\\"city\\\": \\\"Armenia\\\", \\\"images\\\": [\\\"https://images.example.com/accommodations/gallery/apt-centro-001-sala.jpg\\\", \\\"https://images.example.com/accommodations/gallery/apt-centro-001-cocina.jpg\\\", \\\"https://images.example.com/accommodations/gallery/apt-centro-001-habitacion.jpg\\\"], \\\"available\\\": true}"
                        )
                )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccommodationGetByIdResponseDTO> getAccommodation(
            @PathVariable @Parameter(description = "Accommodation ID", required = true) Long id) {
        log.info("Retrieving accommodation with ID: {}", id);
        AccommodationGetByIdResponseDTO response = accommodationService.getAccommodation(id);
        log.debug("Accommodation retrieved successfully with title: {}", response.title());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Deactivate accommodation", description = "Soft deletes an accommodation if it has no future active reservations, validating if user is the owner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accommodation deactivated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDTO.class),
                            examples = @ExampleObject(
                                    value = "{\\\"message\\\": \\\"Alojamiento dado de baja con éxito.\\\"}")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Active future reservations exist",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = edu.uniquindio.stayhub_v2.dto.auth.Error.class)
                    )
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized action, user is not the owner",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = edu.uniquindio.stayhub_v2.dto.auth.Error.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Accommodation not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = edu.uniquindio.stayhub_v2.dto.auth.Error.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> deactivateAccommodation(
            @PathVariable @Parameter(description = "Accommodation ID", required = true) Long id,
            @RequestHeader("Authorization") String token) {
        log.info("Request to deactivate accommodation with ID: {}", id);
        String requesterEmail = jwtService.getEmailFromToken(token);
        accommodationService.deactivateAccommodation(id, requesterEmail);
        return new ResponseEntity<>(new MessageResponseDTO("Alojamiento dado de baja con éxito."), HttpStatus.OK);
    }
}