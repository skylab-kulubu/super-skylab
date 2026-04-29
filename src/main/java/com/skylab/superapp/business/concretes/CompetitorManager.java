package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.*;
import com.skylab.superapp.core.constants.CompetitorMessages;
import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.core.exceptions.BusinessException;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.mappers.CompetitorMapper;
import com.skylab.superapp.core.utilities.security.CompetitorSecurityUtils;
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
@RequiredArgsConstructor
public class CompetitorManager implements CompetitorService {

    private final CompetitorDao competitorDao;
    private final UserService userService;
    private final EventService eventService;
    private final EventTypeService eventTypeService;
    private final CompetitorMapper competitorMapper;
    private final CompetitorSecurityUtils competitorSecurityUtils;

    @Override
    @Transactional
    public CompetitorDto addCompetitor(CreateCompetitorRequest request) {
        log.info("Initiating competitor registration. UserId: {}, EventId: {}", request.getUserId(), request.getEventId());

        var user = userService.getUserEntityById(request.getUserId());
        var event = eventService.getEventEntityById(request.getEventId());
        var currentUser = userService.getAuthenticatedUser();

        boolean isSelfRegistration = currentUser.getId().equals(user.getId());
        if (!isSelfRegistration) {
            log.debug("Registration is for a different user. Verifying authorization. InitiatorId: {}, TargetUserId: {}", currentUser.getId(), user.getId());
            competitorSecurityUtils.checkCreate(event.getType().getName());
        }

        if (competitorDao.existsByUserAndEvent(user, event)) {
            log.warn("Competitor registration failed: Mapping already exists. UserId: {}, EventId: {}", request.getUserId(), request.getEventId());
            throw new BusinessException(CompetitorMessages.COMPETITOR_ALREADY_IN_COMPETITION);
        }

        if (!event.isActive()) {
            log.warn("Competitor registration failed: Event is not active. EventId: {}", request.getEventId());
            throw new BusinessException(EventMessages.EVENT_NOT_ACTIVE);
        }

        var competitor = Competitor.builder()
                .user(user)
                .event(event)
                .score(isSelfRegistration ? null : request.getPoints())
                .isWinner(request.isWinner())
                .build();

        var savedCompetitor = competitorDao.save(competitor);
        log.info("Competitor registered successfully. CompetitorId: {}", savedCompetitor.getId());

        return convertToDto(savedCompetitor);
    }

    @Override
    @Transactional
    public CompetitorDto updateCompetitor(UUID id, UpdateCompetitorRequest request) {
        log.info("Initiating competitor update. CompetitorId: {}", id);
        var competitor = getCompetitorEntityById(id);

        competitorSecurityUtils.checkUpdate(competitor.getEvent().getType().getName());

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

        var updatedCompetitor = competitorDao.save(competitor);
        log.info("Competitor updated successfully. CompetitorId: {}", updatedCompetitor.getId());

        return convertToDto(updatedCompetitor);
    }

    @Override
    @Transactional
    public void deleteCompetitor(UUID competitorId) {
        log.info("Initiating competitor deletion. CompetitorId: {}", competitorId);

        Competitor competitor = getCompetitorEntityById(competitorId);
        var currentUser = userService.getAuthenticatedUser();

        if (!currentUser.getId().equals(competitor.getUser().getId())) {
            log.debug("Initiating cross-user deletion authorization check. InitiatorId: {}, TargetUserId: {}", currentUser.getId(), competitor.getUser().getId());
            competitorSecurityUtils.checkDelete(competitor.getEvent().getType().getName());
        }

        competitorDao.delete(competitor);
        log.info("Competitor deleted successfully. CompetitorId: {}", competitorId);
    }

    @Override
    public CompetitorDto getCompetitorById(UUID id) {
        log.debug("Retrieving competitor. CompetitorId: {}", id);
        return convertToDto(getCompetitorEntityById(id));
    }

    @Override
    public List<CompetitorDto> getMyCompetitors() {
        log.debug("Retrieving authenticated user's competitors.");
        return convertToDtoList(competitorDao.findCompetitorsByUser(userService.getAuthenticatedUserEntity()));
    }

    @Override
    public List<CompetitorDto> getAllCompetitors() {
        log.debug("Retrieving all competitors.");
        return convertToDtoList(competitorDao.findAll());
    }

    @Override
    public List<CompetitorDto> getCompetitorsByEventId(UUID eventId) {
        log.debug("Retrieving competitors by event. EventId: {}", eventId);
        return convertToDtoList(competitorDao.findByEventId(eventId));
    }

    @Override
    public List<CompetitorDto> getCompetitorsByUserId(UUID userId) {
        log.debug("Retrieving competitors by user. UserId: {}", userId);
        return convertToDtoList(competitorDao.findByUserId(userId));
    }

    @Override
    public List<CompetitorDto> getCompetitorsByEventTypeId(UUID eventTypeId) {
        log.debug("Retrieving competitors by event type. EventTypeId: {}", eventTypeId);
        return convertToDtoList(competitorDao.findAllByEventType(eventTypeService.getEventTypeEntityById(eventTypeId)));
    }

    @Override
    public List<LeaderboardDto> getLeaderboardByEventType(String eventTypeName) {
        log.info("Processing leaderboard retrieval. EventTypeName: {}", eventTypeName);
        List<LeaderboardScoreDto> scores = competitorDao.getLeaderboardScoresByEventType(eventTypeName);
        return processLeaderboard(scores);
    }

    @Override
    public List<LeaderboardDto> getLeaderboardBySeasonAndEventType(UUID seasonId, String eventTypeName) {
        log.info("Processing leaderboard retrieval by season. SeasonId: {}, EventTypeName: {}", seasonId, eventTypeName);
        List<LeaderboardScoreDto> scores = competitorDao.getLeaderboardScoresBySeasonAndEventType(eventTypeName, seasonId);
        return processLeaderboard(scores);
    }

    private List<LeaderboardDto> processLeaderboard(List<LeaderboardScoreDto> scores) {
        log.debug("Processing and ranking leaderboard scores. TotalEntries: {}", scores.size());

        List<LeaderboardDto> leaderboard = scores.stream().map(score ->
                new LeaderboardDto(userService.getUserById(score.getUserId()), score.getTotalScore(), score.getEventCount(), 0)
        ).collect(Collectors.toList());

        assignRanks(leaderboard);
        return leaderboard;
    }

    @Override
    public CompetitorDto getEventWinner(UUID eventId) {
        log.debug("Retrieving event winner. EventId: {}", eventId);
        return convertToDto(competitorDao.findWinnerOfEvent(eventService.getEventEntityById(eventId)));
    }

    @Override
    public double getUserTotalPoints(UUID userId) {
        log.debug("Calculating user total points. UserId: {}", userId);
        return competitorDao.findCompetitorsByUser(userService.getUserEntityById(userId)).stream()
                .map(Competitor::getScore)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    @Override
    public boolean isUserParticipant(UUID userId, UUID eventId) {
        log.debug("Checking user participation status. UserId: {}, EventId: {}", userId, eventId);
        return competitorDao.existsByUserAndEvent(
                userService.getUserEntityById(userId),
                eventService.getEventEntityById(eventId)
        );
    }

    @Override
    public Competitor getCompetitorEntityById(UUID id) {
        log.debug("Retrieving competitor entity. CompetitorId: {}", id);

        return competitorDao.findById(id)
                .orElseThrow(() -> {
                    log.error("Competitor entity retrieval failed: Resource not found. CompetitorId: {}", id);
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