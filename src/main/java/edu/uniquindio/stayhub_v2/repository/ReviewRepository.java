package edu.uniquindio.stayhub_v2.repository;

import edu.uniquindio.stayhub_v2.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByAccommodationIdOrderByCreatedAtDesc(Long accommodationId, Pageable pageable);

    boolean existsByReservationId(Long reservationId);

    @org.springframework.data.jpa.repository.Query("SELECT AVG(r.rating) FROM Review r WHERE r.accommodation.host.id = :hostId")
    Double findAverageRatingByHostId(@org.springframework.data.repository.query.Param("hostId") Long hostId);
}
