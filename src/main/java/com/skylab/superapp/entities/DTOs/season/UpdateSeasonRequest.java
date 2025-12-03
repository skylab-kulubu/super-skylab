package com.skylab.superapp.entities.DTOs.season;

import com.skylab.superapp.core.constants.SeasonMessages;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateSeasonRequest {


    @NotNull(message = SeasonMessages.SEASON_NAME_CANNOT_BE_NULL_OR_BLANK)
    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean active;

}
