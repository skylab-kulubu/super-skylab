package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.Competitor;
import com.skylab.superapp.entities.DTOs.Competitor.CreateCompetitorDto;
import com.skylab.superapp.entities.DTOs.Competitor.GetCompetitorDto;
import com.skylab.superapp.entities.User;

import java.util.List;

public interface CompetitorService {

    Competitor addCompetitor(CreateCompetitorDto createCompetitorDto);
    void setCompetitorPoints(int competitorId, double points);
    void setCompetitorWinner(int competitorId, boolean isWinner);
    void deleteCompetitor(int competitorId);
    Competitor getCompetitorById(int id);
    List<Competitor> getMyCompetitors();


    List<Competitor> getAllCompetitors();
    List<Competitor> getCompetitorsByEventId(int eventId);
    List<Competitor> getCompetitorsByUserId(int userId);
    List<Competitor> getCompetitorsByCompetitionId(int seasonId);
    List<Competitor> getCompetitorsByEventTypeId(int eventTypeId);

    List<Competitor> getLeaderboardByEventType(String eventTypeName);
    List<Competitor> getCompetitionLeaderboard(int competitionId);

    Competitor getEventWinner(int eventId);
    void setEventWinner(int eventId, int competitorId);

    double getUserTotalPoints(int userId);
    double getUsersTotalPointsInCompetition(int userId, int competitionId);
    int getUserCompetitionCount(int userId);

    boolean isUserParticipant(int userId, int eventId);


}