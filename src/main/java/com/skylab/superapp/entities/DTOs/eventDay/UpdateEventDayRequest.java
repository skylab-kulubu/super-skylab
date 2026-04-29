package com.skylab.superapp.entities.DTOs.eventDay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventDayRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
