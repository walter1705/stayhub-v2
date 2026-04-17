package edu.uniquindio.stayhub_v2.controller;

import edu.uniquindio.stayhub_v2.dto.review.ReviewCreateRequest;
import edu.uniquindio.stayhub_v2.dto.review.ReviewDTO;
import edu.uniquindio.stayhub_v2.dto.review.ReviewHostResponseRequest;
import edu.uniquindio.stayhub_v2.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Reviews", description = "Guest reviews and host responses")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Create a review", description = "Only allowed if the reservation is COMPLETED.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Review created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Reservation not found"),
            @ApiResponse(responseCode = "409", description = "Reservation not eligible for review")
    })
    @PostMapping("/api/v2/reviews")
    public ResponseEntity<ReviewDTO> create(@Valid @RequestBody ReviewCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(request));
    }

    @Operation(summary = "List reviews for an accommodation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reviews page", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    @GetMapping("/api/v2/accommodations/{id}/reviews")
    public ResponseEntity<Page<ReviewDTO>> list(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.list(id, page, size));
    }

    @Operation(summary = "Host response to a review")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated review", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @PostMapping("/api/v2/reviews/{reviewId}/response")
    public ResponseEntity<ReviewDTO> respond(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewHostResponseRequest request) {
        return ResponseEntity.ok(reviewService.respond(reviewId, request));
    }
}
