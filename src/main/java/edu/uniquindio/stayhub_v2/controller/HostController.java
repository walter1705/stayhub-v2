package edu.uniquindio.stayhub_v2.controller;

import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationSummaryResponseDTO;
import edu.uniquindio.stayhub_v2.service.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Accommodations", description = "Host-specific accommodation endpoints")
@RestController
@RequestMapping("/api/v2/hosts/me")
@RequiredArgsConstructor
@Slf4j
public class HostController {

    private final AccommodationService accommodationService;

    @Operation(summary = "List my accommodations", description = "Returns paginated accommodations owned by the authenticated host")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated accommodations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user is not a HOST")
    })
    @GetMapping("/accommodations")
    public ResponseEntity<Page<AccommodationSummaryResponseDTO>> listMyAccommodations(
            @RequestParam(defaultValue = "false") boolean includeDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /hosts/me/accommodations - listing host accommodations page={}", page);
        return ResponseEntity.ok(accommodationService.listMyAccommodations(page, includeDeleted));
    }
}
