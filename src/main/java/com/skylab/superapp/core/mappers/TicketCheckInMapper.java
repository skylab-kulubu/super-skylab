package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.TicketCheckIn;
import com.skylab.superapp.entities.DTOs.ticketCheckIn.TicketCheckInDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketCheckInMapper {

    @Mapping(target = "eventDayId", source = "eventDay.id")
    TicketCheckInDto toDto(TicketCheckIn ticketCheckIn);
}