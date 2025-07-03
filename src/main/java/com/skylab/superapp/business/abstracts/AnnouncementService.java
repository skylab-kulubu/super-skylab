package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.Announcement;
import com.skylab.superapp.entities.DTOs.Announcement.CreateAnnouncementDto;
import com.skylab.superapp.entities.DTOs.Announcement.GetAnnouncementDto;

import java.util.List;

public interface AnnouncementService {

    void addAnnouncement(CreateAnnouncementDto createAnnouncementDto);

    void deleteAnnouncement(int id);

    void updateAnnouncement(GetAnnouncementDto getAnnouncementDto);

    List<Announcement> getAllAnnouncements();

    Announcement getAnnouncementById(int id);

    List<Announcement> getAllAnnouncementsByEventTypeName(String eventTypeName);

    List<Announcement> getAllAnnouncementsByEventTypeId(int eventTypeId);

    void addImagesToAnnouncement(int id, List<Integer> photoIds);
}
