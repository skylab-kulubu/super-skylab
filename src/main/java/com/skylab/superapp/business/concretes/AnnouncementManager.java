package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.AnnouncementService;
import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.exceptions.AnnouncementNotFoundException;
import com.skylab.superapp.core.exceptions.ImageAlreadyAddedException;
import com.skylab.superapp.core.mappers.AnnouncementMapper;
import com.skylab.superapp.dataAccess.AnnouncementDao;
import com.skylab.superapp.entities.Announcement;
import com.skylab.superapp.entities.DTOs.Announcement.AnnouncementDto;
import com.skylab.superapp.entities.DTOs.Announcement.CreateAnnouncementRequest;
import com.skylab.superapp.entities.DTOs.Announcement.UpdateAnnouncementRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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
    public AnnouncementDto addAnnouncement(CreateAnnouncementRequest createAnnouncementRequest) {
        // no need to check tenant, because tenants that doesnt have role wont be able to access this endpoint -yusssss
        /*
        var tenantCheck = userService.tenantCheck(createAnnouncementDto.getTenant(), username);
        if(!tenantCheck){
            throw new UserNotAuthorizedException();
        }

         */

        var author = userService.getAuthenticatedUserEntity();
        //controlleradvice handles this exception so no need to check any kind of business rules here -yusssss
        /*
        if(!author.isSuccess()){
            return new ErrorResult(author.getMessage(), author.getHttpStatus());
        }

         */

        var eventType = eventTypeService.getEventTypeEntityById(createAnnouncementRequest.getEventTypeId());
        /*
        if(!eventType.isSuccess()){
            return new ErrorResult(eventType.getMessage(), eventType.getHttpStatus());
        }
         */

        Announcement announcement = Announcement.builder()
                .body(createAnnouncementRequest.getBody())
                .createdAt(LocalDateTime.now())
                .date(createAnnouncementRequest.getDate())
                .title(createAnnouncementRequest.getTitle())
                .formUrl(createAnnouncementRequest.getFormUrl())
                .user(author)
                .active(createAnnouncementRequest.isActive())
                .eventType(eventType)
                .build();


        return announcementMapper.toDto(announcementDao.save(announcement));
    }

    @Override
    public void deleteAnnouncement(UUID id) {
        var result = announcementDao.findById(id).orElseThrow(AnnouncementNotFoundException::new);

        /*
        var tenantCheck = userService.tenantCheck(result.getType().getName(), username);

        if(!tenantCheck){
            return new ErrorResult(AnnouncementMessages.UserNotAuthorized, HttpStatus.UNAUTHORIZED);
        }

         */

        announcementDao.delete(result);
    }

    @Override
    public AnnouncementDto updateAnnouncement(UUID id, UpdateAnnouncementRequest updateAnnouncementRequest) {
        var announcement = getAnnouncementEntityById(id);

        /*
        var tenantCheck = userService.tenantCheck(result.getType().getName(), username);
        if(!tenantCheck){
            return new ErrorResult(AnnouncementMessages.UserNotAuthorized, HttpStatus.UNAUTHORIZED);
        }

         */
        if (updateAnnouncementRequest.getEventTypeId() != null) {
            var eventType = eventTypeService.getEventTypeEntityById(updateAnnouncementRequest.getEventTypeId());
            /*
            if(!eventType.isSuccess()){
                return new ErrorResult(eventType.getMessage(), eventType.getHttpStatus());
            }
             */
            announcement.setEventType(eventType);
        }

        announcement.setBody(updateAnnouncementRequest.getBody() == null ? announcement.getBody() : updateAnnouncementRequest.getBody());
        announcement.setTitle(updateAnnouncementRequest.getTitle() == null ? announcement.getTitle() : updateAnnouncementRequest.getTitle());


        return announcementMapper.toDto(announcementDao.save(announcement));
    }

    @Override
    public List<AnnouncementDto> getAllAnnouncements(boolean includeUser, boolean includeEventType, boolean includeImages) {
        var result = announcementDao.findAll();
        if(result.isEmpty()){
            throw new AnnouncementNotFoundException();
        }

        return announcementMapper.toDtoList(result, includeUser, includeEventType, includeImages);
    }

    @Override
    public AnnouncementDto getAnnouncementById(UUID id, boolean includeUser, boolean includeEventType, boolean includeImages) {
        var announcement = getAnnouncementEntityById(id);

        return announcementMapper.toDto(announcement, includeUser, includeEventType, includeImages);
    }

    @Override
    public List<AnnouncementDto> getAllAnnouncementsByEventTypeId(UUID eventTypeId, boolean includeUser, boolean includeEventType, boolean includeImages) {
        var eventType = eventTypeService.getEventTypeEntityById(eventTypeId);

        return announcementMapper.toDtoList(announcementDao.findAllByEventType(eventType), includeUser, includeEventType, includeImages);
    }


    @Override
    public void addImagesToAnnouncement(UUID id, List<UUID> imageIds) {
        var announcement = getAnnouncementEntityById(id);
/*
        var tenantCheck = userService.tenantCheck(announcement.getType().getName(), username);
        if(!tenantCheck){
            return new ErrorResult(AnnouncementMessages.UserNotAuthorized, HttpStatus.UNAUTHORIZED);
        }

 */

        var images = imageService.getImagesByIds(imageIds);
        /*
        if(!images.isSuccess()){
            return new ErrorResult(images.getMessage(), images.getHttpStatus());
        }
         */

        for (var image : images) {
            if(image.getAnnouncement() != null){
                throw new ImageAlreadyAddedException();
            }
            image.setAnnouncement(announcement);
        }

        announcementDao.save(announcement);
    }

    @Override
    public Announcement getAnnouncementEntityById(UUID id) {
        return announcementDao.findById(id).orElseThrow(AnnouncementNotFoundException::new);
    }
}
