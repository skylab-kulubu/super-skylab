package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.*;
import com.skylab.superapp.core.constants.CompetitorMessages;
import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.core.exceptions.BusinessException;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.mappers.CompetitorMapper;
import com.skylab.superapp.core.utilities.security.EventSecurityUtils;
import com.skylab.superapp.dataAccess.CompetitorDao;
import com.skylab.superapp.entities.Competitor;
import com.skylab.superapp.entities.DTOs.Competitor.*;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CompetitorManager implements CompetitorService {

    private final CompetitorDao competitorDao;
    private final UserService userService;
    private final EventService eventService;
    private final EventTypeService eventTypeService;
    private final CompetitorMapper competitorMapper;
    private final EventSecurityUtils eventSecurityUtils;

    public CompetitorManager(CompetitorDao competitorDao, UserService userService, EventService eventService, EventTypeService eventTypeService, CompetitorMapper competitorMapper, EventSecurityUtils eventSecurityUtils) {
        this.competitorDao = competitorDao;
        this.userService = userService;
        this.eventService = eventService;
        this.eventTypeService = eventTypeService;
        this.competitorMapper = competitorMapper;
        this.eventSecurityUtils = eventSecurityUtils;
    }

    @Override
    @Transactional
    public CompetitorDto addCompetitor(CreateCompetitorRequest request) {
        log.info("Attempting to add competitor: user {} for event {}", request.getUserId(), request.getEventId());
        var user = userService.getUserEntityById(request.getUserId());
        var event = eventService.getEventEntityById(request.getEventId());
        var currentUser = userService.getAuthenticatedUser();

        boolean isSelfRegistration = currentUser.getId().equals(user.getId());
        if (!isSelfRegistration) {
            log.debug("Registration is for another user. Verifying authorization...");
            eventSecurityUtils.checkAuthorization(event.getType());
        }

        if (competitorDao.existsByUserAndEvent(user, event)) {
            log.warn("Competitor mapping already exists for user {} and event {}", request.getUserId(), request.getEventId());
            throw new BusinessException(CompetitorMessages.COMPETITOR_ALREADY_IN_COMPETITION);
        }
        if (!event.isActive()) {
            log.warn("Cannot register competitor: Event {} is not active", request.getEventId());
            throw new BusinessException(EventMessages.EVENT_NOT_ACTIVE);
        }

        var competitor = Competitor.builder()
                .user(user)
                .event(event)
                .score(isSelfRegistration ? null : request.getPoints())
                .isWinner(request.isWinner())
                .build();

        log.info("Competitor successfully added: {}", competitor.getId());
        return convertToDto(competitorDao.save(competitor));
    }

    @Override
    @Transactional
    public CompetitorDto updateCompetitor(UUID id, UpdateCompetitorRequest request) {
        log.info("Attempting to update competitor with id: {}", id);
        var competitor = getCompetitorEntityById(id);

        eventSecurityUtils.checkAuthorization(competitor.getEvent().getType());

        if (request.getUserId() != null) {
            competitor.setUser(userService.getUserEntityById(request.getUserId()));
        }
        if (request.getEventId() != null) {
            competitor.setEvent(eventService.getEventEntityById(request.getEventId()));
        }
        if (request.getPoints() != 0) {
            competitor.setScore(request.getPoints());
        }
        competitor.setWinner(request.isWinner());

        log.info("Competitor successfully updated: {}", id);
        return convertToDto(competitorDao.save(competitor));
    }

    @Override
    @Transactional
    public void deleteCompetitor(UUID competitorId) {
        log.info("Attempting to delete competitor with id: {}", competitorId);
        Competitor competitor = getCompetitorEntityById(competitorId);
        var currentUser = userService.getAuthenticatedUser();

        if (!currentUser.getId().equals(competitor.getUser().getId())) {
            log.debug("User is attempting to delete another competitor's record. Verifying authorization...");
            eventSecurityUtils.checkAuthorization(competitor.getEvent().getType());
        }
        competitorDao.delete(competitor);
        log.info("Competitor deleted successfully: {}", competitorId);
    }

    @Override
    public CompetitorDto getCompetitorById(UUID id) {
        return convertToDto(getCompetitorEntityById(id));
    }

    @Override
    public List<CompetitorDto> getMyCompetitors() {
        return convertToDtoList(competitorDao.findCompetitorsByUser(userService.getAuthenticatedUserEntity()));
    }

    @Override
    public List<CompetitorDto> getAllCompetitors() {
        return convertToDtoList(competitorDao.findAll());
    }

    @Override
    public List<CompetitorDto> getCompetitorsByEventId(UUID eventId) {
        return convertToDtoList(competitorDao.findByEventId(eventId));
    }

    @Override
    public List<CompetitorDto> getCompetitorsByUserId(UUID userId) {
        return convertToDtoList(competitorDao.findByUserId(userId));
    }

    @Override
    public List<CompetitorDto> getCompetitorsByEventTypeId(UUID eventTypeId) {
        return convertToDtoList(competitorDao.findAllByEventType(eventTypeService.getEventTypeEntityById(eventTypeId)));
    }

    @Override
    public List<LeaderboardDto> getLeaderboardByEventType(String eventTypeName) {
        log.info("Fetching leaderboard for event type: {}", eventTypeName);
        List<LeaderboardScoreDto> scores = competitorDao.getLeaderboardScoresByEventType(eventTypeName);
        return processLeaderboard(scores);
    }

    @Override
    public List<LeaderboardDto> getLeaderboardBySeasonAndEventType(UUID seasonId, String eventTypeName) {
        log.info("Fetching leaderboard for season: {} and event type: {}", seasonId, eventTypeName);
        List<LeaderboardScoreDto> scores = competitorDao.getLeaderboardScoresBySeasonAndEventType(eventTypeName, seasonId);
        return processLeaderboard(scores);
    }

    private List<LeaderboardDto> processLeaderboard(List<LeaderboardScoreDto> scores) {
        List<LeaderboardDto> leaderboard = scores.stream().map(score ->
                new LeaderboardDto(userService.getUserById(score.getUserId()), score.getTotalScore(), score.getEventCount(), 0)
        ).collect(Collectors.toList());

        assignRanks(leaderboard);
        return leaderboard;
    }

    @Override
    public CompetitorDto getEventWinner(UUID eventId) {
        return convertToDto(competitorDao.findWinnerOfEvent(eventService.getEventEntityById(eventId)));
    }

    @Override
    public double getUserTotalPoints(UUID userId) {
        return competitorDao.findCompetitorsByUser(userService.getUserEntityById(userId)).stream()
                .map(Competitor::getScore)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    @Override
    public boolean isUserParticipant(UUID userId, UUID eventId) {
        return competitorDao.existsByUserAndEvent(
                userService.getUserEntityById(userId),
                eventService.getEventEntityById(eventId)
        );
    }

    @Override
    public Competitor getCompetitorEntityById(UUID id) {
        return competitorDao.findById(id)
                .orElseThrow(() -> {
                    log.error("Competitor not found with id: {}", id);
                    return new ResourceNotFoundException(CompetitorMessages.COMPETITOR_NOT_FOUND);
                });
    }

    private void assignRanks(List<LeaderboardDto> leaderboard) {
        if (leaderboard.isEmpty()) return;

        int currentRank = 1;
        leaderboard.get(0).setRank(currentRank);

        for (int i = 1; i < leaderboard.size(); i++) {
            if (leaderboard.get(i).getTotalScore() == leaderboard.get(i - 1).getTotalScore()) {
                leaderboard.get(i).setRank(currentRank);
            } else {
                currentRank = i + 1;
                leaderboard.get(i).setRank(currentRank);
            }
        }
    }

    private List<CompetitorDto> convertToDtoList(List<Competitor> competitors) {
        if (competitors.isEmpty()) return List.of();

        List<UUID> userIds = competitors.stream()
                .map(competitor -> competitor.getUser().getId())
                .distinct()
                .toList();

        Map<UUID, UserDto> userDtoMap = userService.getAllUsersByIds(userIds).stream()
                .collect(Collectors.toMap(UserDto::getId, userDto -> userDto));

        return competitors.stream().map(competitor -> {
            CompetitorDto dto = competitorMapper.toDtoWithEvent(competitor);
            if (competitor.getUser() != null) {
                dto.setUser(userDtoMap.get(competitor.getUser().getId()));
            }
            return dto;
        }).collect(Collectors.toList());
    }

    private CompetitorDto convertToDto(Competitor competitor) {
        UserDto userDto = competitor.getUser() != null
                ? userService.getUserById(competitor.getUser().getId())
                : null;
        return competitorMapper.toDto(competitor);
    }
}