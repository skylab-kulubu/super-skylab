package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.*;
import com.skylab.superapp.core.exceptions.CompetitorNotFoundException;
import com.skylab.superapp.core.exceptions.CompetitorNotParticipatingInEventException;
import com.skylab.superapp.core.mappers.CompetitorMapper;
import com.skylab.superapp.dataAccess.CompetitorDao;
import com.skylab.superapp.entities.Competitor;
import com.skylab.superapp.entities.DTOs.Competitor.CompetitorDto;
import com.skylab.superapp.entities.DTOs.Competitor.CreateCompetitorRequest;
import com.skylab.superapp.entities.DTOs.Competitor.UpdateCompetitorRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CompetitorManager implements CompetitorService {

    private final CompetitorDao competitorDao;
    private final UserService userService;
    private final EventService eventService;
    private final EventTypeService eventTypeService;
    private final CompetitionService competitionService;
    private final CompetitorMapper competitorMapper;

    @Autowired
    public CompetitorManager(CompetitorDao competitorDao,@Lazy UserService userService,
                             @Lazy EventService eventService, @Lazy EventTypeService eventTypeService,
                             @Lazy CompetitionService competitionService, CompetitorMapper competitorMapper) {
        this.competitorDao = competitorDao;
        this.userService = userService;
        this.eventService = eventService;
        this.eventTypeService = eventTypeService;
        this.competitionService = competitionService;
        this.competitorMapper = competitorMapper;
    }

    @Override
    public CompetitorDto addCompetitor(CreateCompetitorRequest createCompetitorRequest) {
        var user = userService.getUserEntityById(createCompetitorRequest.getUserId());
        var event = eventService.getEventEntityById(createCompetitorRequest.getEventId());

        if (!event.getType().isCompetitive()){
            throw new CompetitorNotParticipatingInEventException(); // CHANGE THIS EXCEPTION
        }

        if (competitorDao.existsByUserAndEvent(user, event)) {
            throw new CompetitorNotParticipatingInEventException(); // CHANGE THIS EXCEPTION
        }

        var competitor = Competitor.builder()
                .user(user)
                .event(event)
                .points(createCompetitorRequest.getPoints())
                .isWinner(createCompetitorRequest.isWinner())
                .build();

        return competitorMapper.toDto(competitorDao.save(competitor));
    }

    @Override
    public CompetitorDto updateCompetitor(UUID id, UpdateCompetitorRequest updateCompetitorRequest) {
      var competitor = getCompetitorEntityById(id);

        if (updateCompetitorRequest.getUserId() != null) {
            competitor.setUser(userService.getUserEntityById(updateCompetitorRequest.getUserId()));
        }


        if (updateCompetitorRequest.getEventId() != null) {
            var event = eventService.getEventEntityById(updateCompetitorRequest.getEventId());
            if (!event.getType().isCompetitive()) {
                throw new CompetitorNotParticipatingInEventException(); // CHANGE THIS EXCEPTION
            }
            competitor.setEvent(event);
        }

        if (updateCompetitorRequest.getPoints() != 0) {
            competitor.setPoints(updateCompetitorRequest.getPoints());
        }

        competitor.setWinner(updateCompetitorRequest.isWinner());

        return competitorMapper.toDto(competitorDao.save(competitor));

    }


    @Override
    public void deleteCompetitor(UUID competitorId) {
        Competitor competitor = getCompetitorEntity(competitorId);
        competitorDao.delete(competitor);
    }

    @Override
    public CompetitorDto getCompetitorById(UUID id, boolean includeUser, boolean includeEvent) {
       return competitorMapper.toDto(getCompetitorEntityById(id));
    }

    @Override
    public List<CompetitorDto> getMyCompetitors(boolean includeUser, boolean includeEvent, HttpServletRequest request) {
        var authenticatedUser = userService.getAuthenticatedUserEntity(request);
        var result = competitorDao.findCompetitorsByUser(authenticatedUser);
        return competitorMapper.toDtoList(result, includeUser, includeEvent);
    }


    @Override
    public List<CompetitorDto> getAllCompetitors(boolean includeUser, boolean includeEvent) {
        return competitorMapper.toDtoList(competitorDao.findAll(), includeUser, includeEvent);
    }

    @Override
    public List<CompetitorDto> getCompetitorsByEventId(UUID eventId, boolean includeUser, boolean includeEvent) {
       return competitorMapper.toDtoList(competitorDao.findByEventId(eventId), includeUser, includeEvent);
    }

    @Override
    public List<CompetitorDto> getCompetitorsByUserId(UUID userId, boolean includeUser, boolean includeEvent) {
        return competitorMapper.toDtoList(competitorDao.findByUserId(userId), includeUser, includeEvent);
    }


    @Override
    public List<CompetitorDto> getCompetitorsByCompetitionId(UUID competitionId, boolean includeUser, boolean includeEvent) {
        return competitorMapper.toDtoList(competitorDao.findBySeasonId(competitionId), includeUser, includeEvent);
    }

    @Override
    public List<CompetitorDto> getCompetitorsByEventTypeId(UUID eventTypeId, boolean includeUser, boolean includeEvent) {
        var eventType = eventTypeService.getEventTypeEntityById(eventTypeId);

        return competitorMapper.toDtoList(competitorDao.findAllByEventType(eventType), includeUser, includeEvent);
    }


    @Override
    public List<CompetitorDto> getLeaderboardByEventType(String eventTypeName, boolean includeUser, boolean includeEvent) {
        return competitorMapper.toDtoList(competitorDao.findLeaderboardByEventType(eventTypeName), includeUser, includeEvent);
    }


    @Override
    public List<CompetitorDto> getCompetitionLeaderboard(UUID competitionId, boolean includeUser, boolean includeEvent) {
        var competition = competitionService.getCompetitionEntityById(competitionId);

        return competitorMapper.toDtoList(competitorDao.findLeaderboardByCompetition(competition));

    }

    @Override
    public CompetitorDto getEventWinner(UUID eventId, boolean includeUser, boolean includeEvent) {
        var event = eventService.getEventEntityById(eventId);

        return competitorMapper.toDto(competitorDao.findWinnerOfEvent(event), includeUser, includeEvent);

    }

    @Override
    public double getUserTotalPoints(UUID userId) {
        var user = userService.getUserEntityById(userId);
        var competitors = competitorDao.findCompetitorsByUser(user);

        var totalPoints = competitors.stream()
                .mapToDouble(Competitor::getPoints)
                .sum();

        return totalPoints;
    }

    @Override
    public double getUsersTotalPointsInCompetition(UUID userId, UUID competitionId) {
        var user = userService.getUserEntityById(userId);
        var competition = competitionService.getCompetitionEntityById(competitionId);

        return competitorDao.findUsersTotalPointsInCompetition(user, competition);

    }

    @Override
    public int getUserCompetitionCount(UUID userId) {
        var user = userService.getUserEntityById(userId);
        return competitorDao.getTotalCompetitionCountByUserId(user);
    }


    @Override
    public boolean isUserParticipant(UUID userId, UUID eventId) {
        var user = userService.getUserEntityById(userId);
        var event = eventService.getEventEntityById(eventId);

        return competitorDao.existsByUserAndEvent(user, event);
    }

    @Override
    public Competitor getCompetitorEntityById(UUID id) {
        return competitorDao.findById(id)
                .orElseThrow(CompetitorNotFoundException::new);
    }


    private Competitor getCompetitorEntity(UUID competitorId) {
        return competitorDao.findById(competitorId)
                .orElseThrow(CompetitorNotFoundException::new);
    }

}