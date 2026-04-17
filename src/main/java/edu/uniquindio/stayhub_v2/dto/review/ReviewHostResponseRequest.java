package edu.uniquindio.stayhub_v2.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Host response to a review")
public record ReviewHostResponseRequest(
        @NotBlank @Size(max = 2000) String response
) {}
