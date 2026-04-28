package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.EventDayService;
import com.skylab.superapp.business.abstracts.TicketCheckInService;
import com.skylab.superapp.business.abstracts.TicketService;
import com.skylab.superapp.core.constants.TicketCheckInMessages;
import com.skylab.superapp.core.utilities.security.TicketSecurityUtils;
import com.skylab.superapp.dataAccess.TicketCheckInDao;
import com.skylab.superapp.entities.EventDay;
import com.skylab.superapp.entities.Ticket;
import com.skylab.superapp.entities.TicketCheckIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketCheckInManager implements TicketCheckInService {

    private final TicketCheckInDao ticketCheckInDao;
    private final TicketService ticketService;
    private final EventDayService eventDayService;

    private final TicketSecurityUtils ticketSecurityUtils;


    @Override
    public void checkInToEvent(UUID ticketId, UUID eventDayId) {
        log.info("Checking in ticket with ID: {} for event day ID: {}", ticketId, eventDayId);

        Ticket ticket = ticketService.getTicketEntityById(ticketId);
        EventDay eventDay = eventDayService.getEventDayEntityById(eventDayId);

        String eventTypeName = extractEventTypeName(ticket);
        ticketSecurityUtils.checkValidate(eventTypeName);


        if (!ticket.getEvent().getId().equals(eventDay.getEvent().getId())) {
            log.error("Ticket with ID: {} does not belong to the event day with ID: {}", ticketId, eventDayId);
            throw new IllegalArgumentException(TicketCheckInMessages.TICKET_EVENT_MISMATCH);
        }

        if (ticketCheckInDao.existsByTicket_IdAndEventDay_Id(ticketId, eventDayId)) {
            log.error("Ticket with ID: {} has already been checked in for event day ID: {}", ticketId, eventDayId);
            throw new IllegalStateException(TicketCheckInMessages.TICKET_ALREADY_CHECKED_IN);
        }



        TicketCheckIn ticketCheckIn =  TicketCheckIn.builder()
                .ticket(ticket)
                .eventDay(eventDay)
                .build();


         ticketCheckInDao.save(ticketCheckIn);

         log.info("Successfully checked in ticket with ID: {} for event day ID: {}", ticketId, eventDayId);

    }

    private String extractEventTypeName(Ticket ticket) {
        if (ticket != null && ticket.getEvent() != null && ticket.getEvent().getType() != null) {
            return ticket.getEvent().getType().getName();
        }
        return null;
    }
}
