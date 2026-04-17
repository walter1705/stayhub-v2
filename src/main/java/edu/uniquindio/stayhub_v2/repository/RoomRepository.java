package edu.uniquindio.stayhub_v2.repository;

import edu.uniquindio.stayhub_v2.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByAccommodationIdAndDeletedFalse(Long accommodationId);

    Optional<Room> findByIdAndDeletedFalse(Long id);

    long countByAccommodationIdAndDeletedFalse(Long accommodationId);
}
