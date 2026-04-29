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
        log.debug("Retrieving ticket. UserId: {}, EventId: {}", userId, eventId);

        Ticket ticket = ticketDao.findByOwner_IdAndEvent_Id(userId, eventId)
                .orElseThrow(() -> {
                    log.error("Ticket retrieval failed: Resource not found. UserId: {}, EventId: {}", userId, eventId);
                    return new ResourceNotFoundException("Bilet bulunamadı!");
                });

        return ticketMapper.ticketToGetTicketResponseDto(ticket);
    }

    @Override
    public GetTicketResponseDto getTicketById(UUID ticketId) {
        log.debug("Retrieving ticket. TicketId: {}", ticketId);
        ticketSecurityUtils.checkRead();

        Ticket ticket = ticketDao.findById(ticketId).orElseThrow(() -> {
            log.error("Ticket retrieval failed: Resource not found. TicketId: {}", ticketId);
            return new ResourceNotFoundException(TicketMessages.TICKET_NOT_FOUND_WITH_ID);
        });

        return ticketMapper.ticketToGetTicketResponseDto(ticket);
    }

    @Override
    public List<GetTicketResponseDto> getTicketsByUserEmail(String email) {
        log.debug("Retrieving tickets. UserEmail: {}", email);
        ticketSecurityUtils.checkRead();

        List<Ticket> tickets = ticketDao.findAllByOwner_Email(email);

        return tickets.stream()
                .map(ticketMapper::ticketToGetTicketResponseDto)
                .toList();
    }

    @Override
    public List<GetTicketResponseDto> getTicketsByUserId(UUID userId) {
        log.debug("Retrieving tickets. UserId: {}", userId);
        ticketSecurityUtils.checkRead();

        List<Ticket> tickets = ticketDao.findAllByOwner_Id(userId);

        return tickets.stream()
                .map(ticketMapper::ticketToGetTicketResponseDto)
                .toList();
    }

    @Override
    public Ticket getTicketReference(UUID ticketId) {
        log.debug("Retrieving ticket reference. TicketId: {}", ticketId);
        return ticketDao.getReferenceById(ticketId);
    }

    @Override
    public Ticket getTicketEntityById(UUID ticketId) {
        log.debug("Retrieving ticket entity. TicketId: {}", ticketId);

        return ticketDao.findById(ticketId).orElseThrow(() -> {
            log.error("Ticket entity retrieval failed: Resource not found. TicketId: {}", ticketId);
            return new ResourceNotFoundException(TicketMessages.TICKET_NOT_FOUND_WITH_ID);
        });
    }

    @Override
    public List<GetTicketResponseDto> getMyTickets() {
        log.debug("Retrieving tickets for authenticated user.");
        ticketSecurityUtils.checkReadMe();

        var authenticatedUser = userService.getAuthenticatedUserEntity();
        List<Ticket> tickets = ticketDao.findAllByOwner_Id(authenticatedUser.getId());

        log.info("Retrieved tickets for authenticated user successfully. UserId: {}, TotalCount: {}", authenticatedUser.getId(), tickets.size());

        return tickets.stream()
                .map(ticketMapper::ticketToGetTicketResponseDto)
                .toList();
    }
}