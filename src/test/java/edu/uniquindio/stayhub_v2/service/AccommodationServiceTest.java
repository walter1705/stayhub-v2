package edu.uniquindio.stayhub_v2.service;

import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationCreateRequestDTO;
import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationDetailResponseDTO;
import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationGetByIdResponseDTO;
import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationLegalInfoDTO;
import edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationSummaryResponseDTO;
import edu.uniquindio.stayhub_v2.exception.AccommodationNotFoundException;
import edu.uniquindio.stayhub_v2.exception.ActiveReservationsException;
import edu.uniquindio.stayhub_v2.exception.UnauthorizedHostException;
import edu.uniquindio.stayhub_v2.mapper.AccommodationMapper;
import edu.uniquindio.stayhub_v2.model.Accommodation;
import edu.uniquindio.stayhub_v2.model.ReservationStatus;
import edu.uniquindio.stayhub_v2.model.User;
import edu.uniquindio.stayhub_v2.repository.AccommodationRepository;
import edu.uniquindio.stayhub_v2.repository.ReservationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccommodationServiceTest {

        @Mock
        private AccommodationRepository accommodationRepository;

        @Mock
        private ReservationRepository reservationRepository;

        @Mock
        private AccommodationMapper accommodationMapper;

        @Mock
        private edu.uniquindio.stayhub_v2.service.UserService userService;

        @InjectMocks
        private AccommodationService accommodationService;

        private Accommodation testAccommodation;
        private User hostUser;

        @BeforeEach
        void setUp() {
                hostUser = User.builder()
                                .id(1L)
                                .email("host@example.com")
                                .build();

                testAccommodation = Accommodation.builder()
                                .id(100L)
                                .host(hostUser)
                                .deleted(false)
                                .available(true)
                                .build();
        }

        @AfterEach
        void tearDown() {
                SecurityContextHolder.clearContext();
        }

        private void authenticateAs(String email) {
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                                .withUsername(email).password("encoded").roles("HOST").build();
                UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
        }

        @Test
        void deactivateAccommodation_Successful() {
                when(accommodationRepository.findByIdAndDeletedFalse(100L)).thenReturn(Optional.of(testAccommodation));
                when(reservationRepository.existsByAccommodationIdAndStartDateAfterAndStatus(
                                eq(100L), any(LocalDateTime.class), eq(ReservationStatus.ACTIVE))).thenReturn(false);

                accommodationService.deactivateAccommodation(100L, "host@example.com");

                assertThat(testAccommodation.isDeleted()).isTrue();
                assertThat(testAccommodation.isAvailable()).isFalse();
                verify(accommodationRepository).save(testAccommodation);
        }

        @Test
        void deactivateAccommodation_UnauthorizedHost_ThrowsException() {
                when(accommodationRepository.findByIdAndDeletedFalse(100L)).thenReturn(Optional.of(testAccommodation));

                assertThatThrownBy(() -> accommodationService.deactivateAccommodation(100L, "otheruser@example.com"))
                                .isInstanceOf(UnauthorizedHostException.class)
                                .hasMessageContaining("No tienes permisos para dar de baja esta casa rural.");

                assertThat(testAccommodation.isDeleted()).isFalse();
                verify(reservationRepository, never()).existsByAccommodationIdAndStartDateAfterAndStatus(any(), any(),
                                any());
                verify(accommodationRepository, never()).save(any());
        }

        @Test
        void deactivateAccommodation_ActiveReservationsExist_ThrowsException() {
                when(accommodationRepository.findByIdAndDeletedFalse(100L)).thenReturn(Optional.of(testAccommodation));
                when(reservationRepository.existsByAccommodationIdAndStartDateAfterAndStatus(
                                eq(100L), any(LocalDateTime.class), eq(ReservationStatus.ACTIVE))).thenReturn(true);

                assertThatThrownBy(() -> accommodationService.deactivateAccommodation(100L, "host@example.com"))
                                .isInstanceOf(ActiveReservationsException.class)
                                .hasMessageContaining("reservas futuras");

                assertThat(testAccommodation.isDeleted()).isFalse();
                verify(accommodationRepository, never()).save(any());
        }

        @Test
        void deactivateAccommodation_NotFound_ThrowsException() {
                when(accommodationRepository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> accommodationService.deactivateAccommodation(999L, "host@example.com"))
                                .isInstanceOf(AccommodationNotFoundException.class);
        }

        @Test
        void getAccommodation_Successful() {
                AccommodationGetByIdResponseDTO mockResponse = new AccommodationGetByIdResponseDTO(
                                null, "Title", "Desc", 4, new java.math.BigDecimal("100"), "main.jpg", "loc", "city",
                                java.util.List.of(), true);
                when(accommodationRepository.findByIdAndDeletedFalse(100L)).thenReturn(Optional.of(testAccommodation));
                when(accommodationMapper.toAccommodationGetByIdResponseDTO(testAccommodation)).thenReturn(mockResponse);

                AccommodationGetByIdResponseDTO response = accommodationService.getAccommodation(100L);

                assertThat(response).isNotNull();
                assertThat(response.title()).isEqualTo("Title");
        }

        @Test
        void getAccommodation_NotFound_ThrowsException() {
                when(accommodationRepository.findByIdAndDeletedFalse(999L))
                                .thenReturn(Optional.empty());

                assertThatThrownBy(() -> accommodationService.getAccommodation(999L))
                                .isInstanceOf(AccommodationNotFoundException.class)
                                .hasMessageContaining("Accommodation not found with id");
        }

        @Test
        void createAccommodation_ValidRequest_SavesAndReturnsDetail() {
                authenticateAs("host@example.com");
                AccommodationLegalInfoDTO legalDTO = new AccommodationLegalInfoDTO("RNT-001", "Calle 1", null, "CO", null);
                AccommodationCreateRequestDTO request = new AccommodationCreateRequestDTO(
                                "Test", "Desc", 4, "COP", BigDecimal.valueOf(100000),
                                null, -75.0, 4.5, "Near park", "Armenia",
                                null, true, legalDTO);

                when(userService.getCurrentUser()).thenReturn(hostUser);
                when(accommodationRepository.save(any(Accommodation.class))).thenReturn(testAccommodation);
                AccommodationDetailResponseDTO mockDetail = new AccommodationDetailResponseDTO(
                                100L, "ARM-ABC123", "Test", "Armenia", 4, "COP",
                                BigDecimal.valueOf(100000), null, true, "Desc",
                                null, -75.0, 4.5, "Near park", List.of(), null, null, null);
                when(accommodationMapper.toDetailDTO(testAccommodation)).thenReturn(mockDetail);
                when(accommodationMapper.toEntity(request)).thenReturn(testAccommodation);

                AccommodationDetailResponseDTO result = accommodationService.createAccommodation(request);

                assertThat(result).isNotNull();
                assertThat(result.id()).isEqualTo(100L);
                verify(accommodationRepository).save(any(Accommodation.class));
        }

        @Test
        void getAccommodationByCode_Found_ReturnsDetail() {
                when(accommodationRepository.findByCodeAndDeletedFalse("ARM-ABC123")).thenReturn(Optional.of(testAccommodation));
                AccommodationDetailResponseDTO mockDetail = new AccommodationDetailResponseDTO(
                                100L, "ARM-ABC123", "Test", "Armenia", 4, "COP",
                                BigDecimal.valueOf(100000), null, true, "Desc",
                                null, -75.0, 4.5, "Near park", List.of(), null, null, null);
                when(accommodationMapper.toDetailDTO(testAccommodation)).thenReturn(mockDetail);

                AccommodationDetailResponseDTO result = accommodationService.getAccommodationByCode("ARM-ABC123");

                assertThat(result.code()).isEqualTo("ARM-ABC123");
        }

        @Test
        void getAccommodationByCode_NotFound_ThrowsException() {
                when(accommodationRepository.findByCodeAndDeletedFalse("NOPE")).thenReturn(Optional.empty());

                assertThatThrownBy(() -> accommodationService.getAccommodationByCode("NOPE"))
                                .isInstanceOf(AccommodationNotFoundException.class);
        }

        @Test
        void updateAccommodation_OwnerUpdates_SavesAndReturnsDetail() {
                testAccommodation.setTitle("Old Title");
                edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationUpdateRequestDTO request =
                                new edu.uniquindio.stayhub_v2.dto.accommodation.AccommodationUpdateRequestDTO(
                                        "New Title", null, null, null, null, null, null, null, null, null, null, null, null);

                when(accommodationRepository.findByIdAndDeletedFalse(100L)).thenReturn(Optional.of(testAccommodation));
                when(accommodationRepository.save(testAccommodation)).thenReturn(testAccommodation);
                AccommodationDetailResponseDTO mockDetail = new AccommodationDetailResponseDTO(
                                100L, "ARM-ABC123", "New Title", "Armenia", 4, "COP",
                                BigDecimal.valueOf(100000), null, true, "Desc",
                                null, -75.0, 4.5, "Near park", List.of(), null, null, null);
                when(accommodationMapper.toDetailDTO(testAccommodation)).thenReturn(mockDetail);

                AccommodationDetailResponseDTO result = accommodationService.updateAccommodation(100L, request, "host@example.com");

                assertThat(result.title()).isEqualTo("New Title");
                verify(accommodationRepository).save(testAccommodation);
        }

        @Test
        void listMyAccommodations_HostAuthenticated_ReturnsPage() {
                authenticateAs("host@example.com");
                when(userService.getCurrentUser()).thenReturn(hostUser);
                Page<Accommodation> page = new PageImpl<>(List.of(testAccommodation), PageRequest.of(0, 10), 1);
                when(accommodationRepository.findByHostEmailAndDeletedFalse(eq("host@example.com"), any())).thenReturn(page);
                AccommodationSummaryResponseDTO summaryDTO = new AccommodationSummaryResponseDTO(
                                100L, "ARM-ABC123", "Test", "Armenia", 4, "COP", BigDecimal.valueOf(100000), null, true);
                when(accommodationMapper.toSummaryDTO(testAccommodation)).thenReturn(summaryDTO);

                Page<AccommodationSummaryResponseDTO> result = accommodationService.listMyAccommodations(0, false);

                assertThat(result.getContent()).hasSize(1);
                assertThat(result.getContent().get(0).code()).isEqualTo("ARM-ABC123");
        }
}
