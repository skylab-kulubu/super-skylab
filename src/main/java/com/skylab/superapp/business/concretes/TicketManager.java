package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.TicketService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.constants.TicketMessages;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.mappers.TicketMapper;
import com.skylab.superapp.core.utilities.security.TicketSecurityUtils;
import com.skylab.superapp.dataAccess.TicketDao;
import com.skylab.superapp.entities.DTOs.ticket.response.GetTicketResponseDto;
import com.skylab.superapp.entities.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketManager implements TicketService {

    private final TicketDao ticketDao;
    private final UserService userService;
    private final TicketMapper ticketMapper;

    private final TicketSecurityUtils ticketSecurityUtils;


    @Override
    public GetTicketResponseDto getTicketByUserIdAndEventId(UUID userId, UUID eventId) {
        log.info("Fetching ticket for user ID: {} and event ID: {}", userId, eventId);

        Ticket ticket = ticketDao.findByOwner_IdAndEvent_Id(userId, eventId)
                .orElseThrow(() -> {
                    log.error("Ticket not found for user ID: {} and event ID: {}", userId, eventId);
                    return new ResourceNotFoundException("Bilet bulunamadı!");
                });

        return ticketMapper.ticketToGetTicketResponseDto(ticket);
    }

    @Override
    public GetTicketResponseDto getTicketById(UUID ticketId) {
        log.info("Fetching ticket with ID: {}", ticketId);
        ticketSecurityUtils.checkRead();

        Ticket ticket = ticketDao.findById(ticketId).orElseThrow(() -> {
            log.error("Ticket with ID: {} not found", ticketId);
            return new ResourceNotFoundException(TicketMessages.TICKET_NOT_FOUND_WITH_ID);
        });

        log.info("Ticket fetched with ID: {}", ticketId);

        return ticketMapper.ticketToGetTicketResponseDto(ticket);


    }

    @Override
    public List<GetTicketResponseDto> getTicketsByUserEmail(String email) {
        log.info("Fetching tickets for user email: {}", email);
        ticketSecurityUtils.checkRead();

        List<Ticket> tickets = ticketDao.findAllByOwner_Email(email);

        return tickets.stream()
                .map(ticketMapper::ticketToGetTicketResponseDto)
                .toList();
    }

    @Override
    public List<GetTicketResponseDto> getTicketsByUserId(UUID userId) {
        log.info("Fetching tickets for user ID: {}", userId);
        ticketSecurityUtils.checkRead();

        List<Ticket> tickets = ticketDao.findAllByOwner_Id(userId);

        return tickets.stream()
                .map(ticketMapper::ticketToGetTicketResponseDto)
                .toList();
    }

    @Override
    public Ticket getTicketReference(UUID ticketId) {
      log.info("Fetching ticket reference for ID: {}", ticketId);

        return ticketDao.getReferenceById(ticketId);
    }

    @Override
    public Ticket getTicketEntityById(UUID ticketId) {
        log.info("Fetching ticket entity with ID: {}", ticketId);


        Ticket ticket = ticketDao.findById(ticketId).orElseThrow(() -> {
            log.error("Ticket entity with ID: {} not found", ticketId);
            return new ResourceNotFoundException(TicketMessages.TICKET_NOT_FOUND_WITH_ID);
        });

        log.info("Ticket entity fetched with ID: {}", ticketId);


        return ticket;

    }

    @Override
    public List<GetTicketResponseDto> getMyTickets() {
        log.info("Fetching tickets for the currently authenticated user");
        ticketSecurityUtils.checkReadMe();
        var authenticatedUser = userService.getAuthenticatedUserEntity();

        List<Ticket> tickets = ticketDao.findAllByOwner_Id(authenticatedUser.getId());

        return tickets.stream()
                .map(ticketMapper::ticketToGetTicketResponseDto)
                .toList();
    }

}
