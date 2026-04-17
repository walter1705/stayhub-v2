package edu.uniquindio.stayhub_v2.controller;

import edu.uniquindio.stayhub_v2.dto.metrics.HostMetricsResponse;
import edu.uniquindio.stayhub_v2.service.HostMetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Metrics", description = "Host performance metrics")
@RestController
@RequestMapping("/api/v2/hosts/me")
@RequiredArgsConstructor
public class HostMetricsController {

    private final HostMetricsService hostMetricsService;

    @Operation(summary = "Get host metrics", description = "Basic metrics for accommodations owned by the authenticated host.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Metrics", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HostMetricsResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/metrics")
    public ResponseEntity<HostMetricsResponse> getMetrics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(hostMetricsService.getMetrics(from, to));
    }
}
