package edu.uniquindio.stayhub_v2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationLegalInfo {

    @Column(name = "legal_registration_number")
    private String registrationNumber;

    @Column(name = "legal_address_line1")
    private String addressLine1;

    @Column(name = "legal_postal_code")
    private String postalCode;

    @Column(name = "legal_country")
    private String country;

    @Column(name = "legal_accepted_terms_at")
    private LocalDateTime acceptedTermsAt;
}
