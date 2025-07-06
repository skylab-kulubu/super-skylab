package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.business.abstracts.SessionService;
import com.skylab.superapp.core.exceptions.*;
import com.skylab.superapp.dataAccess.SessionDao;
import com.skylab.superapp.entities.DTOs.sessions.CreateSessionDto;
import com.skylab.superapp.entities.Session;
import com.skylab.superapp.entities.SessionType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SessionManager implements SessionService {

    private final SessionDao sessionDao;
    private final EventService eventService;

    public SessionManager(SessionDao sessionDao,@Lazy EventService eventService) {
        this.sessionDao = sessionDao;
        this.eventService = eventService;
    }

    @Override
    public List<Session> getAllSessions() {
        return sessionDao.findAll();
    }

    @Override
    public Session addSession(CreateSessionDto createSessionDto) {
        if (createSessionDto.getStartTime() == null || createSessionDto.getEndTime() == null) {
            throw new SessionDatesCannotBeNullException();
        }

        if (createSessionDto.getStartTime().after(createSessionDto.getEndTime())) {
            throw new SessionStartDateCannotBeAfterEndDateException();
        }

        if (createSessionDto.getTitle() == null || createSessionDto.getTitle().isEmpty()) {
            throw new SessionTitleCannotBeNullOrBlankException();
        }

        if (createSessionDto.getSpeakerName() == null || createSessionDto.getSpeakerName().isEmpty()) {
            throw new SessionSpeakerNameCannotBeNullOrBlankException();
        }

        if (createSessionDto.getSessionType() == null || !isValidSessionType(createSessionDto.getSessionType())) {
            throw new SessionTypeNotValidException();
        }

        var event = eventService.getEventById(createSessionDto.getEventId());

        Session session = Session.builder()
                .title(createSessionDto.getTitle())
                .speakerName(createSessionDto.getSpeakerName())
                .speakerLinkedin(createSessionDto.getSpeakerLinkedin())
                .description(createSessionDto.getDescription())
                .orderIndex(createSessionDto.getOrderIndex())
                .startTime(createSessionDto.getStartTime())
                .endTime(createSessionDto.getEndTime())
                .sessionType(SessionType.valueOf(createSessionDto.getSessionType()))
                .event(event)
                .build();

        return sessionDao.save(session);
    }

    @Override
    public void deleteSession(int id) {
        sessionDao.findById(id).orElseThrow(SessionNotFoundException::new);
        sessionDao.deleteById(id);
    }

    private static final Set<String> VALID_SESSION_TYPES =
            EnumSet.allOf(SessionType.class)
                    .stream()
                    .map(Enum::name)
                    .collect(Collectors.toSet());

    private boolean isValidSessionType(String sessionTypeStr) {
        return VALID_SESSION_TYPES.contains(sessionTypeStr);
    }
}
