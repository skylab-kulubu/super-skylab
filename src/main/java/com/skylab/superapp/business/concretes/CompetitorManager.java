package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.CompetitorService;
import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.results.*;
import com.skylab.superapp.dataAccess.CompetitorDao;
import com.skylab.superapp.dataAccess.UserDao;
import com.skylab.superapp.entities.Competitor;
import com.skylab.superapp.entities.DTOs.Competitor.GetCompetitorDto;
import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CompetitorManager implements CompetitorService {

    private final CompetitorDao competitorDao;
    private final UserService userService;
    private final EventService eventService;
    private final EventTypeService eventTypeService;

    @Autowired
    public CompetitorManager(CompetitorDao competitorDao, UserService userService,
                             @Lazy EventService eventService, @Lazy EventTypeService eventTypeService) {
        this.competitorDao = competitorDao;
        this.userService = userService;
        this.eventService = eventService;
        this.eventTypeService = eventTypeService;
    }

    @Override
    public DataResult<List<GetCompetitorDto>> getAllCompetitors() {
        List<Integer> userIds = competitorDao.findDistinctUserIds();
        if (userIds.isEmpty()) {
            return new ErrorDataResult<>(CompetitorMessages.CompetitorNotFound, HttpStatus.NOT_FOUND);
        }

        var userResult = userService.getAllUserByIds(userIds);
        if (!userResult.isSuccess()) {
            return new ErrorDataResult<>(userResult.getMessage(), userResult.getHttpStatus());
        }

        List<GetCompetitorDto> returnCompetitors = userResult.getData().stream()
                .map(user -> {
                    double totalPoints = competitorDao.getTotalPointsByUserId(user.getId());
                    int competitionCount = competitorDao.getTotalCompetitionCountByUserId(user.getId());
                    return GetCompetitorDto.builder()
                            .id(user.getId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .totalPoints(totalPoints != 0 ? totalPoints : 0.0)
                            .competitionCount(competitionCount)
                            .build();
                })
                .collect(Collectors.toList());
        return new SuccessDataResult<>(returnCompetitors, CompetitorMessages.CompetitorListedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetCompetitorDto>> getAllCompetitorsByEventType(String eventTypeName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!userService.tenantCheck(eventTypeName, authentication.getName())) {
            return new ErrorDataResult<>(CompetitorMessages.TenantCheckFailed, HttpStatus.UNAUTHORIZED);
        }

        List<Competitor> competitors = competitorDao.findByEventTypeName(eventTypeName);
        if (competitors.isEmpty()) {
            return new ErrorDataResult<>(CompetitorMessages.CompetitorNotFound, HttpStatus.NOT_FOUND);
        }

        var eventTypeResult = eventTypeService.getEventTypeByName(eventTypeName);
        if (!eventTypeResult.isSuccess()) {
            return new ErrorDataResult<>(eventTypeResult.getMessage(), eventTypeResult.getHttpStatus());
        }

        List<User> users = competitors.stream()
                .map(Competitor::getUser)
                .distinct()
                .collect(Collectors.toList());
        List<GetCompetitorDto> returnCompetitors = users.stream()
                .map(user -> {
                    double totalPoints = competitorDao.getTotalPointsByUserIdAndEventTypeId(user.getId(), eventTypeResult.getData().getId());
                    int competitionCount = competitorDao.getTotalCompetitionCountByUserIdAndEventTypeId(user.getId(), eventTypeResult.getData().getId());
                    return GetCompetitorDto.builder()
                            .id(user.getId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .totalPoints(totalPoints != 0 ? totalPoints : 0.0)
                            .competitionCount(competitionCount)
                            .build();
                })
                .collect(Collectors.toList());
        return new SuccessDataResult<>(returnCompetitors, CompetitorMessages.CompetitorListedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<User> getCompetitorEntityById(int id) {
        var userResult = userService.getUserEntityById(id);

        if (!userResult.isSuccess()) {
            return new ErrorDataResult<>(userResult.getMessage(), userResult.getHttpStatus());
        }

        if (!competitorDao.existsByUserId(id)) {
            return new ErrorDataResult<>(CompetitorMessages.CompetitorNotFound, HttpStatus.NOT_FOUND);
        }
        return new SuccessDataResult<>(userResult.getData(), CompetitorMessages.CompetitorListedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetCompetitorDto>> getAllBySeasonId(int seasonId) {
        List<Competitor> competitors = competitorDao.findBySeasonId(seasonId);
        if (competitors.isEmpty()) {
            return new ErrorDataResult<>(CompetitorMessages.CompetitorNotFound, HttpStatus.NOT_FOUND);
        }

        List<GetCompetitorDto> returnCompetitors = competitors.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getUser().getId(),
                        Collectors.summarizingDouble(Competitor::getPoints)
                ))
                .entrySet().stream()
                .map(entry -> {
                    var userDataResult = userService.getUserEntityById(entry.getKey());
                    if (!userDataResult.isSuccess()) {
                        return null;
                    }
                    User user = userDataResult.getData();
                    double seasonPoints = entry.getValue().getSum();
                    int seasonCompetitionCount = (int) entry.getValue().getCount();
                    return GetCompetitorDto.builder()
                            .id(user.getId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .totalPoints(seasonPoints)
                            .competitionCount(seasonCompetitionCount)
                            .build();
                })
                .filter(dto -> dto != null)
                .sorted(Comparator.comparingDouble(GetCompetitorDto::getTotalPoints).reversed())
                .collect(Collectors.toList());

        return new SuccessDataResult<>(returnCompetitors, CompetitorMessages.CompetitorListedSuccess, HttpStatus.OK);
    }

    @Override
    public Result addPointsToUser(int userId, int eventId, double points, boolean isWinner, String award) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var userResult = userService.getUserEntityById(userId);
        if (!userResult.isSuccess()) {
            return new ErrorResult(userResult.getMessage(), userResult.getHttpStatus());
        }

        DataResult<Event> eventResult = eventService.getEventEntityById(eventId);
        if (!eventResult.isSuccess()) {
            return new ErrorResult(eventResult.getMessage(), eventResult.getHttpStatus());
        }
        Event event = eventResult.getData();

        if (!userService.tenantCheck(event.getType().getName(), username)) {
            return new ErrorResult(CompetitorMessages.TenantCheckFailed, HttpStatus.UNAUTHORIZED);
        }

        if (competitorDao.existsByUserIdAndEventId(userId, eventId)) {
            return new ErrorResult(CompetitorMessages.competitorEventResultAlreadyExists, HttpStatus.BAD_REQUEST);
        }

        Competitor competitor = Competitor.builder()
                .user(userResult.getData())
                .event(event)
                .season(event.getSeason())
                .eventType(event.getType())
                .points(points)
                .isWinner(isWinner)
                .award(award)
                .build();

        competitorDao.save(competitor);
        return new SuccessResult(CompetitorMessages.CompetitorPointsAddedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<User> getWeeklyWinner(int eventId) {
        var eventResult = competitorDao.findByEventIdAndIsWinnerTrue(eventId);

        if (!eventResult.isPresent()) {
            return new ErrorDataResult<>(CompetitorMessages.WeeklyWinnerNotFound, HttpStatus.NOT_FOUND);
        }

        var user = eventResult.get().getUser();

        return new SuccessDataResult<>(user, CompetitorMessages.WeeklyWinnerFound, HttpStatus.OK);
    }

    @Override
    public DataResult<User> getSeasonWinner(int seasonId) {
        List<Competitor> competitors = competitorDao.findBySeasonId(seasonId);
        if (competitors.isEmpty()) {
            return new ErrorDataResult<>("Sezon için yarışmacı bulunamadı", HttpStatus.NOT_FOUND);
        }

        User winner = competitors.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getUser().getId(),
                        Collectors.summingDouble(Competitor::getPoints)
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> userService.getUserEntityById(entry.getKey()).getData())
                .orElse(null);

        if (winner == null) {
            return new ErrorDataResult<>("Sezon kazananı belirlenemedi", HttpStatus.NOT_FOUND);
        }
        return new SuccessDataResult<>(winner, "Sezon kazananı bulundu", HttpStatus.OK);
    }

}