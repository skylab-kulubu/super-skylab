package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Announcement;
import com.skylab.superapp.entities.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementDao extends JpaRepository<Announcement, Integer> {

    Announcement findById(int id);

    List<Announcement> findByUserId(int userId);

    List<Announcement> findAllByTitleContainingIgnoreCase(String title);

    List<Announcement> findAllByContentContainingIgnoreCase(String content);

    List<Announcement> findAllByType_Name(String name);

    List<Announcement> findAllByEventType(EventType eventType);
}
