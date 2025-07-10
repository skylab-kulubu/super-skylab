package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventDao extends JpaRepository<Event, UUID> {

    Optional<Event> findById(UUID id);

    List<Event> findAllByActive(boolean active);

    List<Event> findAllByTypeAndStartDateIsAfterAndEndDateBefore(EventType eventType, LocalDateTime startDate, LocalDateTime endDate);

    List<Event> findAllByType(EventType eventType);
}
