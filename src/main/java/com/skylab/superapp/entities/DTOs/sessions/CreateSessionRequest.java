package com.skylab.superapp.entities.DTOs.sessions;

import com.skylab.superapp.entities.SessionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateSessionRequest {

    private UUID eventId;

    private String title;

    private String speakerName;

    private String speakerLinkedin;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int orderIndex;

    private SessionType sessionType;

}
