package com.skylab.superapp.entities.DTOs.sessions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
import com.skylab.superapp.entities.DTOs.Image.ImageDto;
import com.skylab.superapp.entities.SessionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class SessionDto {

    private UUID id;

    private String title;

    private String speakerName;

    private String speakerLinkedin;

    private ImageDto speakerImage;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int orderIndex;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private EventDto event;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SessionType sessionType;

    public SessionDto(UUID id, String title, String speakerName, String speakerLinkedin, ImageDto speakerImage,
                      String description, LocalDateTime startTime, LocalDateTime endTime, int orderIndex,
                      EventDto event, SessionType sessionType) {
        this.id = id;
        this.title = title;
        this.speakerName = speakerName;
        this.speakerLinkedin = speakerLinkedin;
        this.speakerImage = speakerImage;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.orderIndex = orderIndex;
        this.event = event;
        this.sessionType = sessionType;
    }
}
