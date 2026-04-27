package com.skylab.superapp.business.abstracts;

import java.util.UUID;

public interface TicketCheckInService {
    void checkInToEvent(UUID ticketId, UUID eventId);

}
