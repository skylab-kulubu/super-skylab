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

    List<SeasonDto> getAllSeasons();

    SeasonDto getSeasonByName(String name);

    SeasonDto getSeasonById(UUID id);

    List<SeasonDto> getActiveSeasons();

    Season getSeasonEntityById(UUID id);
}