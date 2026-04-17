package edu.uniquindio.stayhub_v2.service;

import edu.uniquindio.stayhub_v2.dto.availability.AvailabilityCalendarDayDTO;
import edu.uniquindio.stayhub_v2.dto.availability.AvailabilityCalendarResponseDTO;
import edu.uniquindio.stayhub_v2.dto.availability.AvailabilityCheckResponseDTO;
import edu.uniquindio.stayhub_v2.dto.availability.AvailabilityRuleDTO;
import edu.uniquindio.stayhub_v2.exception.AccommodationNotFoundException;
import edu.uniquindio.stayhub_v2.exception.UnauthorizedHostException;
import edu.uniquindio.stayhub_v2.model.Accommodation;
import edu.uniquindio.stayhub_v2.model.AvailabilityRule;
import edu.uniquindio.stayhub_v2.model.AvailabilityRuleType;
import edu.uniquindio.stayhub_v2.model.Reservation;
import edu.uniquindio.stayhub_v2.model.ReservationStatus;
import edu.uniquindio.stayhub_v2.model.User;
import edu.uniquindio.stayhub_v2.repository.AccommodationRepository;
import edu.uniquindio.stayhub_v2.repository.AvailabilityRuleRepository;
import edu.uniquindio.stayhub_v2.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AvailabilityService {

    private final AccommodationRepository accommodationRepository;
    private final AvailabilityRuleRepository availabilityRuleRepository;
    private final ReservationRepository reservationRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public AvailabilityCheckResponseDTO checkAvailability(Long accommodationId,
                                                          LocalDateTime startDate,
                                                          LocalDateTime endDate) {
        Accommodation accommodation = findAccommodationOrThrow(accommodationId);

        boolean hasConflict = reservationRepository.existsByAccommodationIdAndDateRangeOverlapAndStatus(
                accommodationId, startDate, endDate, ReservationStatus.ACTIVE);

        if (hasConflict) {
            return new AvailabilityCheckResponseDTO(accommodationId, startDate, endDate, false,
                    "El alojamiento ya tiene una reserva activa para las fechas seleccionadas");
        }

        boolean blockedByRule = isBlockedByRule(accommodationId, startDate.toLocalDate(), endDate.toLocalDate());
        if (blockedByRule) {
            return new AvailabilityCheckResponseDTO(accommodationId, startDate, endDate, false,
                    "El alojamiento está bloqueado para las fechas seleccionadas");
        }

        return new AvailabilityCheckResponseDTO(accommodationId, startDate, endDate, true, null);
    }

    @Transactional(readOnly = true)
    public AvailabilityCalendarResponseDTO getCalendar(Long accommodationId, String month) {
        findAccommodationOrThrow(accommodationId);
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        List<Reservation> reservations = reservationRepository
                .findByAccommodationIdAndStatusAndStartDateBetween(
                        accommodationId, ReservationStatus.ACTIVE,
                        firstDay.atStartOfDay(), lastDay.atTime(23, 59, 59));

        List<AvailabilityRule> rules = availabilityRuleRepository.findByAccommodationId(accommodationId);

        Map<LocalDate, Long> bookedDays = buildBookedDays(reservations);

        List<AvailabilityCalendarDayDTO> days = new ArrayList<>();
        for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
            String status = resolveStatus(date, bookedDays, rules);
            Long reservationId = "BOOKED".equals(status) ? bookedDays.get(date) : null;
            days.add(new AvailabilityCalendarDayDTO(date, status, reservationId));
        }

        return new AvailabilityCalendarResponseDTO(accommodationId, month, days);
    }

    @Transactional(readOnly = true)
    public List<AvailabilityRuleDTO> listRules(Long accommodationId) {
        findAccommodationOrThrow(accommodationId);
        return availabilityRuleRepository.findByAccommodationId(accommodationId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public List<AvailabilityRuleDTO> replaceRules(Long accommodationId, List<AvailabilityRuleDTO> dtos) {
        Accommodation accommodation = findAccommodationOrThrow(accommodationId);
        User currentUser = userService.getCurrentUser();
        if (!accommodation.getHost().getEmail().equals(currentUser.getEmail())) {
            throw new UnauthorizedHostException("No tienes permisos para modificar las reglas de este alojamiento.");
        }

        availabilityRuleRepository.deleteByAccommodationId(accommodationId);

        List<AvailabilityRule> rules = dtos.stream()
                .map(dto -> {
                    AvailabilityRule rule = new AvailabilityRule();
                    rule.setAccommodation(accommodation);
                    rule.setType(dto.type());
                    rule.setStartDate(dto.startDate());
                    rule.setEndDate(dto.endDate());
                    rule.setNote(dto.note());
                    return rule;
                })
                .collect(Collectors.toList());

        List<AvailabilityRule> saved = availabilityRuleRepository.saveAll(rules);
        log.info("Replaced {} availability rules for accommodation {}", saved.size(), accommodationId);
        return saved.stream().map(this::toDTO).toList();
    }

    private boolean isBlockedByRule(Long accommodationId, LocalDate start, LocalDate end) {
        List<AvailabilityRule> rules = availabilityRuleRepository.findByAccommodationId(accommodationId);
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            for (AvailabilityRule rule : rules) {
                if (rule.getType() == AvailabilityRuleType.BLOCKED
                        && !date.isBefore(rule.getStartDate())
                        && !date.isAfter(rule.getEndDate())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Map<LocalDate, Long> buildBookedDays(List<Reservation> reservations) {
        Map<LocalDate, Long> map = new java.util.HashMap<>();
        for (Reservation r : reservations) {
            LocalDate start = r.getStartDate().toLocalDate();
            LocalDate end = r.getEndDate().toLocalDate();
            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                map.putIfAbsent(d, r.getId());
            }
        }
        return map;
    }

    private String resolveStatus(LocalDate date, Map<LocalDate, Long> booked, List<AvailabilityRule> rules) {
        if (booked.containsKey(date)) return "BOOKED";
        for (AvailabilityRule rule : rules) {
            if (!date.isBefore(rule.getStartDate()) && !date.isAfter(rule.getEndDate())) {
                return rule.getType() == AvailabilityRuleType.BLOCKED ? "BLOCKED" : "AVAILABLE";
            }
        }
        return "UNDEFINED";
    }

    private AvailabilityRuleDTO toDTO(AvailabilityRule rule) {
        return new AvailabilityRuleDTO(
                rule.getId(), rule.getType(),
                rule.getStartDate(), rule.getEndDate(),
                rule.getNote(), rule.getCreatedAt());
    }

    private Accommodation findAccommodationOrThrow(Long id) {
        return accommodationRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AccommodationNotFoundException("Accommodation not found: " + id));
    }
}
