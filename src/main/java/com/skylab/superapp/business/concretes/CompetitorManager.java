package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.*;
import com.skylab.superapp.core.exceptions.CompetitorNotFoundException;
import com.skylab.superapp.core.exceptions.CompetitorNotParticipatingInEventException;
import com.skylab.superapp.dataAccess.CompetitorDao;
import com.skylab.superapp.entities.Competitor;
import com.skylab.superapp.entities.DTOs.Competitor.CreateCompetitorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompetitorManager implements CompetitorService {

    private final CompetitorDao competitorDao;
    private final UserService userService;
    private final EventService eventService;
    private final EventTypeService eventTypeService;
    private final CompetitionService competitionService;

    @Autowired
    public CompetitorManager(CompetitorDao competitorDao,@Lazy UserService userService,
                             @Lazy EventService eventService, @Lazy EventTypeService eventTypeService,
                             @Lazy CompetitionService competitionService) {
        this.competitorDao = competitorDao;
        this.userService = userService;
        this.eventService = eventService;
        this.eventTypeService = eventTypeService;
        this.competitionService = competitionService;
    }

    @Override
    public Competitor addCompetitor(CreateCompetitorDto competitorDto) {
        var user = userService.getUserById(competitorDto.getUserId());
        var event = eventService.getEventById(competitorDto.getEventId());

        if (!event.getType().isCompetitive()){
            throw new CompetitorNotParticipatingInEventException(); // CHANGE THİS EXCEPTİON
        }

        if (competitorDao.existsByUserAndEvent(user, event)) {
            throw new CompetitorNotParticipatingInEventException(); // CHANGE THİS EXCEPTİON
        }

        var competitor = Competitor.builder()
                .user(user)
                .event(event)
                .points(0)
                .isWinner(false)
                .build();

        return competitorDao.save(competitor);
    }

    @Override
    public void setCompetitorPoints(int competitorId, double points) {
        //if competitor doesn't exists getCompetitorEntity method will throw CompetitorNotFoundException
        var competitor = getCompetitorEntity(competitorId);
        competitor.setPoints(points);
        competitorDao.save(competitor);
    }

    @Override
    public void setCompetitorWinner(int competitorId, boolean isWinner) {
        var competitor = getCompetitorEntity(competitorId);
        competitor.setWinner(isWinner);
        competitorDao.save(competitor);
    }

    @Override
    public void deleteCompetitor(int competitorId) {
        Competitor competitor = getCompetitorEntity(competitorId);
        competitorDao.delete(competitor);
    }

    @Override
    public Competitor getCompetitorById(int id) {
       var competitor = getCompetitorEntity(id);
       return competitor;
    }

    @Override
    public List<Competitor> getMyCompetitors() {
        var authenticatedUser = userService.getAuthenticatedUser();
        return competitorDao.findCompetitorsByUser(authenticatedUser);
    }


    @Override
    public List<Competitor> getAllCompetitors() {
        return competitorDao.findAll();
    }

    @Override
    public List<Competitor> getCompetitorsByEventId(int eventId) {
       return competitorDao.findByEventId(eventId);
    }

    @Override
    public List<Competitor> getCompetitorsByUserId(int userId) {
        return competitorDao.findByUserId(userId);
    }


    @Override
    public List<Competitor> getCompetitorsByCompetitionId(int competitionId) {
        return competitorDao.findBySeasonId(competitionId);
    }

    @Override
    public List<Competitor> getCompetitorsByEventTypeId(int eventTypeId) {
        var eventType = eventTypeService.getEventTypeById(eventTypeId);

        return competitorDao.findAllByEventType(eventType);
    }


    @Override
    public List<Competitor> getLeaderboardByEventType(String eventTypeName) {
        return competitorDao.findLeaderboardByEventType(eventTypeName);
    }


    @Override
    public List<Competitor> getCompetitionLeaderboard(int competitionId) {
        var competition = competitionService.getCompetitionById(competitionId);

        return competitorDao.findLeaderboardByCompetition(competition);

    }

    @Override
    public Competitor getEventWinner(int eventId) {
        var event = eventService.getEventById(eventId);

        return competitorDao.findWinnerOfEvent(event);

    }

    @Override
    public void setEventWinner(int eventId, int competitorId) {
        var event = eventService.getEventById(eventId);
        var competitor = getCompetitorEntity(competitorId);

        if (!competitor.getEvent().equals(event)) {
            throw new CompetitorNotParticipatingInEventException();
        }

        competitor.setWinner(true);
        competitorDao.save(competitor);
    }

    @Override
    public double getUserTotalPoints(int userId) {
        var user = userService.getUserById(userId);
        var competitors = competitorDao.findCompetitorsByUser(user);

        var totalPoints = competitors.stream()
                .mapToDouble(Competitor::getPoints)
                .sum();

        return totalPoints;
    }

    @Override
    public double getUsersTotalPointsInCompetition(int userId, int competitionId) {
        var user = userService.getUserById(userId);
        var competition = competitionService.getCompetitionById(competitionId);

        return competitorDao.findUsersTotalPointsInCompetition(user, competition);

    }

    @Override
    public int getUserCompetitionCount(int userId) {
        var user = userService.getUserById(userId);
        return competitorDao.getTotalCompetitionCountByUserId(user);
    }


    @Override
    public boolean isUserParticipant(int userId, int eventId) {
        var user = userService.getUserById(userId);
        var event = eventService.getEventById(eventId);

        return competitorDao.existsByUserAndEvent(user, event);
    }



    private Competitor getCompetitorEntity(int competitorId) {
        return competitorDao.findById(competitorId)
                .orElseThrow(CompetitorNotFoundException::new);
    }

}