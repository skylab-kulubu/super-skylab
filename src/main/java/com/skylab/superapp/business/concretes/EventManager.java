package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.CompetitorService;
import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.business.abstracts.PhotoService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.business.constants.EventMessages;
import com.skylab.superapp.core.results.*;
import com.skylab.superapp.dataAccess.EventDao;
import com.skylab.superapp.entities.DTOs.Event.CreateEventDto;
import com.skylab.superapp.entities.DTOs.Event.GetBizbizeEventDto;
import com.skylab.superapp.entities.DTOs.Event.GetEventDto;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.Photo;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class EventManager implements EventService {

    private EventDao eventDao;
    private PhotoService photoService;
    private UserService userService;
    private CompetitorService competitorService;

    public EventManager(EventDao eventDao, @Lazy PhotoService photoService, @Lazy UserService userService, @Lazy CompetitorService competitorService) {
        this.eventDao = eventDao;
        this.photoService = photoService;
        this.userService = userService;
        this.competitorService = competitorService;
    }


    @Override
    public DataResult<Integer> addEvent(CreateEventDto createEventDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (createEventDto.getTenant() == null || createEventDto.getTenant().isEmpty()) {
            return new ErrorDataResult<>(EventMessages.TenantCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        boolean tenantCheck = userService.tenantCheck(createEventDto.getTenant(), username);
        if (!tenantCheck) {
            return new ErrorDataResult<>(EventMessages.UserNotAuthorized, HttpStatus.FORBIDDEN);
        }



        Event event = Event.builder()
                .title(createEventDto.getTitle())
                .date(createEventDto.getDate())
                .isActive(createEventDto.isActive())
                .guestName(createEventDto.getGuestName())
                .description(createEventDto.getDescription())
                .linkedin(createEventDto.getLinkedin())
                .formUrl(createEventDto.getFormUrl())
                .type(createEventDto.getType())
                .tenant(createEventDto.getTenant())
                .build();

        eventDao.save(event);
        return new SuccessDataResult<>(event.getId(), EventMessages.EventCreatedSuccess, HttpStatus.CREATED);
    }

    @Override
    public Result deleteEvent(int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

       var event = eventDao.findById(id);
        if (event == null) {
            return new ErrorResult(EventMessages.EventNotFound, HttpStatus.NOT_FOUND);
        }

        var tenantCheck = userService.tenantCheck(event.getTenant(), username);
        if (!tenantCheck) {
            return new ErrorResult(EventMessages.UserNotAuthorized, HttpStatus.FORBIDDEN);
        }



        eventDao.delete(event);
        return new SuccessResult(EventMessages.EventDeleteSuccess, HttpStatus.OK);
    }

    @Override
    public Result updateEvent(GetEventDto getEventDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var event = eventDao.findById(getEventDto.getId());
        if (event == null) {
            return new ErrorResult(EventMessages.EventNotFound, HttpStatus.NOT_FOUND);
        }

        var tenantCheck = userService.tenantCheck(event.getTenant(), username);
        if (!tenantCheck) {
            return new ErrorResult(EventMessages.UserNotAuthorized, HttpStatus.FORBIDDEN);
        }

        event.setTitle(getEventDto.getTitle() == null ? event.getTitle() : getEventDto.getTitle());
        event.setDate(getEventDto.getDate() == null ? event.getDate() : getEventDto.getDate());
        event.setActive(getEventDto.isActive() == event.isActive() ? event.isActive() : getEventDto.isActive());
        event.setDescription(getEventDto.getDescription() == null ? event.getDescription() : getEventDto.getDescription());

        eventDao.save(event);
        return new SuccessResult(EventMessages.EventUpdateSuccess, HttpStatus.OK);
    }

    @Override
    public Result updateBizbizeEvent(GetBizbizeEventDto getBizbizeEventDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var event = eventDao.findById(getBizbizeEventDto.getId());
        if (event == null) {
            return new ErrorResult(EventMessages.EventNotFound, HttpStatus.NOT_FOUND);
        }

        var tenantCheck = userService.tenantCheck(event.getTenant(), username);
        if (!tenantCheck) {
            return new ErrorResult(EventMessages.UserNotAuthorized, HttpStatus.FORBIDDEN);
        }

        event.setTitle(getBizbizeEventDto.getTitle() == null ? event.getTitle() : getBizbizeEventDto.getTitle());
        event.setDate(getBizbizeEventDto.getDate() == null ? event.getDate() : getBizbizeEventDto.getDate());
        event.setActive(getBizbizeEventDto.isActive() == event.isActive() ? event.isActive() : getBizbizeEventDto.isActive());
        event.setLinkedin(getBizbizeEventDto.getLinkedin() == null ? event.getLinkedin() : getBizbizeEventDto.getLinkedin());
        event.setFormUrl(getBizbizeEventDto.getFormUrl() == null ? event.getFormUrl() : getBizbizeEventDto.getFormUrl());
        event.setGuestName(getBizbizeEventDto.getGuestName() == null ? event.getGuestName() : getBizbizeEventDto.getGuestName());
        event.setDescription(getBizbizeEventDto.getDescription() == null ? event.getDescription() : getBizbizeEventDto.getDescription());
        event.setType(getBizbizeEventDto.getType() == null ? event.getType() : getBizbizeEventDto.getType());
        event.setDescription(getBizbizeEventDto.getDescription() == null ? event.getDescription() : getBizbizeEventDto.getDescription());

        eventDao.save(event);
        return new SuccessResult(EventMessages.EventUpdateSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetEventDto>> getAllEventsByTenant(String tenant) {
        if (tenant == null || tenant.isEmpty()) {
            return new ErrorDataResult<>(EventMessages.TenantCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        var events = eventDao.findAllByTenantOrderByDateDesc(tenant);
        if (events.isEmpty()) {
            return new ErrorDataResult<>(EventMessages.EventNotFound, HttpStatus.NOT_FOUND);
        }

        var returnEvents = GetEventDto.buildListGetEventDto(events);
        return new SuccessDataResult<>(returnEvents, EventMessages.EventGetSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetBizbizeEventDto>> getAllBizbizeEvents() {
        var events = eventDao.findAllByTenantOrderByDateDesc("BIZBIZE");
        if (events.isEmpty()) {
            return new ErrorDataResult<>(EventMessages.EventNotFound, HttpStatus.NOT_FOUND);
        }

        var returnEvents = GetBizbizeEventDto.buildListGetBizbizeEventDto(events);
        return new SuccessDataResult<>(returnEvents, EventMessages.EventGetSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<Event> getEventEntityById(int id) {
        var event = eventDao.findById(id);
        if (event == null) {
            return new ErrorDataResult<>(EventMessages.EventNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<>(event, EventMessages.EventGetSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetEventDto>> getAllEventsByTenantAndType(String tenant, String type) {
        if (tenant == null || tenant.isEmpty()) {
            return new ErrorDataResult<>(EventMessages.TenantCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        var events = eventDao.findAllByTenantAndType(tenant, type);
        if (events.isEmpty()) {
            return new ErrorDataResult<>(EventMessages.EventNotFound, HttpStatus.NOT_FOUND);
        }

        var returnEvents = GetEventDto.buildListGetEventDto(events);
        return new SuccessDataResult<>(returnEvents, EventMessages.EventGetSuccess, HttpStatus.OK);
    }

    @Override
    public Result addPhotosToEvent(int eventId, List<Integer> photoIds) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var event = eventDao.findById(eventId);
        if (event == null) {
            return new ErrorResult(EventMessages.EventNotFound, HttpStatus.NOT_FOUND);
        }

        var tenantCheck = userService.tenantCheck(event.getTenant(), username);
        if (!tenantCheck) {
            return new ErrorResult(EventMessages.UserNotAuthorized, HttpStatus.FORBIDDEN);
        }

       var photos = photoService.getPhotosByIds(photoIds);
        if(!photos.isSuccess()){
            return new ErrorResult(photos.getMessage(), photos.getHttpStatus());
        }

        List<Photo> photoList = new ArrayList<>();
        for (var photo : photos.getData()) {
            if (photo.getEvent() != null) {
                return new ErrorResult(EventMessages.PhotoAlreadyAdded, HttpStatus.BAD_REQUEST);
            }
            photo.setEvent(event);
            photoList.add(photo);
        }
        event.setPhotos(photoList);

        eventDao.save(event);
        return new SuccessResult(EventMessages.EventPhotosAddedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetEventDto>> getAllFutureEventsByTenant(String tenant) {
        if (tenant == null || tenant.isEmpty()) {
            return new ErrorDataResult<>(EventMessages.TenantCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        var events = eventDao.findAllByTenantAndDateAfter(tenant, new Date());
        if (events.isEmpty()) {
            return new ErrorDataResult<>(EventMessages.EventNotFound, HttpStatus.NOT_FOUND);
        }

        var returnEvents = GetEventDto.buildListGetEventDto(events);
        return new SuccessDataResult<>(returnEvents, EventMessages.EventGetSuccess, HttpStatus.OK);
    }



}
