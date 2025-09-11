package com.skylab.superapp.webAPI.controllers;


import com.skylab.superapp.business.abstracts.SessionService;
import com.skylab.superapp.core.constants.SessionMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.entities.DTOs.sessions.CreateSessionRequest;
import com.skylab.superapp.entities.DTOs.sessions.SessionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }


    @GetMapping("/")
    public ResponseEntity<DataResult<List<SessionDto>>> getAllSessions(@RequestParam(defaultValue = "false") boolean includeSpeakerImage,
                                                                       @RequestParam(defaultValue = "false") boolean includeEvent){
        var result = sessionService.getAllSessions(includeSpeakerImage, includeEvent);
        if (result.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new SuccessDataResult<>(SessionMessages.SESSIONS_EMPTY,
                            HttpStatus.NO_CONTENT));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SessionMessages.SESSIONS_GET_ALL_SUCCESS,
                        HttpStatus.OK));
    }

    @PostMapping("/")
    public ResponseEntity<DataResult<SessionDto>> addSession(@RequestBody CreateSessionRequest createSessionRequest){
        var result = sessionService.addSession(createSessionRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(result, SessionMessages.SESSION_CREATED_SUCCESSFULLY, HttpStatus.CREATED));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DataResult<Void>> deleteSession(@PathVariable UUID id){
        sessionService.deleteSession(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(null, SessionMessages.SESSION_DELETED_SUCCESSFULLY, HttpStatus.OK));
    }

}
