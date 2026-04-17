package edu.uniquindio.stayhub_v2.mapper;

import edu.uniquindio.stayhub_v2.dto.room.RoomCreateRequestDTO;
import edu.uniquindio.stayhub_v2.dto.room.RoomResponseDTO;
import edu.uniquindio.stayhub_v2.dto.room.RoomUpdateRequestDTO;
import edu.uniquindio.stayhub_v2.model.Room;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "accommodationId", source = "accommodation.id")
    RoomResponseDTO toResponseDTO(Room room);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Room toEntity(RoomCreateRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromDto(RoomUpdateRequestDTO dto, @MappingTarget Room room);
}
