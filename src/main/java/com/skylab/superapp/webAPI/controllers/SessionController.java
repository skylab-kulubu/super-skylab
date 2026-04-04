package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.SessionService;
import com.skylab.superapp.core.constants.SessionMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.entities.DTOs.sessions.CreateSessionRequest;
import com.skylab.superapp.entities.DTOs.sessions.SessionDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public ResponseEntity<DataResult<List<SessionDto>>> getAllSessions() {
        log.info("REST request to get all sessions");
        var result = sessionService.getAllSessions();

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SessionMessages.SESSIONS_GET_ALL_SUCCESS, HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<DataResult<SessionDto>> addSession(@RequestBody @Valid CreateSessionRequest request) {
        log.info("REST request to add new session with title: {}", request.getTitle());
        var result = sessionService.addSession(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(result, SessionMessages.SESSION_CREATED_SUCCESSFULLY, HttpStatus.CREATED));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DataResult<Void>> deleteSession(@PathVariable UUID id) {
        log.info("REST request to delete session with id: {}", id);
        sessionService.deleteSession(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(null, SessionMessages.SESSION_DELETED_SUCCESSFULLY, HttpStatus.OK));
    }
}