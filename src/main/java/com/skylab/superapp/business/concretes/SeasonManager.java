package com.skylab.superapp.business.concretes;


import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.business.abstracts.SeasonService;
import com.skylab.superapp.business.constants.SeasonMessages;
import com.skylab.superapp.core.results.*;
import com.skylab.superapp.dataAccess.SeasonDao;
import com.skylab.superapp.entities.DTOs.Season.CreateSeasonDto;
import com.skylab.superapp.entities.DTOs.Season.GetSeasonDto;
import com.skylab.superapp.entities.DTOs.User.GetUserDto;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.Season;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeasonManager implements SeasonService {

    private final SeasonDao seasonDao;
    private final EventService eventService;
    private final EventTypeService eventTypeService;

    public SeasonManager(SeasonDao seasonDao, @Lazy EventService eventService, EventTypeService eventTypeService) {
        this.seasonDao = seasonDao;
        this.eventService = eventService;
        this.eventTypeService = eventTypeService;
    }

    @Override
    public DataResult<Integer> addSeason(CreateSeasonDto createSeasonDto) {

        if(createSeasonDto.getName() == null || createSeasonDto.getName().isEmpty()) {
            return new ErrorDataResult<>(SeasonMessages.NameCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        if(seasonDao.existsByName(createSeasonDto.getName())) {
            return new ErrorDataResult<>(SeasonMessages.NameAlreadyExists, HttpStatus.BAD_REQUEST);
        }

        var eventTypeResult = eventTypeService.getEventTypeByName(createSeasonDto.getTenant());
        if(!eventTypeResult.isSuccess()) {
            return new ErrorDataResult<>(eventTypeResult.getMessage(), eventTypeResult.getHttpStatus());
        }

        Season season = Season.builder()
                .name(createSeasonDto.getName())
                .startDate(createSeasonDto.getStartDate())
                .endDate(createSeasonDto.getEndDate())
                .isActive(createSeasonDto.isActive())
                .type(eventTypeResult.getData())
                .build();

        seasonDao.save(season);
        return new SuccessDataResult<>(season.getId(), SeasonMessages.SeasonAddedSuccess, HttpStatus.CREATED);
    }

    @Override
    public Result deleteSeason(int id) {

        var season = seasonDao.findById(id);
        if(season == null) {
            return new ErrorResult(SeasonMessages.SeasonNotFound, HttpStatus.NOT_FOUND);
        }

        seasonDao.delete(season);
        return new SuccessResult(SeasonMessages.SeasonDeletedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetSeasonDto>> getAllSeasonsByTenant(String tenant) {
        if(tenant == null || tenant.isEmpty()) {
            return new ErrorDataResult<>(SeasonMessages.TenantCannotBeNull, HttpStatus.BAD_REQUEST);
        }
        var result = seasonDao.findAllByTenant(tenant);
        if(result.isEmpty()) {
            return new ErrorDataResult<>(SeasonMessages.SeasonNotFound, HttpStatus.NOT_FOUND);
        }
        var returnSeasons = GetSeasonDto.buildListGetSeasonDto(result);
        return new SuccessDataResult<>(returnSeasons, SeasonMessages.SeasonListedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetSeasonDto>> getAllSeasons() {
        var result = seasonDao.findAll();
        if(result.isEmpty()) {
            return new ErrorDataResult<>(SeasonMessages.SeasonNotFound, HttpStatus.NOT_FOUND);
        }

        var returnSeasons = GetSeasonDto.buildListGetSeasonDto(result);
        return new SuccessDataResult<>(returnSeasons, SeasonMessages.SeasonListedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<GetSeasonDto> getSeasonByName(String name) {
        if(name == null || name.isEmpty()) {
            return new ErrorDataResult<>(SeasonMessages.NameCannotBeNull, HttpStatus.BAD_REQUEST);
        }
        var result = seasonDao.findByName(name);
        if(result == null) {
            return new ErrorDataResult<>(SeasonMessages.SeasonNotFound, HttpStatus.NOT_FOUND);
        }

        var returnSeason = new GetSeasonDto(result);
        return new SuccessDataResult<>(returnSeason, SeasonMessages.SeasonListedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<Season> getSeasonEntityById(int id) {
        var result = seasonDao.findById(id);
        if(result == null) {
            return new ErrorDataResult<>(SeasonMessages.SeasonNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<>(result, SeasonMessages.SeasonListedSuccess, HttpStatus.OK);
    }



    @Override
    public DataResult<GetSeasonDto> getSeasonById(int id) {
        var result = seasonDao.findById(id);
        if(result == null) {
            return new ErrorDataResult<>(SeasonMessages.SeasonNotFound, HttpStatus.NOT_FOUND);
        }
        var returnSeason = new GetSeasonDto(result);
        return new SuccessDataResult<>(returnSeason, SeasonMessages.SeasonListedSuccess, HttpStatus.OK);
    }

    @Override
    public Result addEventToSeason(int seasonId, int eventId) {
        var season = seasonDao.findById(seasonId);
        if (season == null) {
            return new ErrorResult(SeasonMessages.SeasonNotFound, HttpStatus.NOT_FOUND);
        }

        var eventResult = eventService.getEventEntityById(eventId); // Renamed to avoid confusion
        if (!eventResult.isSuccess()) {
            return new ErrorResult(eventResult.getMessage(), eventResult.getHttpStatus());
        }
        Event eventEntity = eventResult.getData();


        if (season.getEvents().contains(eventEntity)) {
            return new ErrorResult(SeasonMessages.EventAlreadyExistsInSeason, HttpStatus.BAD_REQUEST);
        }

        if (eventEntity.getSeason() != null && eventEntity.getSeason().getId() == season.getId()) {
            return new ErrorResult(SeasonMessages.EventAlreadyExistsInSeason, HttpStatus.BAD_REQUEST);
        }

        if (eventEntity.getSeason() != null && eventEntity.getSeason().getId() != season.getId()) {
            return new ErrorResult(SeasonMessages.EventIsInAnotherSeason, HttpStatus.BAD_REQUEST);
        }


        // --- FIX STARTS HERE ---
        // 1. Add to the collection in Season (for in-memory consistency of the Season object)
        season.getEvents().add(eventEntity);
        // 2. Set the owning side of the relationship (CRUCIAL for DB persistence)
        eventEntity.setSeason(season);
        // --- FIX ENDS HERE ---

        // Save the season. If Event entities are managed and dirty-checking is active,
        // changes to eventEntity (like setting its season) might be persisted automatically
        // when the transaction commits.
        // Saving season might cascade if configured, but explicitly saving eventEntity
        // after modifying its FK relationship is often clearer if no cascading is set up for this.
        // However, since you are calling seasonDao.save(season), and if your method is @Transactional,
        // the persistence context should manage the update to eventEntity as well because it's now dirty.
        seasonDao.save(season);
        // If the above doesn't update the event's season_id, you might need to explicitly save the event:
        // eventRepository.save(eventEntity); // Assuming you have an eventRepository or similar in eventService

        return new SuccessResult(SeasonMessages.EventAddedToSeasonSuccess, HttpStatus.OK);
    }

    @Override
    public Result removeEventFromSeason(int seasonId, int eventId) {
        var season = seasonDao.findById(seasonId);
        if (season == null) {
            return new ErrorResult(SeasonMessages.SeasonNotFound, HttpStatus.NOT_FOUND);
        }

        var eventResult = eventService.getEventEntityById(eventId); // Renamed
        if (!eventResult.isSuccess()) {
            return new ErrorResult(eventResult.getMessage(), eventResult.getHttpStatus());
        }
        Event eventEntity = eventResult.getData(); // Get the actual Event entity

        // Check if the event is actually in this season's collection
        // or more directly, if the event's season is this season.
        if (!season.getEvents().contains(eventEntity) || eventEntity.getSeason() == null || eventEntity.getSeason().getId() != season.getId() ) {
            // The event.getSeason() check is more robust as 'contains' relies on equals/hashCode and might miss if list isn't perfectly managed
            return new ErrorResult(SeasonMessages.EventNotFoundInSeason, HttpStatus.NOT_FOUND);
        }

        // --- FIX STARTS HERE ---
        // 1. Remove from the collection in Season (for in-memory consistency)
        season.getEvents().remove(eventEntity);
        // 2. Set the owning side of the relationship to null (CRUCIAL for DB persistence)
        eventEntity.setSeason(null);
        // --- FIX ENDS HERE ---

        seasonDao.save(season);
        // Again, you might need to explicitly save the eventEntity if its state change isn't cascaded
        // or picked up by dirty checking through seasonDao.save(season).
        // eventRepository.save(eventEntity); // To persist the eventEntity with season_id = null

        return new SuccessResult(SeasonMessages.EventRemovedFromSeasonSuccess, HttpStatus.OK);
    }
}
