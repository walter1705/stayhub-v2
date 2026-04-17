package edu.uniquindio.stayhub_v2.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Public user information exposed in nested resources")
public record UserPublicDTO(
        @Schema(description = "User ID", example = "1")
        Long id,

        @Schema(description = "Full name", example = "Carlos Ramirez")
        String fullName,

        @Schema(description = "Email address", example = "carlos.ramirez@email.com")
        String email,

        @Schema(description = "Profile picture URL", nullable = true)
        String profilePicture
) {}
