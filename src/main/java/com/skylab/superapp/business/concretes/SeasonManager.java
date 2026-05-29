package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.SeasonService;
import com.skylab.superapp.core.constants.SeasonMessages;
import com.skylab.superapp.core.exceptions.BusinessException;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.exceptions.ValidationException;
import com.skylab.superapp.core.mappers.SeasonMapper;
import com.skylab.superapp.core.security.authz.Authorize;
import com.skylab.superapp.dataAccess.SeasonDao;
import com.skylab.superapp.entities.DTOs.season.CreateSeasonRequest;
import com.skylab.superapp.entities.DTOs.season.PatchSeasonRequest;
import com.skylab.superapp.entities.DTOs.season.SeasonDto;
import com.skylab.superapp.entities.DTOs.season.UpdateSeasonRequest;
import com.skylab.superapp.entities.Season;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeasonManager implements SeasonService {

    private final SeasonDao seasonDao;
    private final SeasonMapper seasonMapper;

    @Override
    @Transactional
    @Authorize(resource = "SEASON", action = "CREATE")
    public SeasonDto addSeason(CreateSeasonRequest createSeasonRequest) {
        log.info("Initiating season creation. SeasonName: {}", createSeasonRequest.getName());

        if (seasonDao.existsByName(createSeasonRequest.getName())) {
            log.warn("Season creation failed: Name already exists. SeasonName: {}", createSeasonRequest.getName());
            throw new BusinessException(SeasonMessages.SEASON_NAME_ALREADY_EXISTS);
        }

        Season season = Season.builder()
                .name(createSeasonRequest.getName())
                .startDate(createSeasonRequest.getStartDate())
                .endDate(createSeasonRequest.getEndDate())
                .active(createSeasonRequest.isActive())
                .build();

        var savedSeason = seasonDao.save(season);

        log.info("Season created successfully. SeasonId: {}, SeasonName: {}", savedSeason.getId(), savedSeason.getName());

        return seasonMapper.toDto(savedSeason);
    }

    @Override
    @Authorize(resource = "SEASON", action = "DELETE")
    public void deleteSeason(UUID id) {
        log.info("Initiating season deletion. SeasonId: {}", id);

        var season = getSeasonEntityById(id);

        if (season.getEvents() != null && !season.getEvents().isEmpty()) {
            log.warn("Season deletion failed: Has related events. SeasonId: {}", id);
            throw new BusinessException(SeasonMessages.SEASON_HAS_RELATED_EVENTS);
        }

        seasonDao.delete(season);

        log.info("Season deleted successfully. SeasonId: {}", id);
    }

    @Override
    @Transactional
    @Authorize(resource = "SEASON", action = "UPDATE")
    public SeasonDto updateSeason(UUID id, UpdateSeasonRequest updateSeasonRequest) {
        log.info("Initiating season replace (PUT). SeasonId: {}", id);

        var season = getSeasonEntityById(id);

        validateDateRange(id, updateSeasonRequest.getStartDate(), updateSeasonRequest.getEndDate());
        applyName(id, season, updateSeasonRequest.getName());
        season.setStartDate(updateSeasonRequest.getStartDate());
        season.setEndDate(updateSeasonRequest.getEndDate());
        season.setActive(updateSeasonRequest.isActive());

        var savedSeason = seasonDao.save(season);
        log.info("Season replaced successfully. SeasonId: {}", id);

        return seasonMapper.toDto(savedSeason);
    }

    @Override
    @Transactional
    @Authorize(resource = "SEASON", action = "UPDATE")
    public SeasonDto patchSeason(UUID id, PatchSeasonRequest request) {
        log.info("Initiating season patch (PATCH). SeasonId: {}", id);

        var season = getSeasonEntityById(id);

        var newStartDate = request.getStartDate() != null ? request.getStartDate() : season.getStartDate();
        var newEndDate = request.getEndDate() != null ? request.getEndDate() : season.getEndDate();
        validateDateRange(id, newStartDate, newEndDate);

        if (request.getName() != null) applyName(id, season, request.getName());
        if (request.getStartDate() != null) season.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) season.setEndDate(request.getEndDate());
        if (request.getActive() != null) season.setActive(request.getActive());

        var savedSeason = seasonDao.save(season);
        log.info("Season patched successfully. SeasonId: {}", id);

        return seasonMapper.toDto(savedSeason);
    }

    private void validateDateRange(UUID id, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            log.warn("Season update failed: Start date is after end date. SeasonId: {}", id);
            throw new ValidationException(SeasonMessages.START_DATE_CANNOT_BE_AFTER_END_DATE);
        }
    }

    private void applyName(UUID id, Season season, String name) {
        if (name.equals(season.getName())) {
            return;
        }
        if (seasonDao.existsByName(name)) {
            log.warn("Season update failed: Name already exists. SeasonId: {}, RequestedName: {}", id, name);
            throw new BusinessException(SeasonMessages.SEASON_NAME_ALREADY_EXISTS);
        }
        season.setName(name);
    }

    @Override
    public List<SeasonDto> getAllSeasons() {
        log.debug("Retrieving all seasons.");

        var seasons = seasonDao.findAll();

        log.info("Retrieved all seasons successfully. TotalCount: {}", seasons.size());

        return seasons.stream()
                .map(seasonMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SeasonDto getSeasonByName(String name) {
        log.debug("Retrieving season by name. SeasonName: {}", name);

        if (name == null || name.isEmpty()) {
            log.warn("Season retrieval failed: Name is null or blank.");
            throw new ValidationException(SeasonMessages.SEASON_NAME_CANNOT_BE_NULL_OR_BLANK);
        }

        var result = seasonDao.findByName(name);
        if (result.isEmpty()) {
            log.error("Season retrieval failed: Resource not found. SeasonName: {}", name);
            throw new ResourceNotFoundException(SeasonMessages.SEASON_NOT_FOUND);
        }

        return seasonMapper.toDto(result.get());
    }

    @Override
    public SeasonDto getSeasonById(UUID id) {
        log.debug("Retrieving season. SeasonId: {}", id);
        return seasonMapper.toDto(getSeasonEntityById(id));
    }

    @Override
    public List<SeasonDto> getActiveSeasons() {
        log.debug("Retrieving active seasons.");

        var activeSeasons = seasonDao.findAllByActive(true);

        log.info("Retrieved active seasons successfully. TotalCount: {}", activeSeasons.size());

        return activeSeasons.stream()
                .map(seasonMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Season getSeasonEntityById(UUID id) {
        log.debug("Retrieving season entity. SeasonId: {}", id);

        return seasonDao.findById(id).orElseThrow(() -> {
            log.error("Season entity retrieval failed: Resource not found. SeasonId: {}", id);
            return new ResourceNotFoundException(SeasonMessages.SEASON_NOT_FOUND);
        });
    }
}