package edu.uniquindio.stayhub_v2.dto.user;

import edu.uniquindio.stayhub_v2.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Authenticated user profile response")
public record UserMeResponseDTO(
        @Schema(description = "User ID", example = "1")
        Long id,

        @Schema(description = "Full name", example = "Carlos Ramirez")
        String fullName,

        @Schema(description = "Email address", example = "carlos.ramirez@email.com")
        String email,

        @Schema(description = "Profile picture URL", nullable = true)
        String profilePicture,

        @Schema(description = "Assigned roles")
        Set<Role> roles,

        @Schema(description = "Phone number", example = "+573101234567")
        String phoneNumber,

        @Schema(description = "Birth date", example = "1990-01-01")
        LocalDate birthDate,

        @Schema(description = "Account creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Last update timestamp")
        LocalDateTime updatedAt
) {}
