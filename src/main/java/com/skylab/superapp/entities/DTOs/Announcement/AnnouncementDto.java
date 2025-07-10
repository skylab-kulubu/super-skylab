package com.skylab.superapp.entities.DTOs.Announcement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skylab.superapp.entities.DTOs.Image.ImageDto;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.DTOs.eventType.EventTypeDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AnnouncementDto {

    private UUID id;

    private String title;

    private LocalDateTime date;

    private String body;

    private boolean active;

    private LocalDateTime createdAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDto user;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private EventTypeDto eventType;

    private String formUrl;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ImageDto> images;

    public AnnouncementDto(UUID id, String title, LocalDateTime date, String body, boolean active,
                           LocalDateTime createdAt, UserDto user, EventTypeDto eventType, String formUrl,
                           List<ImageDto> images) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.body = body;
        this.active = active;
        this.createdAt = createdAt;
        this.user = user;
        this.eventType = eventType;
        this.formUrl = formUrl;
        this.images = images;
    }
}
