package edu.uniquindio.stayhub_v2.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

@Schema(description = "Partial user profile update request — all fields optional")
public record UserUpdateRequestDTO(
        @Size(max = 100, message = "Full name must not exceed 100 characters")
        @Schema(description = "Full name", example = "Carlos Ramirez")
        String fullName,

        @Pattern(regexp = "^\\+57\\s?\\d{10}$", message = "Phone number must follow Colombian format: +57 followed by 10 digits")
        @Schema(description = "Phone number in Colombian format", example = "+573101234567")
        String phoneNumber,

        @URL(message = "Profile picture must be a valid URL")
        @Schema(description = "Profile picture URL", nullable = true)
        String profilePicture
) {}
