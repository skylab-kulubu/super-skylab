package com.skylab.superapp.entities.DTOs.Announcement;

import com.skylab.superapp.core.constants.AnnouncementMessages;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateAnnouncementRequestDto {

    @NotNull(message = AnnouncementMessages.TITLE_NOT_NULL)
    private String title;

    @NotNull(message = AnnouncementMessages.BODY_NOT_NULL)
    private String body;

    private boolean active;

    @NotNull(message = AnnouncementMessages.EVENT_TYPE_ID_NOT_NULL)
    private UUID eventTypeId;

    private String formUrl;

}
