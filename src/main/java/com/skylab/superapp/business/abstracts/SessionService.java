package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.sessions.CreateSessionRequest;
import com.skylab.superapp.entities.DTOs.sessions.SessionDto;
import com.skylab.superapp.entities.DTOs.sessions.UpdateSessionRequest;
import com.skylab.superapp.entities.Session;

import java.util.List;
import java.util.UUID;

public interface SessionService {
    SessionDto addSession(CreateSessionRequest createSessionRequest);

    List<SessionDto> getAllSessions(boolean includeSpeakerImage, boolean includeEvent);

    SessionDto getSessionById(UUID id, boolean includeSpeakerImage, boolean includeEvent);

    SessionDto updateSession(UUID id, UpdateSessionRequest updateSessionRequest);

    void deleteSession(UUID id);

    Session getSessionEntityById(UUID id);
}
