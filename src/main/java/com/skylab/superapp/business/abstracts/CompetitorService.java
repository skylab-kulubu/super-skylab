package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.Competitor;
import com.skylab.superapp.entities.DTOs.Competitor.CompetitorDto;
import com.skylab.superapp.entities.DTOs.Competitor.CreateCompetitorRequest;
import com.skylab.superapp.entities.DTOs.Competitor.UpdateCompetitorRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

public interface CompetitorService {

    CompetitorDto addCompetitor(CreateCompetitorRequest createCompetitorRequest);
    CompetitorDto updateCompetitor(UUID id, UpdateCompetitorRequest updateCompetitorRequest);
    void deleteCompetitor(UUID competitorId);
    CompetitorDto getCompetitorById(UUID id, boolean includeUser, boolean includeEvent);

    List<CompetitorDto> getMyCompetitors(boolean includeUser, boolean includeEvent);


    List<CompetitorDto> getAllCompetitors(boolean includeUser, boolean includeEvent);
    List<CompetitorDto> getCompetitorsByEventId(UUID eventId, boolean includeUser, boolean includeEvent);
    List<CompetitorDto> getCompetitorsByUserId(UUID userId, boolean includeUser, boolean includeEvent);
    List<CompetitorDto> getCompetitorsByEventTypeId(UUID eventTypeId, boolean includeUser, boolean includeEvent);

    List<CompetitorDto> getLeaderboardByEventType(String eventTypeName, boolean includeUser, boolean includeEvent);

    CompetitorDto getEventWinner(UUID eventId, boolean includeUser, boolean includeEvent);

    double getUserTotalPoints(UUID userId);

    boolean isUserParticipant(UUID userId, UUID eventId);

    Competitor getCompetitorEntityById(UUID id);


    List<CompetitorDto> getLeaderboardBySeasonAndEventType(UUID seasonId, String eventTypeName);
}