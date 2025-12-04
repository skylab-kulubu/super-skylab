package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.*;
import com.skylab.superapp.core.constants.CompetitorMessages;
import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.core.exceptions.BusinessException;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.mappers.CompetitorMapper;
import com.skylab.superapp.core.utilities.ldap.LdapService;
import com.skylab.superapp.dataAccess.CompetitorDao;
import com.skylab.superapp.entities.Competitor;
import com.skylab.superapp.entities.DTOs.Competitor.*;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.EventType;
import com.skylab.superapp.entities.LdapUser;
import com.skylab.superapp.entities.UserProfile;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompetitorManager implements CompetitorService {

    private final CompetitorDao competitorDao;
    private final UserService userService;
    private final EventService eventService;
    private final EventTypeService eventTypeService;
    private final CompetitorMapper competitorMapper;

    private static final Set<String> PRIVILEGED_ROLES = Set.of("ADMIN", "YK", "DK");


    public CompetitorManager(CompetitorDao competitorDao,@Lazy UserService userService,
                             @Lazy EventService eventService, @Lazy EventTypeService eventTypeService, CompetitorMapper competitorMapper){
        this.competitorDao = competitorDao;
        this.userService = userService;
        this.eventService = eventService;
        this.eventTypeService = eventTypeService;
        this.competitorMapper = competitorMapper;
    }

    @Override
    @Transactional
    public CompetitorDto addCompetitor(CreateCompetitorRequest createCompetitorRequest) {
        var user = userService.getUserEntityById(createCompetitorRequest.getUserId());
        var event = eventService.getEventEntityById(createCompetitorRequest.getEventId());
        var currentUser = userService.getAuthenticatedUser();

        boolean isSelfRegistration = currentUser.getId().equals(user.getId());
        if (!isSelfRegistration) {
            checkAuthorization(event.getType());
        }

        if (competitorDao.existsByUserAndEvent(user, event)) {
            throw new BusinessException(CompetitorMessages.COMPETITOR_ALREADY_IN_COMPETITION);
        }

        if (!event.isActive()) {
            throw new BusinessException(EventMessages.EVENT_NOT_ACTIVE);
        }

        var competitor = Competitor.builder()
                .user(user)
                .event(event)
                .score(isSelfRegistration ? null : createCompetitorRequest.getPoints())
                .isWinner(createCompetitorRequest.isWinner())
                .build();

        return convertToDto(competitorDao.save(competitor), true, true);
    }

    @Override
    public CompetitorDto updateCompetitor(UUID id, UpdateCompetitorRequest updateCompetitorRequest) {
      var competitor = getCompetitorEntityById(id);

        checkAuthorization(competitor.getEvent().getType());

        if (updateCompetitorRequest.getUserId() != null) {
            competitor.setUser(userService.getUserEntityById(updateCompetitorRequest.getUserId()));
        }


        if (updateCompetitorRequest.getEventId() != null) {
            var event = eventService.getEventEntityById(updateCompetitorRequest.getEventId());
            competitor.setEvent(event);
        }

        if (updateCompetitorRequest.getPoints() != 0) {
            competitor.setScore(updateCompetitorRequest.getPoints());
        }

        competitor.setWinner(updateCompetitorRequest.isWinner());

        return convertToDto(competitorDao.save(competitor), true, true);

    }


    @Override
    public void deleteCompetitor(UUID competitorId) {
        Competitor competitor = getCompetitorEntityById(competitorId);

        var currentUser = userService.getAuthenticatedUser();

        boolean isSelf = currentUser.getId().equals(competitor.getUser().getId());

        if (!isSelf){
            checkAuthorization(competitor.getEvent().getType());
        }

        competitorDao.delete(competitor);
    }

    @Override
    public CompetitorDto getCompetitorById(UUID id, boolean includeUser, boolean includeEvent) {
        return convertToDto(getCompetitorEntityById(id), includeUser, includeEvent);
    }

    @Override
    public List<CompetitorDto> getMyCompetitors(boolean includeUser, boolean includeEvent) {
        var authenticatedUser = userService.getAuthenticatedUserEntity();
        var result = competitorDao.findCompetitorsByUser(authenticatedUser);
        return convertToDtoList(result, includeUser, includeEvent);
    }

    @Override
    public List<CompetitorDto> getAllCompetitors(boolean includeUser, boolean includeEvent) {
        return convertToDtoList(competitorDao.findAll(), includeUser, includeEvent);
    }

    @Override
    public List<CompetitorDto> getCompetitorsByEventId(UUID eventId, boolean includeUser, boolean includeEvent) {
        return convertToDtoList(competitorDao.findByEventId(eventId), includeUser, includeEvent);
    }

    @Override
    public List<CompetitorDto> getCompetitorsByUserId(UUID userId, boolean includeUser, boolean includeEvent) {
        return convertToDtoList(competitorDao.findByUserId(userId), includeUser, includeEvent);
    }

    @Override
    public List<CompetitorDto> getCompetitorsByEventTypeId(UUID eventTypeId, boolean includeUser, boolean includeEvent) {
        var eventType = eventTypeService.getEventTypeEntityById(eventTypeId);
        return convertToDtoList(competitorDao.findAllByEventType(eventType), includeUser, includeEvent);
    }


    @Override
    public List<LeaderboardDto> getLeaderboardByEventType(String eventTypeName) {
        List<LeaderboardScoreDto> scores = competitorDao.getLeaderboardScoresByEventType(eventTypeName);

        List<LeaderboardDto> leaderboard = scores.stream().map(score -> {
            UserDto userDto = userService.getUserById(score.getUserId());

            return new LeaderboardDto(
                    userDto,
                    score.getTotalScore(),
                    score.getEventCount(),
                    0
            );
        }).collect(Collectors.toList());

        assignRanks(leaderboard);

        return leaderboard;
    }

    @Override
    public List<LeaderboardDto> getLeaderboardBySeasonAndEventType(UUID seasonId, String eventTypeName) {
        List<LeaderboardScoreDto> scores = competitorDao.getLeaderboardScoresBySeasonAndEventType(eventTypeName, seasonId);

        List<LeaderboardDto> leaderboard = scores.stream()
                .map(score -> {
                    UserDto userDto = userService.getUserById(score.getUserId());

                    return new LeaderboardDto(
                            userDto,
                            score.getTotalScore(),
                            score.getEventCount(),
                            0
                    );
                })
                .collect(Collectors.toList());
        assignRanks(leaderboard);

        return leaderboard;
    }


    @Override
    public CompetitorDto getEventWinner(UUID eventId, boolean includeUser, boolean includeEvent) {
        var event = eventService.getEventEntityById(eventId);
        return convertToDto(competitorDao.findWinnerOfEvent(event), includeUser, includeEvent);
    }

    @Override
    public double getUserTotalPoints(UUID userId) {
        var user = userService.getUserEntityById(userId);
        var competitors = competitorDao.findCompetitorsByUser(user);

        return competitors.stream()
                .map(Competitor::getScore)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
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
                .orElseThrow(() -> new ResourceNotFoundException(CompetitorMessages.COMPETITOR_NOT_FOUND));
    }

    private void checkAuthorization(EventType eventType) {
        var currentUser = userService.getAuthenticatedUser();
        boolean isAuthorized = currentUser.getRoles().stream()
                .anyMatch(role -> PRIVILEGED_ROLES.contains(role) ||
                        eventType.getAuthorizedRoles().contains(role));

        if (!isAuthorized) {
            throw new AccessDeniedException(EventMessages.USER_NOT_AUTHORIZED_FOR_EVENT_TYPE);
        }
    }

    private void assignRanks(List<LeaderboardDto> leaderboard) {
        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).setRank(i + 1);
        }
    }

    private List<CompetitorDto> convertToDtoList(List<Competitor> competitors, boolean includeUser, boolean includeEvent) {
        if (competitors.isEmpty()) return List.of();

        Map<UUID, UserDto> userDtoMap = new HashMap<>();

        if (includeUser) {
            List<UserProfile> profiles = competitors.stream()
                    .map(Competitor::getUser)
                    .distinct()
                    .toList();

            userDtoMap = userService.mapProfilesToUsers(profiles);
        }

        return competitorMapper.toDtoList(competitors, userDtoMap, includeUser, includeEvent);
    }

    private CompetitorDto convertToDto(Competitor competitor, boolean includeUser, boolean includeEvent) {
        UserDto userDto = null;
        if (includeUser && competitor.getUser() != null) {
            userDto = userService.getUserById(competitor.getUser().getId());
        }
        return competitorMapper.toDto(competitor, userDto, includeEvent);
    }



}