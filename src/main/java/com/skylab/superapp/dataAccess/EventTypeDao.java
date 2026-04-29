package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventTypeDao extends JpaRepository<EventType, UUID> {
    Optional<EventType> findByName(String name);

    @Query("SELECT COUNT(e) > 0 FROM Event e WHERE e.type.id = :id")
    boolean existsEventByTypeId(@Param("id") UUID id);

    @Query("SELECT COUNT(a) > 0 FROM Announcement a WHERE a.eventType.id = :id")
    boolean existsAnnouncementByTypeId(@Param("id") UUID id);

}