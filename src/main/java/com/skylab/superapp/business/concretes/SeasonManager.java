package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.business.abstracts.SeasonService;
import com.skylab.superapp.core.exceptions.*;
import com.skylab.superapp.dataAccess.SeasonDao;
import com.skylab.superapp.entities.DTOs.Season.CreateSeasonDto;
import com.skylab.superapp.entities.Season;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeasonManager implements SeasonService {

    private final SeasonDao seasonDao;
    private final EventService eventService;

    public SeasonManager(SeasonDao seasonDao,@Lazy EventService eventService) {
        this.seasonDao = seasonDao;
        this.eventService = eventService;
    }

    @Override
    public Season addSeason(CreateSeasonDto createSeasonDto) {
        if(createSeasonDto.getName() == null || createSeasonDto.getName().isEmpty()) {
           throw new SeasonNameCannotBeNullOrBlankException();
        }

        if(seasonDao.existsByName(createSeasonDto.getName())) {
            throw new SeasonNameAlreadyExistsException();
        }

        Season season = Season.builder()
                .name(createSeasonDto.getName())
                .startDate(createSeasonDto.getStartDate())
                .endDate(createSeasonDto.getEndDate())
                .active(createSeasonDto.isActive())
                .build();

        return seasonDao.save(season);
    }

    @Override
    public void deleteSeason(int id) {

        var season = getSeasonEntity(id);

        seasonDao.delete(season);
    }

    @Override
    public List<Season> getAllSeasons() {
       return seasonDao.findAll();
    }

    @Override
    public Season getSeasonByName(String name) {
        if(name == null || name.isEmpty()) {
            throw new SeasonNameCannotBeNullOrBlankException();
        }
        var result = seasonDao.findByName(name);
       if (result.isEmpty()){
           throw new SeasonNotFoundException();
       }

        return result.get();
    }


    @Override
    public Season getSeasonById(int id) {
        return getSeasonEntity(id);
    }

    @Override
    public List<Season> getActiveSeasons() {
        return seasonDao.findAllByActive(true);
    }

    @Transactional
    @Override
    public void addEventToSeason(int seasonId, int eventId) {
        var season = getSeasonEntity(seasonId);
        var event = eventService.getEventById(eventId);


        if (season.getEvents().contains(event)) {
            throw new SeasonAlreadyContainsEventException();
        }

        if (event.getSeason() != null && event.getSeason().getId() != season.getId()) {
            throw new EventIsInAnotherSeasonException();
        }

        season.getEvents().add(event);
        event.setSeason(season);

        seasonDao.save(season);

    }

    @Transactional
    @Override
    public void removeEventFromSeason(int seasonId, int eventId) {
        var season = getSeasonEntity(seasonId);
        var event = eventService.getEventById(eventId); // Renamed



        if (!season.getEvents().contains(event) || event.getSeason() == null || event.getSeason().getId() != season.getId() ) {
            throw new SeasonDoesNotContainEventException();
        }


        season.getEvents().remove(event);
        event.setSeason(null);

        seasonDao.save(season);

    }



    private Season getSeasonEntity(int id){
        return seasonDao.findById(id).orElseThrow(SeasonNotFoundException::new);
    }
}
