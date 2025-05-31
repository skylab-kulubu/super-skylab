package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EventTypeDao extends JpaRepository<EventType, Integer> {
    Optional<EventType> findByName(String name);
}