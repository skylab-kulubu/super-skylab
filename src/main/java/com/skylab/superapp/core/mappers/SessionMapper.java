package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.sessions.SessionDto;
import com.skylab.superapp.entities.Session;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SessionMapper {

    private final ImageMapper imageMapper;
    private final EventMapper eventMapper;

    public SessionMapper(@Lazy ImageMapper imageMapper, @Lazy EventMapper eventMapper) {
        this.imageMapper = imageMapper;
        this.eventMapper = eventMapper;
    }

    public SessionDto toDto(Session session, boolean includeEvent) {

        if (session == null) {
            return null;
        }
        return new SessionDto(
                session.getId(),
                session.getTitle(),
                session.getSpeakerName(),
                session.getSpeakerLinkedin(),
                (session.getSpeakerImage() != null) ? imageMapper.toString(session.getSpeakerImage()) : null,
                session.getDescription(),
                session.getStartTime(),
                session.getEndTime(),
                session.getOrderIndex(),
                includeEvent ? eventMapper.toDto(session.getEvent()) : null,
                session.getSessionType()
        );
    }

    public SessionDto toDto(Session session) {
        return toDto(session, false);
    }


    public List<SessionDto> toDtoList(List<Session> sessions, boolean includeEvent) {
        return sessions.stream()
                .map(session -> toDto(session, includeEvent))
                .toList();
    }

    public List<SessionDto> toDtoList(List<Session> sessions) {
        return toDtoList(sessions, false);
    }
}
