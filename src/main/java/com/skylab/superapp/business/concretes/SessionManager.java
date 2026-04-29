package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.*;
import com.skylab.superapp.core.constants.SessionMessages;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.exceptions.ValidationException;
import com.skylab.superapp.core.mappers.SessionMapper;
import com.skylab.superapp.core.utilities.security.SessionSecurityUtils;
import com.skylab.superapp.dataAccess.SessionDao;
import com.skylab.superapp.entities.DTOs.sessions.CreateSessionRequest;
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
    private final SessionSecurityUtils sessionSecurityUtils;

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
    public SessionDto updateSession(UUID id, UpdateSessionRequest updateSessionRequest) {
        log.info("Initiating session update. SessionId: {}", id);
        var session = getSessionEntityById(id);

        sessionSecurityUtils.checkUpdate(session.getEventDay().getEvent().getType().getName());

        session.setTitle(updateSessionRequest.getTitle() == null ? session.getTitle() : updateSessionRequest.getTitle());
        session.setSpeakerName(updateSessionRequest.getSpeakerName() == null ? session.getSpeakerName() : updateSessionRequest.getSpeakerName());
        session.setSpeakerLinkedin(updateSessionRequest.getSpeakerLinkedin() == null ? session.getSpeakerLinkedin() : updateSessionRequest.getSpeakerLinkedin());
        session.setDescription(updateSessionRequest.getDescription() == null ? session.getDescription() : updateSessionRequest.getDescription());
        session.setStartTime(updateSessionRequest.getStartTime() == null ? session.getStartTime() : updateSessionRequest.getStartTime());
        session.setEndTime(updateSessionRequest.getEndTime() == null ? session.getEndTime() : updateSessionRequest.getEndTime());
        session.setOrderIndex(updateSessionRequest.getOrderIndex() == 0 ? session.getOrderIndex() : updateSessionRequest.getOrderIndex());
        session.setSessionType(updateSessionRequest.getSessionType() == null ? session.getSessionType() : SessionType.valueOf(updateSessionRequest.getSessionType().name()));

        var savedSession = sessionDao.save(session);
        log.info("Session updated successfully. SessionId: {}", savedSession.getId());

        return sessionMapper.toDto(savedSession);
    }

    @Override
    public SessionDto addSession(CreateSessionRequest createSessionDto) {
        log.info("Initiating session creation. Title: {}, EventDayId: {}", createSessionDto.getTitle(), createSessionDto.getEventDayId());

        var eventDay = eventDayService.getEventDayEntityById(createSessionDto.getEventDayId());

        sessionSecurityUtils.checkCreate(eventDay.getEvent().getType().getName());

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
    public void deleteSession(UUID id) {
        log.info("Initiating session deletion. SessionId: {}", id);

        var session = sessionDao.findById(id).orElseThrow(() -> {
            log.error("Session deletion failed: Resource not found. SessionId: {}", id);
            return new ResourceNotFoundException(SessionMessages.SESSION_NOT_FOUND);
        });

        sessionSecurityUtils.checkDelete(session.getEventDay().getEvent().getType().getName());

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
}