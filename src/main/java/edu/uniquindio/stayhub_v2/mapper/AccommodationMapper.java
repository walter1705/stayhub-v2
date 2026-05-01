package edu.uniquindio.stayhub_v2.mapper;

import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationCreateRequestDTO;
import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationDetailResponseDTO;
import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationGetByIdResponseDTO;
import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationLegalInfoDTO;
import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationSummaryResponseDTO;
import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationUpdateRequestDTO;
import edu.uniquindio.stayhub_v2.model.Accommodation;
import edu.uniquindio.stayhub_v2.model.AccommodationLegalInfo;
import edu.uniquindio.stayhub_v2.model.RentalType;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Currency;

/**
 * Mapper interface for converting Accommodation entities to DTOs.
 *
 * <p>This mapper uses MapStruct to generate type-safe mapping code at compile time.
 * The {@code componentModel = "spring"} configuration makes the generated
 * implementation a Spring bean that can be injected via {@code @Autowired}.</p>
 *
 * <p><b>Usage Example:</b></p>
 * <pre>{@code
 * @Service
 * public class AccommodationService {
 *
 *     private final AccommodationMapper accommodationMapper;
 *
 *     public AccommodationGetByIdResponseDTO getAccommodation(Long id) {
 *         Accommodation accommodation = accommodationRepository.findById(id)
 *                 .orElseThrow(() -> new AccommodationNotFoundException("Not found"));
 *         return accommodationMapper.toAccommodationGetByIdResponseDTO(accommodation);
 *     }
 * }
 * }</pre>
 *
 * <p><b>Mapping Strategy:</b></p>
 * Fields with matching names are automatically mapped. Custom mappings
 * can be defined using {@code @Mapping} annotations when field names differ
 * or when nested object traversal is required.</p>
 *
 * @author Esteban Gómez León
 * @version 1.0
 * @since 1.0
 * @see org.mapstruct.Mapper
 * @see Accommodation
 * @see AccommodationGetByIdResponseDTO
 */
@Mapper(componentModel = "spring")
public interface AccommodationMapper {

    // ── Currency ↔ String helpers ────────────────────────────────────────────

    default String currencyToString(Currency currency) {
        return currency == null ? null : currency.getCurrencyCode();
    }

    default Currency stringToCurrency(String code) {
        return code == null ? null : Currency.getInstance(code);
    }

    // ── Detail / Summary ─────────────────────────────────────────────────────

    @Mapping(target = "currency", expression = "java(currencyToString(accommodation.getCurrency()))")
    AccommodationDetailResponseDTO toDetailDTO(Accommodation accommodation);

    @Mapping(target = "currency", expression = "java(currencyToString(accommodation.getCurrency()))")
    AccommodationSummaryResponseDTO toSummaryDTO(Accommodation accommodation);

    // ── Create / Update ──────────────────────────────────────────────────────

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "currency", expression = "java(stringToCurrency(dto.currency()))")
    @Mapping(target = "available", expression = "java(dto.available() != null ? dto.available() : true)")
    @Mapping(target = "rentalType", expression = "java(dto.rentalType() != null ? dto.rentalType() : RentalType.CASA_ENTERA)")
    Accommodation toEntity(AccommodationCreateRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "currency", expression = "java(dto.currency() != null ? stringToCurrency(dto.currency()) : accommodation.getCurrency())")
    void updateFromDto(AccommodationUpdateRequestDTO dto, @MappingTarget Accommodation accommodation);

    AccommodationLegalInfoDTO toLegalInfoDTO(AccommodationLegalInfo legal);

    AccommodationLegalInfo toLegalInfo(AccommodationLegalInfoDTO dto);

    /**
     * Maps an Accommodation entity to an AccommodationGetByIdResponseDTO.
     *
     * <p>This method performs an automatic field-by-field mapping. Fields
     * with identical names in the source and target types are copied
     * automatically. Nested objects are mapped recursively if corresponding
     * mapper methods exist.</p>
     *
     * <p><b>Mapped Fields:</b></p>
     * <ul>
     *   <li>All primitive and String fields with matching names</li>
     *   <li>Date/time fields with matching types</li>
     *   <li>Enumerations with matching names</li>
     * </ul>
     *
     * @param accommodation The Accommodation entity to be mapped (must not be null)
     * @return AccommodationGetByIdResponseDTO containing the mapped data
     */
    AccommodationGetByIdResponseDTO toAccommodationGetByIdResponseDTO(Accommodation accommodation);
}