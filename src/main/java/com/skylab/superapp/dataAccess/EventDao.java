package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventDao extends JpaRepository<Event,Integer> {

    Optional<Event> findById(int id);

    List<Event> findAllByActive(boolean active);

    List<Event> findAllByTypeAndDateAfter(EventType eventType, Date date);

    List<Event> findAllByType(EventType eventType);
}
