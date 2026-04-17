package edu.uniquindio.stayhub_v2.service;

import edu.uniquindio.stayhub_v2.dto.availability.AvailabilityCalendarResponseDTO;
import edu.uniquindio.stayhub_v2.dto.availability.AvailabilityCheckResponseDTO;
import edu.uniquindio.stayhub_v2.dto.availability.AvailabilityRuleDTO;
import edu.uniquindio.stayhub_v2.exception.AccommodationNotFoundException;
import edu.uniquindio.stayhub_v2.model.Accommodation;
import edu.uniquindio.stayhub_v2.model.AvailabilityRule;
import edu.uniquindio.stayhub_v2.model.AvailabilityRuleType;
import edu.uniquindio.stayhub_v2.model.Reservation;
import edu.uniquindio.stayhub_v2.model.ReservationStatus;
import edu.uniquindio.stayhub_v2.model.User;
import edu.uniquindio.stayhub_v2.repository.AccommodationRepository;
import edu.uniquindio.stayhub_v2.repository.AvailabilityRuleRepository;
import edu.uniquindio.stayhub_v2.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock private AccommodationRepository accommodationRepository;
    @Mock private AvailabilityRuleRepository availabilityRuleRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private UserService userService;

    @InjectMocks
    private AvailabilityService availabilityService;

    private Accommodation accommodation;
    private User host;

    @BeforeEach
    void setUp() {
        host = User.builder().id(1L).email("host@example.com").build();
        accommodation = Accommodation.builder().id(10L).host(host).build();
    }

    @Test
    void checkAvailability_NoConflicts_ReturnsAvailableTrue() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 1, 15, 0);
        LocalDateTime end = LocalDateTime.of(2026, 6, 5, 11, 0);
        when(accommodationRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(accommodation));
        when(reservationRepository.existsByAccommodationIdAndDateRangeOverlapAndStatus(
                eq(10L), any(), any(), eq(ReservationStatus.ACTIVE))).thenReturn(false);
        when(availabilityRuleRepository.findByAccommodationId(10L)).thenReturn(List.of());

        AvailabilityCheckResponseDTO result = availabilityService.checkAvailability(10L, start, end);

        assertThat(result.available()).isTrue();
        assertThat(result.accommodationId()).isEqualTo(10L);
    }

    @Test
    void checkAvailability_ActiveReservationOverlap_ReturnsNotAvailable() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 1, 15, 0);
        LocalDateTime end = LocalDateTime.of(2026, 6, 5, 11, 0);
        when(accommodationRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(accommodation));
        when(reservationRepository.existsByAccommodationIdAndDateRangeOverlapAndStatus(
                eq(10L), any(), any(), eq(ReservationStatus.ACTIVE))).thenReturn(true);

        AvailabilityCheckResponseDTO result = availabilityService.checkAvailability(10L, start, end);

        assertThat(result.available()).isFalse();
        assertThat(result.reason()).isNotNull();
    }

    @Test
    void checkAvailability_AccommodationNotFound_ThrowsException() {
        when(accommodationRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> availabilityService.checkAvailability(
                99L, LocalDateTime.now(), LocalDateTime.now().plusDays(2)))
                .isInstanceOf(AccommodationNotFoundException.class);
    }

    @Test
    void getCalendar_ValidMonth_ReturnsDayStatuses() {
        when(accommodationRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(accommodation));
        when(reservationRepository.findByAccommodationIdAndStatusAndStartDateBetween(
                eq(10L), eq(ReservationStatus.ACTIVE), any(), any())).thenReturn(List.of());
        when(availabilityRuleRepository.findByAccommodationId(10L)).thenReturn(List.of());

        AvailabilityCalendarResponseDTO result = availabilityService.getCalendar(10L, "2026-06");

        assertThat(result.accommodationId()).isEqualTo(10L);
        assertThat(result.month()).isEqualTo("2026-06");
        assertThat(result.days()).hasSize(30);
    }

    @Test
    void replaceRules_OwnerReplacesRules_DeletesAndSavesNew() {
        when(accommodationRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(accommodation));
        when(userService.getCurrentUser()).thenReturn(host);
        AvailabilityRuleDTO dto = new AvailabilityRuleDTO(null, AvailabilityRuleType.BLOCKED,
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 15), "Vacaciones", null);
        when(availabilityRuleRepository.saveAll(any())).thenReturn(List.of());

        availabilityService.replaceRules(10L, List.of(dto));

        verify(availabilityRuleRepository).deleteByAccommodationId(10L);
        verify(availabilityRuleRepository).saveAll(any());
    }

    @Test
    void listRules_ValidAccommodation_ReturnsRules() {
        AvailabilityRule rule = AvailabilityRule.builder()
                .id(1L).accommodation(accommodation)
                .type(AvailabilityRuleType.AVAILABLE)
                .startDate(LocalDate.of(2026, 5, 1))
                .endDate(LocalDate.of(2026, 5, 31))
                .build();
        when(accommodationRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(accommodation));
        when(availabilityRuleRepository.findByAccommodationId(10L)).thenReturn(List.of(rule));

        List<AvailabilityRuleDTO> result = availabilityService.listRules(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).type()).isEqualTo(AvailabilityRuleType.AVAILABLE);
    }
}
