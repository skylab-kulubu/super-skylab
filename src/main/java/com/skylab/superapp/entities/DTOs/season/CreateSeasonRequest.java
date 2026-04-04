package com.skylab.superapp.entities.DTOs.season;

import com.skylab.superapp.core.constants.SeasonMessages;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateSeasonRequest {

    @NotNull(message = SeasonMessages.SEASON_NAME_CANNOT_BE_NULL_OR_BLANK)
    private String name;

    @NotNull(message = SeasonMessages.SEASON_START_DATE_CANNOT_BE_NULL)
    private LocalDateTime startDate;

    @NotNull(message = SeasonMessages.SEASON_END_DATE_CANNOT_BE_NULL)
    private LocalDateTime endDate;

    private boolean active;
}
