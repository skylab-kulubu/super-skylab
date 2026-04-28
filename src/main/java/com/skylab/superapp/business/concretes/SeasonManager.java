package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.SeasonService;
import com.skylab.superapp.core.constants.SeasonMessages;
import com.skylab.superapp.core.exceptions.*;
import com.skylab.superapp.core.mappers.SeasonMapper;
import com.skylab.superapp.core.utilities.security.SeasonSecurityUtils;
import com.skylab.superapp.dataAccess.SeasonDao;
import com.skylab.superapp.entities.DTOs.season.CreateSeasonRequest;
import com.skylab.superapp.entities.DTOs.season.SeasonDto;
import com.skylab.superapp.entities.DTOs.season.UpdateSeasonRequest;
import com.skylab.superapp.entities.Season;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeasonManager implements SeasonService {

    private final SeasonDao seasonDao;
    private final SeasonMapper seasonMapper;

    private final SeasonSecurityUtils seasonSecurityUtils;

  
    @Override
    @Transactional
    public SeasonDto addSeason(CreateSeasonRequest createSeasonRequest) {
        log.info("Adding new season with name: {}", createSeasonRequest.getName());

        seasonSecurityUtils.checkCreate();

        if(seasonDao.existsByName(createSeasonRequest.getName())) {
            log.error("Season with name: {} already exists", createSeasonRequest.getName());
            throw new SeasonNameAlreadyExistsException();
        }

        Season season = Season.builder()
                .name(createSeasonRequest.getName())
                .startDate(createSeasonRequest.getStartDate())
                .endDate(createSeasonRequest.getEndDate())
                .active(createSeasonRequest.isActive())
                .build();

        var savedSeason = seasonDao.save(season);

        log.info("Season with name: {} created successfully with id: {}", savedSeason.getName(), savedSeason.getId());

        return seasonMapper.toDto(savedSeason);
    }

    @Override
    public void deleteSeason(UUID id) {
        log.info("Deleting season with id: {}", id);

        seasonSecurityUtils.checkDelete();
        var season = getSeasonEntityById(id);
        log.info("Season with id: {} found, proceeding to delete", id);

        seasonDao.delete(season);

        log.info("Season with id: {} deleted successfully", id);
    }

    @Override
    @Transactional
    public SeasonDto updateSeason(UUID id, UpdateSeasonRequest updateSeasonRequest) {
        log.info("Updating season with id: {}", id);

        seasonSecurityUtils.checkUpdate();
        var season = getSeasonEntityById(id);

        log.info("Season with id: {} found, proceeding to update", id);


        var newStartDate = updateSeasonRequest.getStartDate() != null ? updateSeasonRequest.getStartDate() : season.getStartDate();
        var newEndDate = updateSeasonRequest.getEndDate() != null ? updateSeasonRequest.getEndDate() : season.getEndDate();

        if (newStartDate != null && newEndDate != null && newStartDate.isAfter(newEndDate)) {
            throw new SeasonStartDateCannotBeAfterEndDateException();
        }

        if (updateSeasonRequest.getStartDate() != null) season.setStartDate(updateSeasonRequest.getStartDate());
        if (updateSeasonRequest.getEndDate() != null) season.setEndDate(updateSeasonRequest.getEndDate());

        season.setActive(updateSeasonRequest.isActive());

        var savedSeason = seasonDao.save(season);
        log.info("Season with id: {} updated successfully", id);

        return seasonMapper.toDto(savedSeason);
    }

    @Override
    public List<SeasonDto> getAllSeasons() {
        log.info("Getting all seasons");

        var seasons = seasonDao.findAll();

        log.info("{} seasons found", seasons.size());

        return seasons.stream()
                .map(seasonMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public SeasonDto getSeasonByName(String name) {
        log.info("Getting season with name: {}", name);
        if(name == null || name.isEmpty()) {
            log.error("Season name cannot be null or empty");
            throw new ValidationException(SeasonMessages.SEASON_NAME_CANNOT_BE_NULL_OR_BLANK);
        }
        var result = seasonDao.findByName(name);
       if (result.isEmpty()){
           log.error("Season with name: {} not found", name);
           throw new ResourceNotFoundException(SeasonMessages.SEASON_NOT_FOUND);
       }

       log.info("Season with name: {} found, returning season", name);

        return seasonMapper.toDto(result.get());
    }

    @Override
    public SeasonDto getSeasonById(UUID id) {
        log.info("Getting season with id: {}", id);
        var season = getSeasonEntityById(id);

        log.info("Season with id: {} found, returning season", id);
        return seasonMapper.toDto(season);
    }


    @Override
    public List<SeasonDto> getActiveSeasons() {
        log.info("Getting all active seasons");

        var activeSeasons = seasonDao.findAllByActive(true);

        log.info("{} active seasons found", activeSeasons.size());

        return activeSeasons.stream()
                .map(seasonMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Season getSeasonEntityById(UUID id) {
        log.info("Getting season entity with id: {}", id);
        return seasonDao.findById(id).orElseThrow(()-> {
            log.error("Season with id: {} not found", id);
            return new ResourceNotFoundException(SeasonMessages.SEASON_NOT_FOUND);
        });
    }
}
