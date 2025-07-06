package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.sessions.CreateSessionDto;
import com.skylab.superapp.entities.Session;

import java.util.List;

public interface SessionService {
    List<Session> getAllSessions();

    Session addSession(CreateSessionDto createSessionDto);

    void deleteSession(int id);
}
