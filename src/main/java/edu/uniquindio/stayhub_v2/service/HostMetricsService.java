package edu.uniquindio.stayhub_v2.service;

import edu.uniquindio.stayhub_v2.dto.metrics.HostMetricsResponse;
import edu.uniquindio.stayhub_v2.model.Reservation;
import edu.uniquindio.stayhub_v2.model.ReservationStatus;
import edu.uniquindio.stayhub_v2.model.User;
import edu.uniquindio.stayhub_v2.repository.AccommodationRepository;
import edu.uniquindio.stayhub_v2.repository.ReservationRepository;
import edu.uniquindio.stayhub_v2.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HostMetricsService {

    private final AccommodationRepository accommodationRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public HostMetricsResponse getMetrics(LocalDate from, LocalDate to) {
        User host = userService.getCurrentUser();
        Long hostId = host.getId();

        long accommodationsCount = accommodationRepository.countByHostId(hostId);

        LocalDateTime fromDt = from != null ? from.atStartOfDay() : null;
        LocalDateTime toDt = to != null ? to.atTime(23, 59, 59) : null;

        List<Reservation> reservations = reservationRepository.findByHostIdAndStatusInAndDateRange(
                hostId,
                List.of(ReservationStatus.ACTIVE, ReservationStatus.COMPLETED),
                fromDt,
                toDt);

        double revenueTotal = reservations.stream()
                .map(Reservation::getTotalPrice)
                .filter(p -> p != null)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();

        long totalBookedNights = reservations.stream()
                .mapToLong(r -> ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate()))
                .sum();

        double occupancyRate = 0.0;
        if (accommodationsCount > 0 && from != null && to != null) {
            long periodDays = ChronoUnit.DAYS.between(from, to) + 1;
            long totalAvailableNights = periodDays * accommodationsCount;
            occupancyRate = totalAvailableNights > 0
                    ? Math.min(1.0, (double) totalBookedNights / totalAvailableNights)
                    : 0.0;
        }

        Double averageRating = reviewRepository.findAverageRatingByHostId(hostId);

        String currency = reservations.stream()
                .filter(r -> r.getAccommodation().getCurrency() != null)
                .map(r -> r.getAccommodation().getCurrency().getCurrencyCode())
                .findFirst()
                .orElse("COP");

        return new HostMetricsResponse(
                from, to,
                (int) accommodationsCount,
                reservations.size(),
                occupancyRate,
                revenueTotal,
                currency,
                averageRating);
    }
}
