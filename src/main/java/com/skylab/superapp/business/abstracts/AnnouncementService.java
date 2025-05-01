package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.Announcement;
import com.skylab.superapp.entities.DTOs.Announcement.CreateAnnouncementDto;
import com.skylab.superapp.entities.DTOs.Announcement.GetAnnouncementDto;

import java.util.List;

public interface AnnouncementService {

    Result addAnnouncement(CreateAnnouncementDto createAnnouncementDto);

    Result deleteAnnouncement(int id);

    Result updateAnnouncement(GetAnnouncementDto getAnnouncementDto);

    DataResult<List<GetAnnouncementDto>> getAllAnnouncementsByTenant(String tenant);

    DataResult<List<GetAnnouncementDto>> getAllAnnouncementsByTenantAndType(String tenant, String type);

    DataResult<Announcement> getAnnouncementEntityById(int id);

    Result addPhotosToAnnouncement(int id, List<Integer> photoIds);
}
