package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.EventDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventDayDao extends JpaRepository<EventDay, UUID> {

    List<EventDay> findAllByEvent(Event event);
    boolean existsByEvent(Event event);

}
