package edu.uniquindio.stayhub_v2.repository;

import edu.uniquindio.stayhub_v2.model.RentalPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalPackageRepository extends JpaRepository<RentalPackage, Long> {

    List<RentalPackage> findByAccommodationIdOrderByPriceAsc(Long accommodationId);
}
