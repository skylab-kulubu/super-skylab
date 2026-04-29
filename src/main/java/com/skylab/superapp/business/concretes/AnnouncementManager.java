package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.*;
import com.skylab.superapp.core.constants.AnnouncementMessages;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.exceptions.ValidationException;
import com.skylab.superapp.core.mappers.AnnouncementMapper;
import com.skylab.superapp.dataAccess.AnnouncementDao;
import com.skylab.superapp.entities.Announcement;
import com.skylab.superapp.entities.DTOs.Announcement.AnnouncementDto;
import com.skylab.superapp.entities.DTOs.Announcement.CreateAnnouncementRequestDto;
import com.skylab.superapp.entities.DTOs.Announcement.UpdateAnnouncementRequest;
import com.skylab.superapp.entities.Image;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnnouncementManager implements AnnouncementService {

    private final AnnouncementDao announcementDao;
    private final ImageService imageService;
    private final EventTypeService eventTypeService;
    private final AnnouncementMapper announcementMapper;
    private final MediaService mediaService;


    public AnnouncementManager(AnnouncementDao announcementDao,
                               ImageService imageService,
                               EventTypeService eventTypeService,
                               AnnouncementMapper announcementMapper, MediaService mediaService) {
        this.announcementDao = announcementDao;
        this.imageService = imageService;
        this.eventTypeService = eventTypeService;
        this.announcementMapper = announcementMapper;
        this.mediaService = mediaService;
    }


    @Override
    public AnnouncementDto addAnnouncement(CreateAnnouncementRequestDto request) {
        var eventType = eventTypeService.getEventTypeEntityById(request.getEventTypeId());

        Image coverImage = null;
        if (request.getCoverImageId() != null) {
            mediaService.attachImageMedia(request.getCoverImageId());
            coverImage = imageService.getImageEntityById(request.getCoverImageId());
        }

        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .body(request.getBody())
                .active(request.isActive())
                .formUrl(request.getFormUrl())
                .eventType(eventType)
                .coverImage(coverImage)
                .build();

        return announcementMapper.toDto(announcementDao.save(announcement));
    }

    @Override
    public void deleteAnnouncement(UUID id) {
        var result = announcementDao.findById(id).orElseThrow(() ->new ResourceNotFoundException(AnnouncementMessages.ANNOUNCEMENT_NOT_FOUND));

        announcementDao.delete(result);
    }

    @Override
    @Transactional
    public AnnouncementDto updateAnnouncement(UUID id, UpdateAnnouncementRequest updateAnnouncementRequest) {
        var announcement = getAnnouncementEntityById(id);

        if (updateAnnouncementRequest.getEventTypeId() != null) {
            var eventType = eventTypeService.getEventTypeEntityById(updateAnnouncementRequest.getEventTypeId());
            announcement.setEventType(eventType);
        }

        if (updateAnnouncementRequest.getTitle() != null && updateAnnouncementRequest.getTitle().isEmpty()) {
            throw new ValidationException(AnnouncementMessages.ANNOUNCEMENT_TITLE_EMPTY);
        }

        if (updateAnnouncementRequest.getBody() != null && updateAnnouncementRequest.getBody().isEmpty()) {
            throw new ValidationException(AnnouncementMessages.BODY_NOT_EMPTY);
        }


        if (updateAnnouncementRequest.getTitle() != null) {
            announcement.setTitle(updateAnnouncementRequest.getTitle());
        }

        if (updateAnnouncementRequest.getBody() != null) {
            announcement.setBody(updateAnnouncementRequest.getBody());
        }

        if (updateAnnouncementRequest.getFormUrl() != null) {
            announcement.setFormUrl(updateAnnouncementRequest.getFormUrl());
        }

        if (updateAnnouncementRequest.getActive() != null) {
            announcement.setActive(updateAnnouncementRequest.getActive());
        }

        var savedAnnouncement = announcementDao.save(announcement);

        return announcementMapper.toDto(savedAnnouncement);
    }

    @Override
    public List<AnnouncementDto> getAllAnnouncements() {
        var result = announcementDao.findAll();
        if(result.isEmpty()){
            throw new ResourceNotFoundException(AnnouncementMessages.ANNOUNCEMENT_NOT_FOUND);
        }

        return result.stream()
                .map(announcementMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AnnouncementDto getAnnouncementById(UUID id) {
        var announcement = getAnnouncementEntityById(id);

        return announcementMapper.toDto(announcement);
    }

    @Override
    public List<AnnouncementDto> getAllAnnouncementsByEventTypeId(UUID eventTypeId) {
        var eventType = eventTypeService.getEventTypeEntityById(eventTypeId);

        var announcements = announcementDao.findAllByEventType(eventType);

        return announcements.stream()
                .map(announcementMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public Announcement getAnnouncementEntityById(UUID id) {
        return announcementDao.findById(id).orElseThrow(() -> new ResourceNotFoundException(AnnouncementMessages.ANNOUNCEMENT_NOT_FOUND));
    }
}
