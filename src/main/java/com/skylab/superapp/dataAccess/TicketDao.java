package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.Ticket;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketDao extends JpaRepository<Ticket, UUID> {
    boolean existsByOwner_IdAndEvent_Id(UUID ownerId, UUID eventId);

    Optional<Ticket> findByOwner_IdAndEvent_Id(UUID ownerId, UUID eventId);

    List<Ticket> findAllByOwner_Id(UUID ownerId);

    List<Ticket> findAllByOwner_Email(String email);

    boolean existsByGuestEmailAndEvent_Id(String email, UUID eventId);

    List<Ticket> findAllByGuestEmail(String email);

    List<Ticket> findAllByEvent(Event event);

    List<Ticket> findAllByEvent_Id(UUID eventId);
}
