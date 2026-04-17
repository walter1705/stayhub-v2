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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private RoomMapper roomMapper;
    @Mock
    private UserService userService;

    @InjectMocks
    private RoomService roomService;

    private User hostUser;
    private Accommodation accommodation;
    private Room room;

    @BeforeEach
    void setUp() {
        hostUser = User.builder().id(1L).email("host@example.com").build();
        accommodation = Accommodation.builder().id(10L).host(hostUser).code("ARM-ABC123").build();
        room = Room.builder().id(1L).accommodation(accommodation).code("ARM-ABC123-R01").name("Suite").capacity(2).build();
    }

    @Test
    void listRooms_ValidAccommodation_ReturnsRooms() {
        when(accommodationRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(accommodation));
        when(roomRepository.findByAccommodationIdAndDeletedFalse(10L)).thenReturn(List.of(room));
        RoomResponseDTO dto = new RoomResponseDTO(1L, 10L, "ARM-ABC123-R01", "Suite", 2, List.of());
        when(roomMapper.toResponseDTO(room)).thenReturn(dto);

        List<RoomResponseDTO> result = roomService.listRooms(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Suite");
    }

    @Test
    void createRoom_OwnerCreates_SavesAndReturnsDTO() {
        when(accommodationRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(accommodation));
        when(userService.getCurrentUser()).thenReturn(hostUser);
        when(roomMapper.toEntity(any())).thenReturn(room);
        when(roomRepository.save(room)).thenReturn(room);
        RoomResponseDTO dto = new RoomResponseDTO(1L, 10L, "ARM-ABC123-R01", "Suite", 2, List.of());
        when(roomMapper.toResponseDTO(room)).thenReturn(dto);

        RoomCreateRequestDTO request = new RoomCreateRequestDTO("Suite", 2, null);
        RoomResponseDTO result = roomService.createRoom(10L, request);

        assertThat(result.name()).isEqualTo("Suite");
        verify(roomRepository).save(room);
    }

    @Test
    void createRoom_NonOwner_ThrowsUnauthorized() {
        User other = User.builder().id(2L).email("other@example.com").build();
        when(accommodationRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(accommodation));
        when(userService.getCurrentUser()).thenReturn(other);

        assertThatThrownBy(() -> roomService.createRoom(10L, new RoomCreateRequestDTO("Suite", 2, null)))
                .isInstanceOf(UnauthorizedHostException.class);
    }

    @Test
    void updateRoom_OwnerUpdates_SavesAndReturnsDTO() {
        when(roomRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(room));
        when(userService.getCurrentUser()).thenReturn(hostUser);
        when(roomRepository.save(room)).thenReturn(room);
        RoomResponseDTO dto = new RoomResponseDTO(1L, 10L, "ARM-ABC123-R01", "Updated", 3, List.of());
        when(roomMapper.toResponseDTO(room)).thenReturn(dto);

        RoomUpdateRequestDTO request = new RoomUpdateRequestDTO("Updated", 3, null);
        RoomResponseDTO result = roomService.updateRoom(1L, request);

        assertThat(result.name()).isEqualTo("Updated");
        verify(roomRepository).save(room);
    }

    @Test
    void deleteRoom_OwnerDeletes_SoftDeletesRoom() {
        when(roomRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(room));
        when(userService.getCurrentUser()).thenReturn(hostUser);

        roomService.deleteRoom(1L);

        assertThat(room.isDeleted()).isTrue();
        verify(roomRepository).save(room);
    }

    @Test
    void listRooms_AccommodationNotFound_ThrowsException() {
        when(accommodationRepository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.listRooms(999L))
                .isInstanceOf(AccommodationNotFoundException.class);
    }
}
