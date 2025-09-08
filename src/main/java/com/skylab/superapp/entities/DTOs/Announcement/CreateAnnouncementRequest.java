package com.skylab.superapp.entities.DTOs.Announcement;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateAnnouncementRequest {

    private String title;

    private String body;

    private LocalDateTime date;

    private boolean active;

    private LocalDateTime createdAt;

    private UUID eventTypeId;

    private String formUrl;

}
