package com.skylab.superapp.entities.DTOs.Announcement;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateAnnouncementRequest {

    private String title;

    private String description;

    private LocalDateTime date;

    private String content;

    private boolean active;

    private LocalDateTime createdAt;

    private UUID eventTypeId;

    private String formUrl;

}
