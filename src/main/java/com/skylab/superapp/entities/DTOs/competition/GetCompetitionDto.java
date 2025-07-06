package com.skylab.superapp.entities.DTOs.competition;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skylab.superapp.entities.DTOs.Event.GetEventDetailsDto;
import com.skylab.superapp.entities.DTOs.eventType.GetEventTypeDto;
import com.skylab.superapp.entities.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetCompetitionDto {

    private int id;

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date endDate;

    private boolean active;

    private List<GetEventDetailsDto> events;

    private GetEventTypeDto eventType;
}