package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.AnnouncementService;
import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.exceptions.AnnouncementNotFoundException;
import com.skylab.superapp.core.exceptions.EventTypeNotFoundException;
import com.skylab.superapp.core.exceptions.ImageAlreadyAddedException;
import com.skylab.superapp.dataAccess.AnnouncementDao;
import com.skylab.superapp.entities.Announcement;
import com.skylab.superapp.entities.DTOs.Announcement.CreateAnnouncementDto;
import com.skylab.superapp.entities.DTOs.Announcement.GetAnnouncementDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AnnouncementManager implements AnnouncementService {

    private final AnnouncementDao announcementDao;
    private final UserService userService;
    private final ImageService imageService;
    private final EventTypeService eventTypeService;


    public AnnouncementManager(AnnouncementDao announcementDao,
                               @Lazy UserService userService,
                               @Lazy ImageService imageService,
                               @Lazy EventTypeService eventTypeService) {
        this.announcementDao = announcementDao;
        this.userService = userService;
        this.imageService = imageService;
        this.eventTypeService = eventTypeService;
    }


    @Override
    public void addAnnouncement(CreateAnnouncementDto createAnnouncementDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // no need to check tenant, because tenants that doesnt have role wont be able to access this endpoint -yusssss
        /*
        var tenantCheck = userService.tenantCheck(createAnnouncementDto.getTenant(), username);
        if(!tenantCheck){
            throw new UserNotAuthorizedException();
        }

         */

        var author = userService.getUserByUsername(username);
        //controlleradvice handles this exception so no need to check any kind of business rules here -yusssss
        /*
        if(!author.isSuccess()){
            return new ErrorResult(author.getMessage(), author.getHttpStatus());
        }

         */

        var eventType = eventTypeService.getEventTypeByName(createAnnouncementDto.getEventTypeName());
        /*
        if(!eventType.isSuccess()){
            return new ErrorResult(eventType.getMessage(), eventType.getHttpStatus());
        }
         */

        Announcement announcement = Announcement.builder()
                .content(createAnnouncementDto.getContent())
                .createdAt(new Date())
                .date(createAnnouncementDto.getDate())
                .description(createAnnouncementDto.getDescription())
                .title(createAnnouncementDto.getTitle())
                .formUrl(createAnnouncementDto.getFormUrl())
                .user(author)
                .isActive(createAnnouncementDto.isActive())
                .eventType(eventType)
                .build();


        announcementDao.save(announcement);
    }

    @Override
    public void deleteAnnouncement(int id) {
        var result = announcementDao.findById(id);
        if(result == null){
            throw new AnnouncementNotFoundException();
        }

        /*
        var tenantCheck = userService.tenantCheck(result.getType().getName(), username);

        if(!tenantCheck){
            return new ErrorResult(AnnouncementMessages.UserNotAuthorized, HttpStatus.UNAUTHORIZED);
        }

         */

        announcementDao.delete(result);
    }

    @Override
    public void updateAnnouncement(GetAnnouncementDto getAnnouncementDto) {
        var result = announcementDao.findById(getAnnouncementDto.getId());
        if(result == null){
            throw new AnnouncementNotFoundException();
        }

        /*
        var tenantCheck = userService.tenantCheck(result.getType().getName(), username);
        if(!tenantCheck){
            return new ErrorResult(AnnouncementMessages.UserNotAuthorized, HttpStatus.UNAUTHORIZED);
        }

         */

        result.setContent(getAnnouncementDto.getContent() == null ? result.getContent() : getAnnouncementDto.getContent());
        result.setTitle(getAnnouncementDto.getTitle() == null ? result.getTitle() : getAnnouncementDto.getTitle());


        announcementDao.save(result);
    }

    @Override
    public List<Announcement> getAllAnnouncements() {
        var result = announcementDao.findAll();
        if(result.isEmpty()){
            throw new AnnouncementNotFoundException();
        }

        return result;
    }

    @Override
    public Announcement getAnnouncementById(int id) {
        var result = announcementDao.findById(id);

        if(result == null){
            throw new AnnouncementNotFoundException();
        }

        return result;
    }

    @Override
    public List<Announcement> getAllAnnouncementsByEventTypeName(String eventTypeName) {
        var eventType = eventTypeService.getEventTypeByName(eventTypeName);
        if(eventType == null){
            throw new EventTypeNotFoundException();
        }

        return announcementDao.findAllByEventType(eventType);
    }

    @Override
    public List<Announcement> getAllAnnouncementsByEventTypeId(int eventTypeId) {
        var eventType = eventTypeService.getEventTypeById(eventTypeId);
        if(eventType == null){
            throw new EventTypeNotFoundException();
        }

        return announcementDao.findAllByEventType(eventType);
    }


    @Override
    public void addImagesToAnnouncement(int id, List<Integer> imageIds) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var announcement = announcementDao.findById(id);
        if(announcement == null){
            throw new AnnouncementNotFoundException();
        }
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

        var imageList = images;
        for (var image : imageList) {
            if(image.getAnnouncement() != null){
                throw new ImageAlreadyAddedException();
            }
            image.setAnnouncement(announcement);
        }

        announcementDao.save(announcement);
    }
}
