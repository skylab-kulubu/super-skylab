package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Announcement;
import com.skylab.superapp.entities.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnnouncementDao extends JpaRepository<Announcement, UUID> {

    List<Announcement> findAllByEventType(EventType eventType);
}
