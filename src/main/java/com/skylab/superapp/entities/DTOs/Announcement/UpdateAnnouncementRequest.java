package com.skylab.superapp.entities.DTOs.Announcement;

import com.skylab.superapp.entities.DTOs.Image.ImageDto;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.DTOs.eventType.EventTypeDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UpdateAnnouncementRequest {

    private String title;

    private String description;

    private LocalDate date;

    private String content;

    private boolean active;

    private LocalDateTime createdAt;

    private UserDto user;

    private EventTypeDto eventType;

    private String formUrl;

    private List<ImageDto> images;


}
