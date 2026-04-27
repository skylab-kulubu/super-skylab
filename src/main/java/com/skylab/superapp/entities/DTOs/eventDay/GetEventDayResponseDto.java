package com.skylab.superapp.entities.DTOs.eventDay;

import com.skylab.superapp.entities.DTOs.Event.EventDto;
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
public class GetEventDayResponseDto {

    private UUID id;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private EventDto event;

}
