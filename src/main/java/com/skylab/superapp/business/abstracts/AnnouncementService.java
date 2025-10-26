package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.Announcement;
import com.skylab.superapp.entities.DTOs.Announcement.AnnouncementDto;
import com.skylab.superapp.entities.DTOs.Announcement.CreateAnnouncementRequestDto;
import com.skylab.superapp.entities.DTOs.Announcement.UpdateAnnouncementRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface AnnouncementService {

    AnnouncementDto addAnnouncement(CreateAnnouncementRequestDto createAnnouncementRequest, MultipartFile coverImage);

    void deleteAnnouncement(UUID id);

    AnnouncementDto updateAnnouncement(UUID id, UpdateAnnouncementRequest updateAnnouncementRequest);

    List<AnnouncementDto> getAllAnnouncements(boolean includeUser, boolean includeEventType, boolean includeImages);

    AnnouncementDto getAnnouncementById(UUID id, boolean includeEventType);

    List<AnnouncementDto> getAllAnnouncementsByEventTypeId(UUID eventTypeId, boolean includeUser,
                                                           boolean includeEventType, boolean includeImages);

    Announcement getAnnouncementEntityById(UUID id);
}
