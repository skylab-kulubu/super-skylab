package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.Announcement;
import com.skylab.superapp.entities.DTOs.Announcement.AnnouncementDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnnouncementMapper {

    private final UserMapper userMapper;
    private final ImageMapper imageMapper;
    private final EventTypeMapper eventTypeMapper;

    public AnnouncementMapper(@Lazy UserMapper userMapper, @Lazy ImageMapper imageMapper,
                              @Lazy EventTypeMapper eventTypeMapper) {
        this.userMapper = userMapper;
        this.imageMapper = imageMapper;
        this.eventTypeMapper = eventTypeMapper;
    }

    public AnnouncementDto toDto(Announcement announcement, boolean includeEventType) {
        if (announcement == null) {
            return null;
        }
        return new AnnouncementDto(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getBody(),
                announcement.getActive(),
                includeEventType ? eventTypeMapper.toDto(announcement.getEventType()) : null,
                announcement.getFormUrl(),
                imageMapper.toString(announcement.getCoverImage())
        );
    }

    public AnnouncementDto toDto(Announcement announcement) {
        return toDto(announcement, false);
    }

    public List<AnnouncementDto> toDtoList(List<Announcement> announcements,
                                            boolean includeUser, boolean includeEventType, boolean includeImages) {
        return announcements.stream()
                .map(announcement -> toDto(announcement, includeEventType))
                .toList();
    }

    public List<AnnouncementDto> toDtoList(List<Announcement> announcements) {
        return toDtoList(announcements, false, false, false);
    }

}