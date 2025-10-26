package com.skylab.superapp.entities.DTOs.sessions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
import com.skylab.superapp.entities.SessionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionDto {

    private UUID id;

    private String title;

    private String speakerName;

    private String speakerLinkedin;

    private String speakerImageUrl;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int orderIndex;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private EventDto event;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SessionType sessionType;

}
