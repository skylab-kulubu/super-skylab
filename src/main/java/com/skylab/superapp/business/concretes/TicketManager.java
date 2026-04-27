package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.business.abstracts.TicketService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.constants.TicketMessages;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.mappers.TicketMapper;
import com.skylab.superapp.dataAccess.TicketDao;
import com.skylab.superapp.entities.DTOs.ticket.response.GetTicketResponseDto;
import com.skylab.superapp.entities.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TicketManager implements TicketService {

    private final TicketDao ticketDao;

    private final Logger logger = LoggerFactory.getLogger(TicketManager.class);
    private final EventService eventService;
    private final UserService userService;
    private final TicketMapper ticketMapper;


    public TicketManager(TicketDao ticketDao, EventService eventService, UserService userService, TicketMapper ticketMapper) {
        this.ticketDao = ticketDao;
        this.eventService = eventService;
        this.userService = userService;
        this.ticketMapper = ticketMapper;
    }

    @Override
    public GetTicketResponseDto getTicketByUserIdAndEventId(UUID userId, UUID eventId) {
        logger.info("Fetching ticket for user ID: {} and event ID: {}", userId, eventId);

        Ticket ticket = ticketDao.findByOwner_IdAndEvent_Id(userId, eventId)
                .orElseThrow(() -> {
                    logger.error("Ticket not found for user ID: {} and event ID: {}", userId, eventId);
                    return new ResourceNotFoundException("Bilet bulunamadı!");
                });

        return ticketMapper.ticketToGetTicketResponseDto(ticket);
    }

    @Override
    public GetTicketResponseDto getTicketById(UUID ticketId) {
        logger.info("Fetching ticket with ID: {}", ticketId);

        Ticket ticket = ticketDao.findById(ticketId).orElseThrow(() -> {
            logger.error("Ticket with ID: {} not found", ticketId);
            return new ResourceNotFoundException(TicketMessages.TICKET_NOT_FOUND_WITH_ID);
        });

        logger.info("Ticket fetched with ID: {}", ticketId);

        return ticketMapper.ticketToGetTicketResponseDto(ticket);


    }

    @Override
    public List<GetTicketResponseDto> getTicketsByUserEmail(String email) {
        logger.info("Fetching tickets for user email: {}", email);

        List<Ticket> tickets = ticketDao.findAllByOwner_Email(email);

        return tickets.stream()
                .map(ticketMapper::ticketToGetTicketResponseDto)
                .toList();
    }

    @Override
    public List<GetTicketResponseDto> getTicketsByUserId(UUID userId) {
        logger.info("Fetching tickets for user ID: {}", userId);

        List<Ticket> tickets = ticketDao.findAllByOwner_Id(userId);

        return tickets.stream()
                .map(ticketMapper::ticketToGetTicketResponseDto)
                .toList();
    }

    @Override
    public Ticket getTicketReference(UUID ticketId) {
      logger.info("Fetching ticket reference for ID: {}", ticketId);

        return ticketDao.getReferenceById(ticketId);
    }

    @Override
    public Ticket getTicketEntityById(UUID ticketId) {
        logger.info("Fetching ticket entity with ID: {}", ticketId);


        Ticket ticket = ticketDao.findById(ticketId).orElseThrow(() -> {
            logger.error("Ticket entity with ID: {} not found", ticketId);
            return new ResourceNotFoundException(TicketMessages.TICKET_NOT_FOUND_WITH_ID);
        });

        logger.info("Ticket entity fetched with ID: {}", ticketId);


        return ticket;

    }

    @Override
    public List<GetTicketResponseDto> getMyTickets() {
        logger.info("Fetching tickets for the currently authenticated user");
        var authenticatedUser = userService.getAuthenticatedUserEntity();

        List<Ticket> tickets = ticketDao.findAllByOwner_Id(authenticatedUser.getId());

        return tickets.stream()
                .map(ticketMapper::ticketToGetTicketResponseDto)
                .toList();
    }

}
