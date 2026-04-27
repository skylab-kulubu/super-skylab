package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SessionDao extends JpaRepository<Session, UUID> {
    List<Session> findAllByEventDay_Event(Event event);

}
