package com.skylab.superapp.entities.DTOs.sessions;

import com.skylab.superapp.core.constants.SessionMessages;
import com.skylab.superapp.entities.SessionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateSessionRequest {

    @NotNull(message = SessionMessages.EVENT_DAY_ID_NOT_NULL)
    private UUID eventDayId;

    @NotBlank(message = SessionMessages.SESSION_TITLE_NOT_BLANK)
    private String title;

    @NotBlank(message = SessionMessages.SPEAKER_NAME_NOT_BLANK)
    private String speakerName;

    private UUID speakerImageId;

    private String speakerLinkedin;

    private String description;

    @NotNull(message = SessionMessages.SESSION_START_TIME_NOT_NULL)
    private LocalDateTime startTime;

    @NotNull(message = SessionMessages.SESSION_END_TIME_NOT_NULL)
    private LocalDateTime endTime;

    private int orderIndex;

    @NotNull(message = SessionMessages.SESSION_TYPE_NOT_NULL)
    private SessionType sessionType;

}
