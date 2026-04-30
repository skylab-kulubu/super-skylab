package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.Ticket;
import com.skylab.superapp.entities.DTOs.ticket.response.GetTicketResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {EventMapper.class, UserMapper.class, TicketCheckInMapper.class})
public interface TicketMapper {

    @Mapping(target = "ticketType", source = "ticketType")
    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "guestFirstName", source = "guestFirstName")
    @Mapping(target = "guestLastName", source = "guestLastName")
    @Mapping(target = "guestEmail", source = "guestEmail")
    @Mapping(target = "guestPhoneNumber", source = "guestPhoneNumber")
    @Mapping(target = "guestUniversity", source = "guestUniversity")
    @Mapping(target = "guestFaculty", source = "guestFaculty")
    @Mapping(target = "guestDepartment", source = "guestDepartment")
    @Mapping(target = "guestGrade", source = "guestGrade")
    GetTicketResponseDto ticketToGetTicketResponseDto(Ticket ticket);

}