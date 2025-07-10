package com.skylab.superapp.entities.DTOs.Announcement;

import com.skylab.superapp.entities.DTOs.Image.ImageDto;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.DTOs.eventType.EventTypeDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateAnnouncementRequest {

    private String title;

    private String body;

    private LocalDate date;

    private boolean active;

    private LocalDateTime createdAt;

    private UUID eventTypeId;

    private String formUrl;

    private List<ImageDto> images;


}
