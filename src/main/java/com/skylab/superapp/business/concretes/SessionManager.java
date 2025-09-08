package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.SessionService;
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

    public SessionManager(SessionDao sessionDao, SessionMapper sessionMapper) {
        this.sessionDao = sessionDao;
        this.sessionMapper = sessionMapper;
    }

    @Override
    public List<SessionDto> getAllSessions(boolean includeSpeakerImage, boolean includeEvent) {
        return sessionMapper.toDtoList(sessionDao.findAll(), includeSpeakerImage, includeEvent);
    }

    @Override
    public SessionDto getSessionById(UUID id, boolean includeSpeakerImage, boolean includeEvent) {
        return sessionMapper.toDto(getSessionEntityById(id), includeSpeakerImage, includeEvent);
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
            throw new SessionDatesCannotBeNullException();
        }

        if (createSessionDto.getStartTime().isAfter(createSessionDto.getEndTime())) {
            throw new SessionStartDateCannotBeAfterEndDateException();
        }

        if (createSessionDto.getTitle() == null || createSessionDto.getTitle().isEmpty()) {
            throw new SessionTitleCannotBeNullOrBlankException();
        }

        if (createSessionDto.getSpeakerName() == null || createSessionDto.getSpeakerName().isEmpty()) {
            throw new SessionSpeakerNameCannotBeNullOrBlankException();
        }

        Session session = Session.builder()
                .title(createSessionDto.getTitle())
                .speakerName(createSessionDto.getSpeakerName())
                .speakerLinkedin(createSessionDto.getSpeakerLinkedin())
                .description(createSessionDto.getDescription())
                .orderIndex(createSessionDto.getOrderIndex())
                .startTime(createSessionDto.getStartTime())
                .endTime(createSessionDto.getEndTime())
                .sessionType(SessionType.valueOf(createSessionDto.getSessionType().name()))
                .build();

        return sessionMapper.toDto(sessionDao.save(session));
    }

    @Override
    public void deleteSession(UUID id) {
        sessionDao.findById(id).orElseThrow(SessionNotFoundException::new);
        sessionDao.deleteById(id);
    }

    @Override
    public Session getSessionEntityById(UUID id) {
        return sessionDao.findById(id)
                .orElseThrow(SessionNotFoundException::new);
    }
}
