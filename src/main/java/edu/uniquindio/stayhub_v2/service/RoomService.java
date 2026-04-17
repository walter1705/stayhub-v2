package edu.uniquindio.stayhub_v2.service;

import edu.uniquindio.stayhub_v2.dto.room.RoomCreateRequestDTO;
import edu.uniquindio.stayhub_v2.dto.room.RoomResponseDTO;
import edu.uniquindio.stayhub_v2.dto.room.RoomUpdateRequestDTO;
import edu.uniquindio.stayhub_v2.exception.AccommodationNotFoundException;
import edu.uniquindio.stayhub_v2.exception.UnauthorizedHostException;
import edu.uniquindio.stayhub_v2.mapper.RoomMapper;
import edu.uniquindio.stayhub_v2.model.Accommodation;
import edu.uniquindio.stayhub_v2.model.Room;
import edu.uniquindio.stayhub_v2.model.User;
import edu.uniquindio.stayhub_v2.repository.AccommodationRepository;
import edu.uniquindio.stayhub_v2.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final AccommodationRepository accommodationRepository;
    private final RoomMapper roomMapper;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<RoomResponseDTO> listRooms(Long accommodationId) {
        accommodationRepository.findByIdAndDeletedFalse(accommodationId)
                .orElseThrow(() -> new AccommodationNotFoundException("Accommodation not found: " + accommodationId));
        return roomRepository.findByAccommodationIdAndDeletedFalse(accommodationId)
                .stream().map(roomMapper::toResponseDTO).toList();
    }

    @Transactional
    public RoomResponseDTO createRoom(Long accommodationId, RoomCreateRequestDTO requestDTO) {
        Accommodation accommodation = accommodationRepository.findByIdAndDeletedFalse(accommodationId)
                .orElseThrow(() -> new AccommodationNotFoundException("Accommodation not found: " + accommodationId));

        User currentUser = userService.getCurrentUser();
        if (!accommodation.getHost().getEmail().equals(currentUser.getEmail())) {
            throw new UnauthorizedHostException("No tienes permisos para agregar habitaciones a esta casa.");
        }

        Room room = roomMapper.toEntity(requestDTO);
        room.setAccommodation(accommodation);
        room.setCode(generateRoomCode(accommodation.getCode()));

        Room saved = roomRepository.save(room);
        log.info("Room created with code: {} for accommodation: {}", saved.getCode(), accommodationId);
        return roomMapper.toResponseDTO(saved);
    }

    @Transactional
    public RoomResponseDTO updateRoom(Long roomId, RoomUpdateRequestDTO requestDTO) {
        Room room = findActiveRoomOrThrow(roomId);
        User currentUser = userService.getCurrentUser();
        validateRoomOwnership(room, currentUser);

        roomMapper.updateFromDto(requestDTO, room);
        Room saved = roomRepository.save(room);
        log.info("Room {} updated", roomId);
        return roomMapper.toResponseDTO(saved);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        Room room = findActiveRoomOrThrow(roomId);
        User currentUser = userService.getCurrentUser();
        validateRoomOwnership(room, currentUser);

        room.setDeleted(true);
        roomRepository.save(room);
        log.info("Room {} soft-deleted", roomId);
    }

    private Room findActiveRoomOrThrow(Long roomId) {
        return roomRepository.findByIdAndDeletedFalse(roomId)
                .orElseThrow(() -> new NoSuchElementException("Room not found: " + roomId));
    }

    private void validateRoomOwnership(Room room, User user) {
        if (!room.getAccommodation().getHost().getEmail().equals(user.getEmail())) {
            throw new UnauthorizedHostException("No tienes permisos para modificar esta habitación.");
        }
    }

    private String generateRoomCode(String accommodationCode) {
        long count = roomRepository.countByAccommodationIdAndDeletedFalse(
                null) + 1; // approximate; actual count query below
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase();
        return accommodationCode + "-R" + suffix;
    }
}
