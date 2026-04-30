package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.Ticket;
import com.skylab.superapp.entities.DTOs.ticket.response.GetTicketResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EventMapper.class, UserMapper.class, TicketCheckInMapper.class})
public interface TicketMapper {

    GetTicketResponseDto ticketToGetTicketResponseDto(Ticket ticket);


}