package com.skylab.superapp.entities.DTOs.sessions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skylab.superapp.entities.DTOs.Image.GetImageDto;
import com.skylab.superapp.entities.SessionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetSessionDto {

    private int id;
    private String title;
    private String speakerName;
    private String speakerLinkedin;
    private GetImageDto speakerImage;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private Date startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private Date endTime;

    private int orderIndex;
    private int eventId;
    private SessionType sessionType;


}
