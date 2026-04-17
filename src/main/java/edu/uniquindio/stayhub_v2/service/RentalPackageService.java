package edu.uniquindio.stayhub_v2.service;

import edu.uniquindio.stayhub_v2.dto.rentalpackage.RentalPackageCreateRequest;
import edu.uniquindio.stayhub_v2.dto.rentalpackage.RentalPackageDTO;
import edu.uniquindio.stayhub_v2.dto.rentalpackage.RentalPackageUpdateRequest;
import edu.uniquindio.stayhub_v2.model.Accommodation;
import edu.uniquindio.stayhub_v2.model.RentalPackage;
import edu.uniquindio.stayhub_v2.repository.AccommodationRepository;
import edu.uniquindio.stayhub_v2.repository.RentalPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RentalPackageService {

    private final RentalPackageRepository rentalPackageRepository;
    private final AccommodationRepository accommodationRepository;

    @Transactional(readOnly = true)
    public List<RentalPackageDTO> list(Long accommodationId) {
        if (!accommodationRepository.existsById(accommodationId)) {
            throw new NoSuchElementException("Accommodation not found: " + accommodationId);
        }
        return rentalPackageRepository.findByAccommodationIdOrderByPriceAsc(accommodationId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public RentalPackageDTO create(Long accommodationId, RentalPackageCreateRequest req) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new NoSuchElementException("Accommodation not found: " + accommodationId));
        RentalPackage pkg = RentalPackage.builder()
                .accommodation(accommodation)
                .name(req.name())
                .description(req.description())
                .minNights(req.minNights())
                .maxNights(req.maxNights())
                .price(req.price())
                .currency(Currency.getInstance(req.currency()))
                .active(req.active())
                .build();
        return toDTO(rentalPackageRepository.save(pkg));
    }

    @Transactional
    public RentalPackageDTO update(Long packageId, RentalPackageUpdateRequest req) {
        RentalPackage pkg = rentalPackageRepository.findById(packageId)
                .orElseThrow(() -> new NoSuchElementException("Package not found: " + packageId));
        pkg.setName(req.name());
        pkg.setDescription(req.description());
        pkg.setMinNights(req.minNights());
        pkg.setMaxNights(req.maxNights());
        pkg.setPrice(req.price());
        pkg.setCurrency(Currency.getInstance(req.currency()));
        pkg.setActive(req.active());
        return toDTO(rentalPackageRepository.save(pkg));
    }

    @Transactional
    public void delete(Long packageId) {
        if (!rentalPackageRepository.existsById(packageId)) {
            throw new NoSuchElementException("Package not found: " + packageId);
        }
        rentalPackageRepository.deleteById(packageId);
    }

    private RentalPackageDTO toDTO(RentalPackage pkg) {
        return new RentalPackageDTO(
                pkg.getId(),
                pkg.getAccommodation().getId(),
                pkg.getName(),
                pkg.getDescription(),
                pkg.getMinNights(),
                pkg.getMaxNights(),
                pkg.getPrice(),
                pkg.getCurrency().getCurrencyCode(),
                pkg.isActive());
    }
}
