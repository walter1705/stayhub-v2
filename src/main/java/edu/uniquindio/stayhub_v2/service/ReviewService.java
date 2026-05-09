package edu.uniquindio.stayhub_v2.service;

import edu.uniquindio.stayhub_v2.dto.review.ReviewCreateRequest;
import edu.uniquindio.stayhub_v2.dto.review.ReviewDTO;
import edu.uniquindio.stayhub_v2.dto.review.ReviewHostResponseRequest;
import edu.uniquindio.stayhub_v2.dto.review.UserPublicDTO;
import edu.uniquindio.stayhub_v2.model.NotificationType;
import edu.uniquindio.stayhub_v2.model.Reservation;
import edu.uniquindio.stayhub_v2.model.ReservationStatus;
import edu.uniquindio.stayhub_v2.model.Review;
import edu.uniquindio.stayhub_v2.model.User;
import edu.uniquindio.stayhub_v2.repository.AccommodationRepository;
import edu.uniquindio.stayhub_v2.repository.ReservationRepository;
import edu.uniquindio.stayhub_v2.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional
    public ReviewDTO create(ReviewCreateRequest req) {
        Reservation reservation = reservationRepository.findById(req.reservationId())
                .orElseThrow(() -> new NoSuchElementException("Reservation not found: " + req.reservationId()));

        if (reservation.getStatus() != ReservationStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation is not completed.");
        }

        if (reviewRepository.existsByReservationId(req.reservationId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation already has a review.");
        }

        User currentUser = userService.getCurrentUser();
        if (!reservation.getGuest().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the guest of this reservation.");
        }

        Review review = Review.builder()
                .accommodation(reservation.getAccommodation())
                .reservation(reservation)
                .author(currentUser)
                .rating(req.rating())
                .comment(req.comment())
                .build();

        Review saved = reviewRepository.save(review);
        notificationService.createNotification(
                reservation.getAccommodation().getHost().getId(),
                NotificationType.REVIEW_CREATED,
                "Nuevo comentario recibido",
                String.format(
                        "%s dejó una valoración de %s estrellas para %s.",
                        currentUser.getFullName(),
                        req.rating(),
                        reservation.getAccommodation().getTitle()));

        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReviewDTO> list(Long accommodationId, int page, int size) {
        if (!accommodationRepository.existsById(accommodationId)) {
            throw new NoSuchElementException("Accommodation not found: " + accommodationId);
        }
        return reviewRepository
                .findByAccommodationIdOrderByCreatedAtDesc(accommodationId, PageRequest.of(page, size))
                .map(this::toDTO);
    }

    @Transactional
    public ReviewDTO respond(Long reviewId, ReviewHostResponseRequest req) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("Review not found: " + reviewId));

        User currentUser = userService.getCurrentUser();
        if (!review.getAccommodation().getHost().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this accommodation.");
        }

        review.setHostResponse(req.response());
        review.setHostResponseAt(LocalDateTime.now());
        return toDTO(reviewRepository.save(review));
    }

    private ReviewDTO toDTO(Review review) {
        User author = review.getAuthor();
        UserPublicDTO authorDTO = new UserPublicDTO(
                author.getId(),
                author.getFullName(),
                author.getEmail(),
                author.getProfilePicture());
        return new ReviewDTO(
                review.getId(),
                review.getAccommodation().getId(),
                review.getReservation().getId(),
                authorDTO,
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getHostResponse(),
                review.getHostResponseAt());
    }
}
