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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SessionManager implements SessionService {

    private final SessionDao sessionDao;
    private final SessionMapper sessionMapper;
    private final EventService eventService;

    public SessionManager(SessionDao sessionDao, SessionMapper sessionMapper, EventService eventService) {
        this.sessionDao = sessionDao;
        this.sessionMapper = sessionMapper;
        this.eventService = eventService;
    }

    @Override
    public List<SessionDto> getAllSessions(boolean includeEvent) {
        return sessionMapper.toDtoList(sessionDao.findAll(), includeEvent);
    }

    @Override
    public SessionDto getSessionById(UUID id, boolean includeEvent) {
        return sessionMapper.toDto(getSessionEntityById(id), includeEvent);
    }

    @Override
    public SessionDto updateSession(UUID id, UpdateSessionRequest updateSessionRequest) {
        var session = getSessionEntityById(id);

        session.setTitle(updateSessionRequest.getTitle()==null ? session.getTitle() : updateSessionRequest.getTitle());
        session.setSpeakerName(updateSessionRequest.getSpeakerName() == null ? session.getSpeakerName() : updateSessionRequest.getSpeakerName());
        session.setSpeakerLinkedin(updateSessionRequest.getSpeakerLinkedin() == null ? session.getSpeakerLinkedin() : updateSessionRequest.getSpeakerLinkedin());
        session.setDescription(updateSessionRequest.getDescription() == null ? session.getDescription() : updateSessionRequest.getDescription());
        session.setStartTime(updateSessionRequest.getStartTime() == null ? session.getStartTime() : updateSessionRequest.getStartTime());
        session.setEndTime(updateSessionRequest.getEndTime() == null ? session.getEndTime() : updateSessionRequest.getEndTime());
        session.setOrderIndex(updateSessionRequest.getOrderIndex() == 0 ? session.getOrderIndex() : updateSessionRequest.getOrderIndex());
        session.setSessionType(updateSessionRequest.getSessionType() == null ? session.getSessionType() : SessionType.valueOf(updateSessionRequest.getSessionType().name()));


        return sessionMapper.toDto(sessionDao.save(session));
    }

    @Override
    public SessionDto addSession(CreateSessionRequest createSessionDto) {
        if (createSessionDto.getStartTime() == null || createSessionDto.getEndTime() == null) {
            throw new ValidationException(SessionMessages.SESSION_DATES_CANNOT_BE_NULL);
        }

        if (createSessionDto.getStartTime().isAfter(createSessionDto.getEndTime())) {
            throw new SessionStartDateCannotBeAfterEndDateException();
        }

        if (createSessionDto.getTitle() == null || createSessionDto.getTitle().isEmpty()) {
            throw new ValidationException(SessionMessages.SESSION_TITLE_CANNOT_BE_BLANK);
        }

        if (createSessionDto.getSpeakerName() == null || createSessionDto.getSpeakerName().isEmpty()) {
            throw new ValidationException(SessionMessages.SESSION_SPEAKER_NAME_CANNOT_BE_NULL_OR_BLANK);
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

        return sessionMapper.toDto(sessionDao.save(session));
    }

    @Override
    public void deleteSession(UUID id) {
        sessionDao.findById(id).orElseThrow(() -> new ResourceNotFoundException(SessionMessages.SESSION_NOT_FOUND));
        sessionDao.deleteById(id);
    }

    @Override
    public Session getSessionEntityById(UUID id) {
        return sessionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SessionMessages.SESSION_NOT_FOUND));
    }
}
