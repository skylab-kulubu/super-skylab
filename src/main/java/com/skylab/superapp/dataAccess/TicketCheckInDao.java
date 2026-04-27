package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.TicketCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketCheckInDao extends JpaRepository<TicketCheckIn, UUID> {
    boolean existsByTicket_IdAndEventDay_Id(UUID ticketİd, UUID eventDayİd);
}
