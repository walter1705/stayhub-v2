package edu.uniquindio.stayhub_v2.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Public user profile")
public record UserPublicDTO(
        @Schema(description = "User ID") Long id,
        @Schema(description = "Full name") String fullName,
        @Schema(description = "Email") String email,
        @Schema(description = "Profile picture URL", nullable = true) String profilePicture
) {}
