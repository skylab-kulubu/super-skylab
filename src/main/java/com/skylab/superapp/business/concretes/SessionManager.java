package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.business.abstracts.SessionService;
import com.skylab.superapp.core.constants.SessionMessages;
import com.skylab.superapp.core.exceptions.*;
import com.skylab.superapp.core.mappers.SessionMapper;
import com.skylab.superapp.dataAccess.SessionDao;
import com.skylab.superapp.entities.DTOs.sessions.CreateSessionRequest;
import com.skylab.superapp.entities.DTOs.sessions.SessionDto;
import com.skylab.superapp.entities.DTOs.sessions.UpdateSessionRequest;
import com.skylab.superapp.entities.Session;
import com.skylab.superapp.entities.SessionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SessionManager implements SessionService {

    private final SessionDao sessionDao;
    private final SessionMapper sessionMapper;
    private final EventService eventService;
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

    public SessionManager(SessionDao sessionDao, SessionMapper sessionMapper, EventService eventService) {
        this.sessionDao = sessionDao;
        this.sessionMapper = sessionMapper;
        this.eventService = eventService;
    }

    @Override
    public List<SessionDto> getAllSessions() {
        logger.info("Getting all sessions");
        return sessionMapper.toDtoList(sessionDao.findAll());
    }

    @Override
    public SessionDto getSessionById(UUID id) {
        logger.info("Getting session with id: {}", id);
        return sessionMapper.toDto(getSessionEntityById(id));
    }

    @Override
    public SessionDto updateSession(UUID id, UpdateSessionRequest updateSessionRequest) {
        logger.info("Updating session with id: {}", id);
        var session = getSessionEntityById(id);

        session.setTitle(updateSessionRequest.getTitle()==null ? session.getTitle() : updateSessionRequest.getTitle());
        session.setSpeakerName(updateSessionRequest.getSpeakerName() == null ? session.getSpeakerName() : updateSessionRequest.getSpeakerName());
        session.setSpeakerLinkedin(updateSessionRequest.getSpeakerLinkedin() == null ? session.getSpeakerLinkedin() : updateSessionRequest.getSpeakerLinkedin());
        session.setDescription(updateSessionRequest.getDescription() == null ? session.getDescription() : updateSessionRequest.getDescription());
        session.setStartTime(updateSessionRequest.getStartTime() == null ? session.getStartTime() : updateSessionRequest.getStartTime());
        session.setEndTime(updateSessionRequest.getEndTime() == null ? session.getEndTime() : updateSessionRequest.getEndTime());
        session.setOrderIndex(updateSessionRequest.getOrderIndex() == 0 ? session.getOrderIndex() : updateSessionRequest.getOrderIndex());
        session.setSessionType(updateSessionRequest.getSessionType() == null ? session.getSessionType() : SessionType.valueOf(updateSessionRequest.getSessionType().name()));

        var savedSession = sessionDao.save(session);
        logger.info("Session updated successfully");

        return sessionMapper.toDto(savedSession);
    }

    @Override
    public SessionDto addSession(CreateSessionRequest createSessionDto) {
        logger.info("Adding session with title: {}", createSessionDto.getTitle());
        if (createSessionDto.getStartTime().isAfter(createSessionDto.getEndTime())) {
            throw new SessionStartDateCannotBeAfterEndDateException();
        }

        var event = eventService.getEventEntityById(createSessionDto.getEventId());

        Session session = Session.builder()
                .title(createSessionDto.getTitle())
                .speakerName(createSessionDto.getSpeakerName())
                .speakerLinkedin(createSessionDto.getSpeakerLinkedin())
                .description(createSessionDto.getDescription())
                .orderIndex(createSessionDto.getOrderIndex())
                .startTime(createSessionDto.getStartTime())
                .endTime(createSessionDto.getEndTime())
                .event(event)
                .sessionType(SessionType.valueOf(createSessionDto.getSessionType().name()))
                .build();

        var savedSession = sessionDao.save(session);
        logger.info("Session added successfully with id: {}", savedSession.getId());

        return sessionMapper.toDto(savedSession);
    }

    @Override
    public void deleteSession(UUID id) {
        logger.info("Deleting session with id: {}", id);
        sessionDao.findById(id).orElseThrow(() -> new ResourceNotFoundException(SessionMessages.SESSION_NOT_FOUND));

        logger.info("Session found, proceeding to delete");

        sessionDao.deleteById(id);

        logger.info("Session deleted successfully");
    }

    @Override
    public Session getSessionEntityById(UUID id) {
        logger.info("Getting session entity with id: {}", id);
        return sessionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SessionMessages.SESSION_NOT_FOUND));
    }

    @Override
    public List<SessionDto> getSessionsByEventId(UUID eventId) {
        logger.info("Getting sessions for event with id: {}", eventId);
        var event = eventService.getEventEntityById(eventId);
        logger.info("Event found, retrieving sessions");

        var sessions = sessionDao.findAllByEvent(event);

        logger.info("Sessions retrieved successfully, count: {}", sessions.size());

        return sessionMapper.toDtoList(sessions);
    }
}
