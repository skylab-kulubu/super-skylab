package com.skylab.superapp.entities.DTOs.eventDay;

import com.skylab.superapp.core.constants.EventDayMessages;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = EventDayMessages.START_DATE_NOT_NULL)
    private LocalDateTime startDate;

    @NotNull(message = EventDayMessages.END_DATE_NOT_NULL)
    private LocalDateTime endDate;
}
