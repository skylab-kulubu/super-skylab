package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.AnnouncementService;
import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.business.constants.AnnouncementMessages;
import com.skylab.superapp.core.results.*;
import com.skylab.superapp.dataAccess.AnnouncementDao;
import com.skylab.superapp.entities.Announcement;
import com.skylab.superapp.entities.DTOs.Announcement.CreateAnnouncementDto;
import com.skylab.superapp.entities.DTOs.Announcement.GetAnnouncementDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
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


    public AnnouncementManager(AnnouncementDao announcementDao, UserService userService, @Lazy ImageService imageService, EventTypeService eventTypeService) {
        this.announcementDao = announcementDao;
        this.userService = userService;
        this.imageService = imageService;
        this.eventTypeService = eventTypeService;
    }


    @Override
    public Result addAnnouncement(CreateAnnouncementDto createAnnouncementDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if(createAnnouncementDto.getTenant() == null || createAnnouncementDto.getTenant().isEmpty()){
            return new ErrorResult(AnnouncementMessages.tenantCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        var tenantCheck = userService.tenantCheck(createAnnouncementDto.getTenant(), username);
        if(!tenantCheck){
            return new ErrorResult(AnnouncementMessages.UserNotAuthorized, HttpStatus.UNAUTHORIZED);

        }

        var author = userService.getUserEntityByUsername(username);
        if(!author.isSuccess()){
            return new ErrorResult(author.getMessage(), author.getHttpStatus());
        }

        var eventType = eventTypeService.getEventTypeByName(createAnnouncementDto.getType());
        if(!eventType.isSuccess()){
            return new ErrorResult(eventType.getMessage(), eventType.getHttpStatus());
        }


        Announcement announcement = Announcement.builder()
                .content(createAnnouncementDto.getContent())
                .createdAt(new Date())
                .date(createAnnouncementDto.getDate())
                .description(createAnnouncementDto.getDescription())
                .title(createAnnouncementDto.getTitle())
                .formUrl(createAnnouncementDto.getFormUrl())
                .user(author.getData())
                .isActive(createAnnouncementDto.isActive())
                .type(eventType.getData())
                .build();


        announcementDao.save(announcement);
        return new SuccessResult(AnnouncementMessages.AnnouncementAddedSuccess, HttpStatus.CREATED);
    }

    @Override
    public Result deleteAnnouncement(int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var result = announcementDao.findById(id);
        if(result == null){
            return new ErrorResult(AnnouncementMessages.AnnouncementNotFound, HttpStatus.NOT_FOUND);
        }
        var tenantCheck = userService.tenantCheck(result.getType().getName(), username);

        if(!tenantCheck){
            return new ErrorResult(AnnouncementMessages.UserNotAuthorized, HttpStatus.UNAUTHORIZED);
        }

        announcementDao.delete(result);
        return new ErrorResult(AnnouncementMessages.DeleteSuccess, HttpStatus.OK);
    }

    @Override
    public Result updateAnnouncement(GetAnnouncementDto getAnnouncementDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var result = announcementDao.findById(getAnnouncementDto.getId());
        if(result == null){
            return new ErrorResult(AnnouncementMessages.AnnouncementNotFound, HttpStatus.NOT_FOUND);
        }

        var tenantCheck = userService.tenantCheck(result.getType().getName(), username);
        if(!tenantCheck){
            return new ErrorResult(AnnouncementMessages.UserNotAuthorized, HttpStatus.UNAUTHORIZED);
        }

        result.setContent(getAnnouncementDto.getContent() == null ? result.getContent() : getAnnouncementDto.getContent());
        result.setTitle(getAnnouncementDto.getTitle() == null ? result.getTitle() : getAnnouncementDto.getTitle());


        announcementDao.save(result);
        return new SuccessResult(AnnouncementMessages.UpdateSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetAnnouncementDto>> getAllAnnouncementsByTenant(String tenant) {
        var result = announcementDao.findAllByTenant(tenant);
        if(result.isEmpty()){
            return new ErrorDataResult<>(AnnouncementMessages.AnnouncementNotFound, HttpStatus.NOT_FOUND);
        }

        var returnAnnouncements = GetAnnouncementDto.buildListGetAnnouncementDto(result);
        return new SuccessDataResult<>(returnAnnouncements, AnnouncementMessages.GetAllSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetAnnouncementDto>> getAllAnnouncementsByTenantAndType(String tenant, String type) {
        var result = announcementDao.findAllByTenantAndType(tenant, type);
        if(result.isEmpty()){
            return new ErrorDataResult<>(AnnouncementMessages.AnnouncementNotFound, HttpStatus.NOT_FOUND);
        }

        var returnAnnouncements = GetAnnouncementDto.buildListGetAnnouncementDto(result);
        return new SuccessDataResult<>(returnAnnouncements, AnnouncementMessages.GetAllSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<Announcement> getAnnouncementEntityById(int id) {
        var result = announcementDao.findById(id);
        if(result == null){
            return new ErrorDataResult<>(AnnouncementMessages.AnnouncementNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<>(result, AnnouncementMessages.GetAnnouncementSuccess, HttpStatus.OK);
    }

    @Override
    public Result addPhotosToAnnouncement(int id, List<Integer> photoIds) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var announcement = announcementDao.findById(id);
        if(announcement == null){
            return new ErrorResult(AnnouncementMessages.AnnouncementNotFound, HttpStatus.NOT_FOUND);
        }

        var tenantCheck = userService.tenantCheck(announcement.getType().getName(), username);
        if(!tenantCheck){
            return new ErrorResult(AnnouncementMessages.UserNotAuthorized, HttpStatus.UNAUTHORIZED);
        }
        var photos = imageService.getImagesByIds(photoIds);
        if(!photos.isSuccess()){
            return new ErrorResult(photos.getMessage(), photos.getHttpStatus());
        }

        var photoList = photos.getData();
        for (var photo : photoList) {
            if(photo.getAnnouncement() != null){
                return new ErrorResult(AnnouncementMessages.PhotoAlreadyAdded, HttpStatus.BAD_REQUEST);
            }
            photo.setAnnouncement(announcement);
        }

        announcementDao.save(announcement);
        return new SuccessResult(AnnouncementMessages.AnnouncementPhotosAddedSuccess, HttpStatus.OK);
    }
}
