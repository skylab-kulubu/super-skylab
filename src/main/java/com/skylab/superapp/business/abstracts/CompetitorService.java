package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.Competitor;
import com.skylab.superapp.entities.DTOs.Competitor.CompetitorDto;
import com.skylab.superapp.entities.DTOs.Competitor.CreateCompetitorRequest;
import com.skylab.superapp.entities.DTOs.Competitor.LeaderboardDto;
import com.skylab.superapp.entities.DTOs.Competitor.UpdateCompetitorRequest;

import java.util.List;
import java.util.UUID;

public interface CompetitorService {

    CompetitorDto addCompetitor(CreateCompetitorRequest createCompetitorRequest);

    CompetitorDto updateCompetitor(UUID id, UpdateCompetitorRequest updateCompetitorRequest);

    void deleteCompetitor(UUID competitorId);

    CompetitorDto getCompetitorById(UUID id);

    List<CompetitorDto> getMyCompetitors();

    List<CompetitorDto> getAllCompetitors();

    List<CompetitorDto> getCompetitorsByEventId(UUID eventId);

    List<CompetitorDto> getCompetitorsByUserId(UUID userId);

    List<CompetitorDto> getCompetitorsByEventTypeId(UUID eventTypeId);

    List<LeaderboardDto> getLeaderboardByEventType(String eventTypeName);

    List<LeaderboardDto> getLeaderboardBySeasonAndEventType(UUID seasonId, String eventTypeName);

    CompetitorDto getEventWinner(UUID eventId);

    double getUserTotalPoints(UUID userId);

    boolean isUserParticipant(UUID userId, UUID eventId);

    Competitor getCompetitorEntityById(UUID id);
}