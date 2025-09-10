package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.Announcement;
import com.skylab.superapp.entities.DTOs.Announcement.AnnouncementDto;
import com.skylab.superapp.entities.DTOs.Announcement.CreateAnnouncementRequest;
import com.skylab.superapp.entities.DTOs.Announcement.UpdateAnnouncementRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

public interface AnnouncementService {

    AnnouncementDto addAnnouncement(CreateAnnouncementRequest createAnnouncementRequest, HttpServletRequest request);

    void deleteAnnouncement(UUID id);

    AnnouncementDto updateAnnouncement(UUID id, UpdateAnnouncementRequest updateAnnouncementRequest);

    List<AnnouncementDto> getAllAnnouncements(boolean includeUser, boolean includeEventType, boolean includeImages);

    AnnouncementDto getAnnouncementById(UUID id, boolean includeUser, boolean includeEventType, boolean includeImages);

    List<AnnouncementDto> getAllAnnouncementsByEventTypeId(UUID eventTypeId, boolean includeUser,
                                                           boolean includeEventType, boolean includeImages);

    void addImagesToAnnouncement(UUID id, List<UUID> imageIds);

    Announcement getAnnouncementEntityById(UUID id);
}
