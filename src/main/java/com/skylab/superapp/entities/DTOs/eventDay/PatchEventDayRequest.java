package com.skylab.superapp.entities.DTOs.eventDay;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class PatchEventDayRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
