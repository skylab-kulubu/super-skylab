package com.skylab.superapp.entities.DTOs.Announcement;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateAnnouncementRequest {

    private String title;

    private String body;

    private Boolean active;

    private UUID eventTypeId;

    private String formUrl;


}
