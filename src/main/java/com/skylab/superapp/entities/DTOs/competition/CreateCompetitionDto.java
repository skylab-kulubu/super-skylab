package com.skylab.superapp.entities.DTOs.competition;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skylab.superapp.entities.DTOs.eventType.GetEventTypeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCompetitionDto {

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private Date endDate;

    private boolean active;

    private GetEventTypeDto eventType;



}
