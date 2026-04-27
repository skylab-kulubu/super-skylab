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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SeasonManager implements SeasonService {

    private final SeasonDao seasonDao;
    private final SeasonMapper seasonMapper;

    private static final Logger logger = LoggerFactory.getLogger(SeasonManager.class);

    public SeasonManager(SeasonDao seasonDao, SeasonMapper seasonMapper) {
        this.seasonDao = seasonDao;
        this.seasonMapper = seasonMapper;
    }

    @Override
    @Transactional
    public SeasonDto addSeason(CreateSeasonRequest createSeasonRequest) {
        logger.info("Adding new season with name: {}", createSeasonRequest.getName());

        if(seasonDao.existsByName(createSeasonRequest.getName())) {
            logger.error("Season with name: {} already exists", createSeasonRequest.getName());
            throw new SeasonNameAlreadyExistsException();
        }

        Season season = Season.builder()
                .name(createSeasonRequest.getName())
                .startDate(createSeasonRequest.getStartDate())
                .endDate(createSeasonRequest.getEndDate())
                .active(createSeasonRequest.isActive())
                .build();

        var savedSeason = seasonDao.save(season);

        logger.info("Season with name: {} created successfully with id: {}", savedSeason.getName(), savedSeason.getId());

        return seasonMapper.toDto(savedSeason);
    }

    @Override
    public void deleteSeason(UUID id) {
        logger.info("Deleting season with id: {}", id);
        var season = getSeasonEntityById(id);
        logger.info("Season with id: {} found, proceeding to delete", id);

        seasonDao.delete(season);

        logger.info("Season with id: {} deleted successfully", id);
    }

    @Override
    @Transactional
    public SeasonDto updateSeason(UUID id, UpdateSeasonRequest updateSeasonRequest) {
        logger.info("Updating season with id: {}", id);
        var season = getSeasonEntityById(id);

        logger.info("Season with id: {} found, proceeding to update", id);


        var newStartDate = updateSeasonRequest.getStartDate() != null ? updateSeasonRequest.getStartDate() : season.getStartDate();
        var newEndDate = updateSeasonRequest.getEndDate() != null ? updateSeasonRequest.getEndDate() : season.getEndDate();

        if (newStartDate != null && newEndDate != null && newStartDate.isAfter(newEndDate)) {
            throw new SeasonStartDateCannotBeAfterEndDateException();
        }

        if (updateSeasonRequest.getStartDate() != null) season.setStartDate(updateSeasonRequest.getStartDate());
        if (updateSeasonRequest.getEndDate() != null) season.setEndDate(updateSeasonRequest.getEndDate());

        season.setActive(updateSeasonRequest.isActive());

        var savedSeason = seasonDao.save(season);
        logger.info("Season with id: {} updated successfully", id);

        return seasonMapper.toDto(savedSeason);
    }

    @Override
    public List<SeasonDto> getAllSeasons() {
        logger.info("Getting all seasons");

        var seasons = seasonDao.findAll();

        logger.info("{} seasons found", seasons.size());

        return seasons.stream()
                .map(seasonMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public SeasonDto getSeasonByName(String name) {
        logger.info("Getting season with name: {}", name);
        if(name == null || name.isEmpty()) {
            logger.error("Season name cannot be null or empty");
            throw new ValidationException(SeasonMessages.SEASON_NAME_CANNOT_BE_NULL_OR_BLANK);
        }
        var result = seasonDao.findByName(name);
       if (result.isEmpty()){
           logger.error("Season with name: {} not found", name);
           throw new ResourceNotFoundException(SeasonMessages.SEASON_NOT_FOUND);
       }

       logger.info("Season with name: {} found, returning season", name);

        return seasonMapper.toDto(result.get());
    }

    @Override
    public SeasonDto getSeasonById(UUID id) {
        logger.info("Getting season with id: {}", id);
        var season = getSeasonEntityById(id);

        logger.info("Season with id: {} found, returning season", id);
        return seasonMapper.toDto(season);
    }


    @Override
    public List<SeasonDto> getActiveSeasons() {
        logger.info("Getting all active seasons");

        var activeSeasons = seasonDao.findAllByActive(true);

        logger.info("{} active seasons found", activeSeasons.size());

        return activeSeasons.stream()
                .map(seasonMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Season getSeasonEntityById(UUID id) {
        logger.info("Getting season entity with id: {}", id);
        return seasonDao.findById(id).orElseThrow(()-> {
            logger.error("Season with id: {} not found", id);
            return new ResourceNotFoundException(SeasonMessages.SEASON_NOT_FOUND);
        });
    }
}
