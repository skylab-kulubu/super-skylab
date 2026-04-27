package com.skylab.superapp.entities.DTOs.season;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SeasonDto {

    private UUID id;

    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean active;

}
