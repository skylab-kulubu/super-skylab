package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.Ticket;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
        SELECT t FROM Ticket t
        LEFT JOIN t.owner u
        WHERE t.event.id = :eventId
        AND (
            LOWER(u.firstName)  LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.lastName)   LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(u.email)      LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(t.guestFirstName)  LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(t.guestLastName)   LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(t.guestEmail)      LIKE LOWER(CONCAT('%', :q, '%'))
        )
    """)
    List<Ticket> searchByEventIdAndQuery(@Param("eventId") UUID eventId, @Param("q") String q);
}
