package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventTypeDao extends JpaRepository<EventType, UUID> {
    Optional<EventType> findByName(String name);
}