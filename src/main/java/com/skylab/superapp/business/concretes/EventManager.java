package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.*;
import com.skylab.superapp.core.results.*;
import com.skylab.superapp.dataAccess.EventDao;
import com.skylab.superapp.entities.DTOs.Event.CreateEventDto;
import com.skylab.superapp.entities.DTOs.Event.GetBizbizeEventDto;
import com.skylab.superapp.entities.DTOs.Event.GetEventDto;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.Image;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EventManager implements EventService {

    private final EventDao eventDao;
    private final UserService userService;
    private final CompetitorService competitorService;
    private final ImageService imageService;
    private final EventTypeService eventTypeService;

    public EventManager(CompetitorService competitorService, EventDao eventDao, UserService userService,
                        ImageService imageService, EventTypeService eventTypeService) {
        this.competitorService = competitorService;
        this.eventDao = eventDao;
        this.userService = userService;
        this.imageService = imageService;
        this.eventTypeService = eventTypeService;
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

        var eventTypeDataResult = eventTypeService.getEventTypeByName(createEventDto.getType());
        if (!eventTypeDataResult.isSuccess()) {
            return new ErrorDataResult<>(eventTypeDataResult.getMessage(), eventTypeDataResult.getHttpStatus());
        }

        Event event = Event.builder()
                .title(createEventDto.getTitle())
                .date(createEventDto.getDate())
                .isActive(createEventDto.isActive())
                .guestName(createEventDto.getGuestName())
                .description(createEventDto.getDescription())
                .linkedin(createEventDto.getLinkedin())
                .formUrl(createEventDto.getFormUrl())
                .type(eventTypeDataResult.getData())
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

        var tenantCheck = userService.tenantCheck(event.getType().getName(), username);
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

        var tenantCheck = userService.tenantCheck(event.getType().getName(), username);
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

        var tenantCheck = userService.tenantCheck(event.getType().getName(), username);
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

        if (getBizbizeEventDto.getType() != null) {
            var eventTypeResult = eventTypeService.getEventTypeByName(getBizbizeEventDto.getType());
            if (eventTypeResult.isSuccess()) {
                event.setType(eventTypeResult.getData());
            }
        }

        eventDao.save(event);
        return new SuccessResult(EventMessages.EventUpdateSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetEventDto>> getAllEventsByEventType(String tenant) {
        if (tenant == null || tenant.isEmpty()) {
            return new ErrorDataResult<>(EventMessages.TenantCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        var events = eventDao.findAllByType_NameOrderByDateDesc(tenant);
        if (events.isEmpty()) {
            return new ErrorDataResult<>(EventMessages.EventNotFound, HttpStatus.NOT_FOUND);
        }

        var returnEvents = GetEventDto.buildListGetEventDto(events);
        return new SuccessDataResult<>(returnEvents, EventMessages.EventGetSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetBizbizeEventDto>> getAllBizbizeEvents() {
        var events = eventDao.findAllByType_NameOrderByDateDesc("BIZBIZE");
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
    public Result addImagesToEvent(int eventId, List<Integer> imageIds) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var event = eventDao.findById(eventId);
        if (event == null) {
            return new ErrorResult(EventMessages.EventNotFound, HttpStatus.NOT_FOUND);
        }

        var tenantCheck = userService.tenantCheck(event.getType().getName(), username);
        if (!tenantCheck) {
            return new ErrorResult(EventMessages.UserNotAuthorized, HttpStatus.FORBIDDEN);
        }

        var images = imageService.getImagesByIds(imageIds);
        if (!images.isSuccess()) {
            return new ErrorResult(images.getMessage(), images.getHttpStatus());
        }

        List<Image> imageList = new ArrayList<>();
        for (var image : images.getData()) {
            if (image.getEvent() != null) {
                return new ErrorResult(EventMessages.ImageAlreadyAdded, HttpStatus.BAD_REQUEST);
            }
            image.setEvent(event);
            imageList.add(image);
        }
        event.setImages(imageList);

        eventDao.save(event);
        return new SuccessResult(EventMessages.EventImagesAddedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetEventDto>> getAllFutureEventsByTenant(String tenant) {
        if (tenant == null || tenant.isEmpty()) {
            return new ErrorDataResult<>(EventMessages.TenantCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        var events = eventDao.findAllByType_NameAndDateAfter(tenant, new Date());
        if (events.isEmpty()) {
            return new ErrorDataResult<>(EventMessages.EventNotFound, HttpStatus.NOT_FOUND);
        }

        var returnEvents = GetEventDto.buildListGetEventDto(events);
        return new SuccessDataResult<>(returnEvents, EventMessages.EventGetSuccess, HttpStatus.OK);
    }
}