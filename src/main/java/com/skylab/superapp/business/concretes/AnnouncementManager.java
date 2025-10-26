package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.AnnouncementService;
import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.constants.AnnouncementMessages;
import com.skylab.superapp.core.exceptions.ImageAlreadyAddedException;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.exceptions.ValidationException;
import com.skylab.superapp.core.mappers.AnnouncementMapper;
import com.skylab.superapp.dataAccess.AnnouncementDao;
import com.skylab.superapp.entities.Announcement;
import com.skylab.superapp.entities.DTOs.Announcement.AnnouncementDto;
import com.skylab.superapp.entities.DTOs.Announcement.CreateAnnouncementRequestDto;
import com.skylab.superapp.entities.DTOs.Announcement.UpdateAnnouncementRequest;
import com.skylab.superapp.entities.Image;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnnouncementManager implements AnnouncementService {

    private final AnnouncementDao announcementDao;
    private final UserService userService;
    private final ImageService imageService;
    private final EventTypeService eventTypeService;
    private final AnnouncementMapper announcementMapper;


    public AnnouncementManager(AnnouncementDao announcementDao,
                               @Lazy UserService userService,
                               @Lazy ImageService imageService,
                               @Lazy EventTypeService eventTypeService,
                               AnnouncementMapper announcementMapper) {
        this.announcementDao = announcementDao;
        this.userService = userService;
        this.imageService = imageService;
        this.eventTypeService = eventTypeService;
        this.announcementMapper = announcementMapper;
    }


    @Override
    public AnnouncementDto addAnnouncement(CreateAnnouncementRequestDto createAnnouncementRequest, MultipartFile coverImage) {
        var eventType = eventTypeService.getEventTypeEntityById(createAnnouncementRequest.getEventTypeId());

        Image savedCoverImage = null;
        if (coverImage != null && !coverImage.isEmpty()) {
             savedCoverImage = imageService.uploadImage(coverImage);
        }

        Announcement announcement = Announcement.builder()
                .title(createAnnouncementRequest.getTitle())
                .body(createAnnouncementRequest.getBody())
                .active(createAnnouncementRequest.isActive())
                .formUrl(createAnnouncementRequest.getFormUrl())
                .eventType(eventType)
                .coverImage(savedCoverImage)
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
    public List<AnnouncementDto> getAllAnnouncements(boolean includeUser, boolean includeEventType, boolean includeImages) {
        var result = announcementDao.findAll();
        if(result.isEmpty()){
            throw new ResourceNotFoundException(AnnouncementMessages.ANNOUNCEMENT_NOT_FOUND);
        }

        return announcementMapper.toDtoList(result, includeUser, includeEventType, includeImages);
    }

    @Override
    public AnnouncementDto getAnnouncementById(UUID id, boolean includeEventType) {
        var announcement = getAnnouncementEntityById(id);

        return announcementMapper.toDto(announcement, includeEventType);
    }

    @Override
    public List<AnnouncementDto> getAllAnnouncementsByEventTypeId(UUID eventTypeId, boolean includeUser, boolean includeEventType, boolean includeImages) {
        var eventType = eventTypeService.getEventTypeEntityById(eventTypeId);

        return announcementMapper.toDtoList(announcementDao.findAllByEventType(eventType), includeUser, includeEventType, includeImages);
    }


    @Override
    public Announcement getAnnouncementEntityById(UUID id) {
        return announcementDao.findById(id).orElseThrow(() -> new ResourceNotFoundException(AnnouncementMessages.ANNOUNCEMENT_NOT_FOUND));
    }
}
