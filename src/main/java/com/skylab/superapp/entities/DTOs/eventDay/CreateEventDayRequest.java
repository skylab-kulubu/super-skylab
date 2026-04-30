package com.skylab.superapp.entities.DTOs.eventDay;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventDayRequest {

    private UUID eventId;

    private String name;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
