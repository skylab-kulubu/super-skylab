package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Announcement;
import com.skylab.superapp.entities.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementDao extends JpaRepository<Announcement, Integer> {

    Announcement findById(int id);

    List<Announcement> findAllByEventType(EventType eventType);
}
