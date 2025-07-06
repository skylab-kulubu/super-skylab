package com.skylab.superapp.entities.DTOs.sessions;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.skylab.superapp.entities.SessionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSessionDto {
    private String title;
    private String speakerName;
    private String speakerLinkedin;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date endTime;
    private int orderIndex;
    private int eventId;
    private String sessionType;

}
