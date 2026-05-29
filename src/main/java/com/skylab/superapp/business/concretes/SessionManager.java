package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.*;
import com.skylab.superapp.core.constants.SessionMessages;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.exceptions.ValidationException;
import com.skylab.superapp.core.mappers.SessionMapper;
import com.skylab.superapp.core.security.authz.Authorize;
import com.skylab.superapp.core.security.authz.AuthzKey;
import com.skylab.superapp.dataAccess.SessionDao;
import com.skylab.superapp.entities.DTOs.sessions.CreateSessionRequest;
import com.skylab.superapp.entities.DTOs.sessions.PatchSessionRequest;
import com.skylab.superapp.entities.DTOs.sessions.SessionDto;
import com.skylab.superapp.entities.DTOs.sessions.UpdateSessionRequest;
import com.skylab.superapp.entities.Image;
import com.skylab.superapp.entities.Session;
import com.skylab.superapp.entities.SessionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionManager implements SessionService {

    private final SessionDao sessionDao;
    private final SessionMapper sessionMapper;
    private final EventService eventService;
    private final ImageService imageService;
    private final EventDayService eventDayService;
    private final MediaService mediaService;

    @Override
    public List<SessionDto> getAllSessions() {
        log.debug("Retrieving all sessions.");

        var sessions = sessionDao.findAll();

        log.info("Retrieved all sessions successfully. TotalCount: {}", sessions.size());

        return sessions.stream()
                .map(sessionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SessionDto getSessionById(UUID id) {
        log.debug("Retrieving session. SessionId: {}", id);
        return sessionMapper.toDto(getSessionEntityById(id));
    }

    @Override
    @Authorize(resource = "SESSION", action = "UPDATE")
    public SessionDto updateSession(@AuthzKey UUID id, UpdateSessionRequest updateSessionRequest) {
        log.info("Initiating session replace (PUT). SessionId: {}", id);
        var session = getSessionEntityById(id);

        validateTimeRange(id, updateSessionRequest.getStartTime(), updateSessionRequest.getEndTime());

        session.setTitle(updateSessionRequest.getTitle());
        session.setSpeakerName(updateSessionRequest.getSpeakerName());
        session.setSpeakerLinkedin(updateSessionRequest.getSpeakerLinkedin());
        session.setDescription(updateSessionRequest.getDescription());
        session.setStartTime(updateSessionRequest.getStartTime());
        session.setEndTime(updateSessionRequest.getEndTime());
        session.setOrderIndex(updateSessionRequest.getOrderIndex());
        session.setSessionType(SessionType.valueOf(updateSessionRequest.getSessionType().name()));

        var savedSession = sessionDao.save(session);
        log.info("Session replaced successfully. SessionId: {}", savedSession.getId());

        return sessionMapper.toDto(savedSession);
    }

    @Override
    @Authorize(resource = "SESSION", action = "UPDATE")
    public SessionDto patchSession(@AuthzKey UUID id, PatchSessionRequest request) {
        log.info("Initiating session patch (PATCH). SessionId: {}", id);
        var session = getSessionEntityById(id);

        var newStart = request.getStartTime() != null ? request.getStartTime() : session.getStartTime();
        var newEnd = request.getEndTime() != null ? request.getEndTime() : session.getEndTime();
        validateTimeRange(id, newStart, newEnd);

        if (request.getTitle() != null) session.setTitle(request.getTitle());
        if (request.getSpeakerName() != null) session.setSpeakerName(request.getSpeakerName());
        if (request.getSpeakerLinkedin() != null) session.setSpeakerLinkedin(request.getSpeakerLinkedin());
        if (request.getDescription() != null) session.setDescription(request.getDescription());
        if (request.getStartTime() != null) session.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) session.setEndTime(request.getEndTime());
        if (request.getOrderIndex() != null) session.setOrderIndex(request.getOrderIndex());
        if (request.getSessionType() != null) session.setSessionType(SessionType.valueOf(request.getSessionType().name()));

        var savedSession = sessionDao.save(session);
        log.info("Session patched successfully. SessionId: {}", savedSession.getId());

        return sessionMapper.toDto(savedSession);
    }

    private void validateTimeRange(UUID id, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            log.warn("Session update failed: Start time is after end time. SessionId: {}", id);
            throw new ValidationException(SessionMessages.START_DATE_CANNOT_BE_AFTER_END_DATE);
        }
    }

    @Override
    @Authorize(resource = "SESSION", action = "CREATE")
    public SessionDto addSession(@AuthzKey CreateSessionRequest createSessionDto) {
        log.info("Initiating session creation. Title: {}, EventDayId: {}", createSessionDto.getTitle(), createSessionDto.getEventDayId());

        var eventDay = eventDayService.getEventDayEntityById(createSessionDto.getEventDayId());

        if (createSessionDto.getStartTime().isAfter(createSessionDto.getEndTime())) {
            log.warn("Session creation failed: Start date is after end date. Title: {}", createSessionDto.getTitle());
            throw new ValidationException(SessionMessages.START_DATE_CANNOT_BE_AFTER_END_DATE);
        }

        Image speakerImage = null;
        if (createSessionDto.getSpeakerImageId() != null) {
            mediaService.attachImageMedia(createSessionDto.getSpeakerImageId());
            speakerImage = imageService.getImageEntityById(createSessionDto.getSpeakerImageId());
        }

        Session session = Session.builder()
                .title(createSessionDto.getTitle())
                .speakerName(createSessionDto.getSpeakerName())
                .speakerLinkedin(createSessionDto.getSpeakerLinkedin())
                .description(createSessionDto.getDescription())
                .orderIndex(createSessionDto.getOrderIndex())
                .startTime(createSessionDto.getStartTime())
                .speakerImage(speakerImage)
                .endTime(createSessionDto.getEndTime())
                .eventDay(eventDay)
                .sessionType(SessionType.valueOf(createSessionDto.getSessionType().name()))
                .build();

        var savedSession = sessionDao.save(session);
        log.info("Session created successfully. SessionId: {}", savedSession.getId());

        return sessionMapper.toDto(savedSession);
    }

    @Override
    @Authorize(resource = "SESSION", action = "DELETE")
    public void deleteSession(@AuthzKey UUID id) {
        log.info("Initiating session deletion. SessionId: {}", id);

        var session = sessionDao.findById(id).orElseThrow(() -> {
            log.error("Session deletion failed: Resource not found. SessionId: {}", id);
            return new ResourceNotFoundException(SessionMessages.SESSION_NOT_FOUND);
        });

        sessionDao.delete(session);

        log.info("Session deleted successfully. SessionId: {}", id);
    }

    @Override
    public Session getSessionEntityById(UUID id) {
        log.debug("Retrieving session entity. SessionId: {}", id);
        return sessionDao.findById(id)
                .orElseThrow(() -> {
                    log.error("Session entity retrieval failed: Resource not found. SessionId: {}", id);
                    return new ResourceNotFoundException(SessionMessages.SESSION_NOT_FOUND);
                });
    }

    @Override
    public List<SessionDto> getSessionsByEventId(UUID eventId) {
        log.debug("Retrieving sessions for event. EventId: {}", eventId);

        var event = eventService.getEventEntityById(eventId);
        var sessions = sessionDao.findAllByEventDay_Event(event);

        log.info("Sessions retrieved successfully for event. EventId: {}, TotalCount: {}", eventId, sessions.size());

        return sessions.stream()
                .map(sessionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SessionDto> getSessionsByEventDayId(UUID eventDayId) {
        log.debug("Retrieving sessions for eventDay. EventDayId: {}", eventDayId);
        var eventDay = eventDayService.getEventDayEntityById(eventDayId);
        return sessionDao.findAllByEventDay(eventDay)
                .stream()
                .map(sessionMapper::toDto)
                .collect(Collectors.toList());
    }
}