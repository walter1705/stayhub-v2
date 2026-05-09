package edu.uniquindio.stayhub_v2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationServiceItem {

    @Column(name = "service_name", nullable = false, length = 80)
    private String name;

    @Column(name = "service_quantity", nullable = false)
    private Integer quantity;
}
