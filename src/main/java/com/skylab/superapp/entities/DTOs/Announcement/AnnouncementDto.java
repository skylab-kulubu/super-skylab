package com.skylab.superapp.entities.DTOs.Announcement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.DTOs.eventType.EventTypeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementDto {

    private UUID id;

    private String title;

    private String body;

    private Boolean active;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private EventTypeDto eventType;

    private String formUrl;

    private String coverImageUrl;

}
