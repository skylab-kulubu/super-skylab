package com.skylab.superapp.entities.DTOs.ticket.response;

import com.skylab.superapp.entities.DTOs.Event.EventDto;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.DTOs.ticketCheckIn.TicketCheckInDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetTicketResponseDto {

    private UUID id;
    private boolean sent;
    private UserDto owner;
    private EventDto event;

    private List<TicketCheckInDto> checkIns;

}