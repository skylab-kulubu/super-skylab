package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.Announcement;
import com.skylab.superapp.entities.DTOs.Announcement.GetAnnouncementDto;
import com.skylab.superapp.entities.DTOs.User.GetUserDto;
import com.skylab.superapp.entities.DTOs.Image.GetImageDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnnouncementMapper {

    private final UserMapper userMapper;
    private final ImageMapper imageMapper;

    public AnnouncementMapper(UserMapper userMapper, ImageMapper imageMapper) {
        this.userMapper = userMapper;
        this.imageMapper = imageMapper;
    }

    public GetAnnouncementDto toDto(Announcement announcement) {
        return GetAnnouncementDto.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .description(announcement.getDescription())
                .date(announcement.getDate())
                .createdAt(announcement.getCreatedAt())
                .formUrl(announcement.getFormUrl())
                .isActive(announcement.isActive())
                .type(announcement.getEventType().getName())
                .author(userMapper.toDto(announcement.getUser()))
                .images(imageMapper.toDtoList(announcement.getImages()))
                .build();
    }

    public List<GetAnnouncementDto> toDtoList(List<Announcement> announcements) {
        return announcements.stream()
                .map(this::toDto)
                .toList();
    }
}