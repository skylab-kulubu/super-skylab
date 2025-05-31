package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.DTOs.Competitor.GetCompetitorDto;
import com.skylab.superapp.entities.DTOs.User.GetUserDto;
import com.skylab.superapp.entities.User;

import java.util.List;

public interface CompetitorService {
    DataResult<List<GetCompetitorDto>> getAllCompetitors();

    DataResult<List<GetCompetitorDto>> getAllCompetitorsByEventType(String eventTypeName);

    DataResult<User> getCompetitorEntityById(int id);

    DataResult<List<GetCompetitorDto>> getAllBySeasonId(int seasonId);

    Result addPointsToUser(int userId, int eventId, double points, boolean isWinner, String award);

    DataResult<User> getWeeklyWinner(int eventId);

    DataResult<User> getSeasonWinner(int seasonId);
}