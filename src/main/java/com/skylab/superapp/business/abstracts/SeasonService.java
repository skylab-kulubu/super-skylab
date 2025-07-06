package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.DTOs.Season.CreateSeasonDto;
import com.skylab.superapp.entities.DTOs.Season.GetSeasonDto;
import com.skylab.superapp.entities.DTOs.User.GetUserDto;
import com.skylab.superapp.entities.EventType;
import com.skylab.superapp.entities.Season;

import java.util.List;

public interface SeasonService {

    Season addSeason(CreateSeasonDto createSeasonDto);

    void deleteSeason(int id);

    List<Season> getAllSeasons();

    Season getSeasonByName(String name);

    Season getSeasonById(int id);

    List<Season> getActiveSeasons();

    void addEventToSeason(int seasonId, int eventId);

    void removeEventFromSeason(int seasonId, int eventId);

}
