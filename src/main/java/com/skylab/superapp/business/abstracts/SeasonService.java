package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.season.CreateSeasonRequest;
import com.skylab.superapp.entities.DTOs.season.SeasonDto;
import com.skylab.superapp.entities.DTOs.season.UpdateSeasonRequest;
import com.skylab.superapp.entities.Season;

import java.util.List;
import java.util.UUID;

public interface SeasonService {

    SeasonDto addSeason(CreateSeasonRequest createSeasonRequest);

    void deleteSeason(UUID id);

    SeasonDto updateSeason(UUID id, UpdateSeasonRequest updateSeasonRequest);

    List<SeasonDto> getAllSeasons(boolean includeEvents);

    SeasonDto getSeasonByName(String name, boolean includeEvents);

    SeasonDto getSeasonById(UUID id, boolean includeEvents);

    List<SeasonDto> getActiveSeasons(boolean includeEvents);

    void addEventToSeason(UUID seasonId, UUID eventId);

    void removeEventFromSeason(UUID seasonId, UUID eventId);

    Season getSeasonEntityById(UUID id);

}
