package edu.uniquindio.stayhub_v2.controller;

import edu.uniquindio.stayhub_v2.dto.auth.MessageResponseDTO;
import edu.uniquindio.stayhub_v2.dto.rentalpackage.RentalPackageCreateRequest;
import edu.uniquindio.stayhub_v2.dto.rentalpackage.RentalPackageDTO;
import edu.uniquindio.stayhub_v2.dto.rentalpackage.RentalPackageUpdateRequest;
import edu.uniquindio.stayhub_v2.service.RentalPackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@Tag(name = "Rental Packages", description = "Manage rental packages for accommodations")
@RestController
@RequiredArgsConstructor
public class RentalPackageController {

    private final RentalPackageService rentalPackageService;

    @Operation(summary = "List rental packages for an accommodation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Packages list", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalPackageDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    @GetMapping("/api/v2/accommodations/{id}/rental-packages")
    public ResponseEntity<List<RentalPackageDTO>> list(@PathVariable Long id) {
        return ResponseEntity.ok(rentalPackageService.list(id));
    }

    @Operation(summary = "Create a rental package for an accommodation")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Package created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalPackageDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    @PostMapping("/api/v2/accommodations/{id}/rental-packages")
    public ResponseEntity<RentalPackageDTO> create(
            @PathVariable Long id,
            @Valid @RequestBody RentalPackageCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rentalPackageService.create(id, request));
    }

    @Operation(summary = "Update a rental package")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Package updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalPackageDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    @PutMapping("/api/v2/rental-packages/{packageId}")
    public ResponseEntity<RentalPackageDTO> update(
            @PathVariable Long packageId,
            @Valid @RequestBody RentalPackageUpdateRequest request) {
        return ResponseEntity.ok(rentalPackageService.update(packageId, request));
    }

    @Operation(summary = "Delete a rental package")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Package deleted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    @DeleteMapping("/api/v2/rental-packages/{packageId}")
    public ResponseEntity<MessageResponseDTO> delete(@PathVariable Long packageId) {
        rentalPackageService.delete(packageId);
        return ResponseEntity.ok(new MessageResponseDTO("Rental package deleted."));
    }
}
