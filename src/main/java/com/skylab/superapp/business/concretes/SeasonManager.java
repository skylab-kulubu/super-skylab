package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.business.abstracts.SeasonService;
import com.skylab.superapp.core.constants.SeasonMessages;
import com.skylab.superapp.core.exceptions.*;
import com.skylab.superapp.core.mappers.SeasonMapper;
import com.skylab.superapp.dataAccess.SeasonDao;
import com.skylab.superapp.entities.DTOs.season.CreateSeasonRequest;
import com.skylab.superapp.entities.DTOs.season.SeasonDto;
import com.skylab.superapp.entities.DTOs.season.UpdateSeasonRequest;
import com.skylab.superapp.entities.Season;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SeasonManager implements SeasonService {

    private final SeasonDao seasonDao;
    private final EventService eventService;
    private final SeasonMapper seasonMapper;

    public SeasonManager(SeasonDao seasonDao, @Lazy EventService eventService, SeasonMapper seasonMapper) {
        this.seasonDao = seasonDao;
        this.eventService = eventService;
        this.seasonMapper = seasonMapper;
    }

    @Override
    @Transactional
    public SeasonDto addSeason(CreateSeasonRequest createSeasonRequest) {
        if(createSeasonRequest.getName() == null || createSeasonRequest.getName().isEmpty()) {
           throw new ValidationException(SeasonMessages.SEASON_NAME_CANNOT_BE_NULL_OR_BLANK);
        }

        if(seasonDao.existsByName(createSeasonRequest.getName())) {
            throw new SeasonNameAlreadyExistsException();
        }

        Season season = Season.builder()
                .name(createSeasonRequest.getName())
                .startDate(createSeasonRequest.getStartDate())
                .endDate(createSeasonRequest.getEndDate())
                .active(createSeasonRequest.isActive())
                .build();

        return seasonMapper.toDto(seasonDao.save(season));
    }

    @Override
    public void deleteSeason(UUID id) {
        var season = getSeasonEntityById(id);

        seasonDao.delete(season);
    }

    @Override
    @Transactional
    public SeasonDto updateSeason(UUID id, UpdateSeasonRequest updateSeasonRequest) {
        var season = getSeasonEntityById(id);


        var newStartDate = updateSeasonRequest.getStartDate() != null ? updateSeasonRequest.getStartDate() : season.getStartDate();
        var newEndDate = updateSeasonRequest.getEndDate() != null ? updateSeasonRequest.getEndDate() : season.getEndDate();

        if (newStartDate != null && newEndDate != null && newStartDate.isAfter(newEndDate)) {
            throw new SeasonStartDateCannotBeAfterEndDateException();
        }

        if (updateSeasonRequest.getStartDate() != null) season.setStartDate(updateSeasonRequest.getStartDate());
        if (updateSeasonRequest.getEndDate() != null) season.setEndDate(updateSeasonRequest.getEndDate());

        season.setActive(updateSeasonRequest.isActive());

        return seasonMapper.toDto(seasonDao.save(season));
    }

    @Override
    public List<SeasonDto> getAllSeasons(boolean includeEvents) {
       return seasonMapper.toDtoList(seasonDao.findAll(), includeEvents);
    }

    @Override
    public SeasonDto getSeasonByName(String name, boolean includeEvents) {
        if(name == null || name.isEmpty()) {
            throw new ValidationException(SeasonMessages.SEASON_NAME_CANNOT_BE_NULL_OR_BLANK);
        }
        var result = seasonDao.findByName(name);
       if (result.isEmpty()){
           throw new ResourceNotFoundException(SeasonMessages.SEASON_NOT_FOUND);
       }

        return seasonMapper.toDto(result.get());
    }

    @Override
    public SeasonDto getSeasonById(UUID id, boolean includeEvents) {
        var season = getSeasonEntityById(id);
        return seasonMapper.toDto(season, includeEvents);
    }


    @Override
    public List<SeasonDto> getActiveSeasons(boolean includeEvents) {
        return seasonMapper.toDtoList(seasonDao.findAllByActive(true), includeEvents);
    }

    @Transactional
    @Override
    public void addEventToSeason(UUID seasonId, UUID eventId) {
        var season = getSeasonEntityById(seasonId);
        var event = eventService.getEventEntityById(eventId);


        if (season.getEvents().contains(event)) {
            throw new SeasonAlreadyContainsEventException();
        }

        if (event.getSeason() != null && event.getSeason().getId() != season.getId()) {
            throw new BusinessException(SeasonMessages.EVENT_IS_IN_ANOTHER_SEASON);
        }

        season.getEvents().add(event);
        event.setSeason(season);

        seasonDao.save(season);

    }

    @Transactional
    @Override
    public void removeEventFromSeason(UUID seasonId, UUID eventId) {
        var season = getSeasonEntityById(seasonId);
        var event = eventService.getEventEntityById(eventId); // Renamed


        if (!season.getEvents().contains(event) || event.getSeason() == null || event.getSeason().getId() != season.getId() ) {
            throw new SeasonDoesNotContainEventException();
        }

        season.getEvents().remove(event);
        event.setSeason(null);

        seasonDao.save(season);

    }

    @Override
    public Season getSeasonEntityById(UUID id) {
        return seasonDao.findById(id).orElseThrow(()-> new ResourceNotFoundException(SeasonMessages.SEASON_NOT_FOUND));
    }
}
