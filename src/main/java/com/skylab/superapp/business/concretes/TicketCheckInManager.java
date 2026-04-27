package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.EventDayService;
import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.business.abstracts.TicketCheckInService;
import com.skylab.superapp.business.abstracts.TicketService;
import com.skylab.superapp.core.constants.TicketCheckInMessages;
import com.skylab.superapp.dataAccess.TicketCheckInDao;
import com.skylab.superapp.entities.EventDay;
import com.skylab.superapp.entities.Ticket;
import com.skylab.superapp.entities.TicketCheckIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TicketCheckInManager implements TicketCheckInService {

    private final TicketCheckInDao ticketCheckInDao;
    private final TicketService ticketService;
    private final EventService eventService;

    private final Logger logger = LoggerFactory.getLogger(TicketCheckInManager.class);
    private final EventDayService eventDayService;


    public TicketCheckInManager(TicketCheckInDao ticketCheckInDao, TicketService ticketService, EventService eventService, EventDayService eventDayService) {
        this.ticketCheckInDao = ticketCheckInDao;
        this.ticketService = ticketService;
        this.eventService = eventService;
        this.eventDayService = eventDayService;
    }


    @Override
    public void checkInToEvent(UUID ticketId, UUID eventDayId) {
        logger.info("Checking in ticket with ID: {} for event day ID: {}", ticketId, eventDayId);

        Ticket ticket = ticketService.getTicketEntityById(ticketId);
        EventDay eventDay = eventDayService.getEventDayEntityById(eventDayId);

        if (!ticket.getEvent().getId().equals(eventDay.getEvent().getId())) {
            logger.error("Ticket with ID: {} does not belong to the event day with ID: {}", ticketId, eventDayId);
            throw new IllegalArgumentException(TicketCheckInMessages.TICKET_EVENT_MISMATCH);
        }

        if (ticketCheckInDao.existsByTicket_IdAndEventDay_Id(ticketId, eventDayId)) {
            logger.error("Ticket with ID: {} has already been checked in for event day ID: {}", ticketId, eventDayId);
            throw new IllegalStateException(TicketCheckInMessages.TICKET_ALREADY_CHECKED_IN);
        }



        TicketCheckIn ticketCheckIn =  TicketCheckIn.builder()
                .ticket(ticket)
                .eventDay(eventDay)
                .build();


         ticketCheckInDao.save(ticketCheckIn);

         logger.info("Successfully checked in ticket with ID: {} for event day ID: {}", ticketId, eventDayId);

    }
}
