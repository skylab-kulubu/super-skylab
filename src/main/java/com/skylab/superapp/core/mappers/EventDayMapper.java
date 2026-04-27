package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.eventDay.GetEventDayResponseDto;
import com.skylab.superapp.entities.EventDay;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EventDayMapper {

    GetEventDayResponseDto eventDayToGetEventDayResponseDto(EventDay eventDay);

}
