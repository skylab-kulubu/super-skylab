package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.Announcement;
import com.skylab.superapp.entities.DTOs.Announcement.AnnouncementDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ImageMapper.class, EventTypeMapper.class})
public interface AnnouncementMapper {

    @Mapping(target = "coverImageUrl", source = "coverImage")
    @Mapping(target = "eventType", ignore = true)
    AnnouncementDto toDto(Announcement announcement);

    @Mapping(target = "coverImageUrl", source = "coverImage")
    @Mapping(target = "eventType", source = "eventType")
    AnnouncementDto toDtoWithDetails(Announcement announcement);
}