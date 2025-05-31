package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.DTOs.Season.CreateSeasonDto;
import com.skylab.superapp.entities.DTOs.Season.GetSeasonDto;
import com.skylab.superapp.entities.DTOs.User.GetUserDto;
import com.skylab.superapp.entities.Season;

import java.util.List;

public interface SeasonService {

    DataResult<Integer> addSeason(CreateSeasonDto createSeasonDto);

    Result deleteSeason(int id);

    DataResult<List<GetSeasonDto>> getAllSeasonsByTenant(String tenant);

    DataResult<List<GetSeasonDto>> getAllSeasons();

    DataResult<GetSeasonDto> getSeasonByName(String name);

    DataResult<Season> getSeasonEntityById(int id);

    DataResult<GetSeasonDto> getSeasonById(int id);

    Result addEventToSeason(int seasonId, int eventId);

    Result removeEventFromSeason(int seasonId, int eventId);

}
