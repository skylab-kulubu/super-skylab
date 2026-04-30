package com.skylab.superapp.business.concretes;

import com.skylab.superapp.core.exceptions.BusinessException;
import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.dataAccess.TicketDao;
import com.skylab.superapp.entities.*;
import com.skylab.superapp.entities.DTOs.ticket.request.GuestTicketRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketFactory {

    private final TicketDao ticketDao;

    public Ticket createRegisteredTicket(User owner, Event event) {
        if (ticketDao.existsByOwner_IdAndEvent_Id(owner.getId(), event.getId())) {
            log.warn("Ticket creation failed: User already registered. EventId: {}, UserId: {}",
                    event.getId(), owner.getId());
            throw new BusinessException(EventMessages.USER_ALREADY_REGISTERED);
        }

        Ticket ticket = Ticket.builder()
                .owner(owner)
                .event(event)
                .ticketType(TicketType.REGISTERED)
                .sent(false)
                .build();

        return ticketDao.save(ticket);
    }

    public Ticket createGuestTicket(Event event, GuestTicketRequestDto request) {
        if (ticketDao.existsByGuestEmailAndEvent_Id(request.getEmail(), event.getId())) {
            log.warn("Guest ticket creation failed: Email already registered. EventId: {}, Email: {}",
                    event.getId(), request.getEmail());
            throw new BusinessException(EventMessages.GUEST_TICKET_ALREADY_EXISTS);
        }

        Ticket ticket = Ticket.builder()
                .event(event)
                .ticketType(TicketType.GUEST)
                .guestFirstName(request.getFirstName())
                .guestLastName(request.getLastName())
                .guestEmail(request.getEmail())
                .guestPhoneNumber(request.getPhoneNumber())
                .guestBirthday(request.getBirthDate())
                .guestIsStudent(request.getIsStudent())
                .guestUniversity(request.getUniversity())
                .guestFaculty(request.getFaculty())
                .guestDepartment(request.getDepartment())
                .guestGrade(request.getGrade())
                .guestTcIdentityNumber(request.getTcIdentityNumber())
                .guestCarPlateNumber(request.getCarPlateNumber())
                .customAnswers(request.getCustomAnswers())
                .sent(false)
                .build();

        return ticketDao.save(ticket);
    }

    public boolean existsByOwnerAndEvent(UUID ownerId, UUID eventId) {
        return ticketDao.existsByOwner_IdAndEvent_Id(ownerId, eventId);
    }

    public boolean existsByGuestEmailAndEvent(String email, UUID eventId) {
        return ticketDao.existsByGuestEmailAndEvent_Id(email, eventId);
    }
}