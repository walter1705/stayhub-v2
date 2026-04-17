package edu.uniquindio.stayhub_v2.repository;

import edu.uniquindio.stayhub_v2.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByAccommodationIdOrderByCreatedAtDesc(Long accommodationId, Pageable pageable);

    boolean existsByReservationId(Long reservationId);
}
