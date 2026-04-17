package edu.uniquindio.stayhub_v2.repository;

import edu.uniquindio.stayhub_v2.model.AvailabilityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityRuleRepository extends JpaRepository<AvailabilityRule, Long> {

    List<AvailabilityRule> findByAccommodationId(Long accommodationId);

    void deleteByAccommodationId(Long accommodationId);

    @Query("""
            SELECT r FROM AvailabilityRule r
            WHERE r.accommodation.id = :accommodationId
            AND r.startDate <= :date
            AND r.endDate >= :date
            """)
    List<AvailabilityRule> findRulesForDate(
            @Param("accommodationId") Long accommodationId,
            @Param("date") LocalDate date
    );
}
