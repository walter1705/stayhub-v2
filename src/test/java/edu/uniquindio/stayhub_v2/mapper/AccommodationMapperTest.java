package edu.uniquindio.stayhub_v2.mapper;

import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationCreateRequestDTO;
import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationLegalInfoDTO;
import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationServiceDTO;
import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationSummaryResponseDTO;
import edu.uniquindio.stayhub_v2.model.Accommodation;
import edu.uniquindio.stayhub_v2.model.AccommodationLegalInfo;
import edu.uniquindio.stayhub_v2.model.AccommodationServiceItem;
import edu.uniquindio.stayhub_v2.model.RentalType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AccommodationMapperTest {

    private final AccommodationMapper mapper = Mappers.getMapper(AccommodationMapper.class);

    @Test
    void toEntity_MapsServicesFromCreateRequest() {
        AccommodationCreateRequestDTO dto = new AccommodationCreateRequestDTO(
                "Finca",
                "Descripcion",
                8,
                "COP",
                BigDecimal.valueOf(180000),
                null,
                -75.6,
                4.5,
                "Cerca al parque",
                "Armenia",
                List.of(),
                true,
                new AccommodationLegalInfoDTO("RNT-1", "Calle 1", null, "CO", null),
                RentalType.AMBAS,
                List.of(
                        new AccommodationServiceDTO("Habitaciones", 4),
                        new AccommodationServiceDTO("Banos", 3),
                        new AccommodationServiceDTO("Jacuzzis", 1))
        );

        Accommodation entity = mapper.toEntity(dto);

        assertThat(entity.getServices())
                .extracting(AccommodationServiceItem::getName, AccommodationServiceItem::getQuantity)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("Habitaciones", 4),
                        org.assertj.core.groups.Tuple.tuple("Banos", 3),
                        org.assertj.core.groups.Tuple.tuple("Jacuzzis", 1));
    }

    @Test
    void toSummaryDTO_MapsServicesToResponse() {
        Accommodation accommodation = Accommodation.builder()
                .id(10L)
                .code("ARM-123")
                .title("Finca")
                .city("Armenia")
                .capacity(8)
                .currency(Currency.getInstance("COP"))
                .pricePerNight(BigDecimal.valueOf(180000))
                .available(true)
                .rentalType(RentalType.CASA_ENTERA)
                .legal(new AccommodationLegalInfo("RNT-1", "Calle 1", null, "CO", null))
                .services(List.of(
                        new AccommodationServiceItem("Cocinas", 2),
                        new AccommodationServiceItem("Saunas", 1)))
                .build();

        AccommodationSummaryResponseDTO dto = mapper.toSummaryDTO(accommodation);

        assertThat(dto.services())
                .extracting(AccommodationServiceDTO::name, AccommodationServiceDTO::quantity)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("Cocinas", 2),
                        org.assertj.core.groups.Tuple.tuple("Saunas", 1));
    }
}
