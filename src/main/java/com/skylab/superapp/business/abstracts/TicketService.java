package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.ticket.response.GetTicketResponseDto;
import com.skylab.superapp.entities.Ticket;

import java.util.List;
import java.util.UUID;

public interface TicketService {

    GetTicketResponseDto getTicketByUserIdAndEventId(UUID userId, UUID eventId);

    GetTicketResponseDto getTicketById(UUID ticketId);

    List<GetTicketResponseDto> getTicketsByUserEmail(String email);

    List<GetTicketResponseDto> getTicketsByUserId(UUID userId);

    Ticket getTicketReference(UUID ticketId);

    Ticket getTicketEntityById(UUID ticketId);

    List<GetTicketResponseDto> getMyTickets();

    List<GetTicketResponseDto> getTicketsByEventId(UUID eventId);

    List<GetTicketResponseDto> searchTicketsByEventId(UUID eventId, String query);

}
