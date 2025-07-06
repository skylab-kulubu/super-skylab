package com.skylab.superapp.webAPI.controllers;


import com.skylab.superapp.business.abstracts.SessionService;
import com.skylab.superapp.core.constants.SessionMessages;
import com.skylab.superapp.core.mappers.SessionMapper;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.entities.DTOs.sessions.CreateSessionDto;
import com.skylab.superapp.entities.DTOs.sessions.GetSessionDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final SessionMapper sessionMapper;

    public SessionController(SessionService sessionService, SessionMapper sessionMapper) {
        this.sessionService = sessionService;
        this.sessionMapper = sessionMapper;
    }


    @GetMapping("/")
    public ResponseEntity<DataResult<List<GetSessionDto>>> getAllSessions(HttpServletRequest request){
        var result = sessionService.getAllSessions();
        if (result.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new SuccessDataResult<>(SessionMessages.SESSIONS_EMPTY, HttpStatus.NO_CONTENT, request.getRequestURI()));
        }
        var dtos = sessionMapper.toDtoList(result);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(dtos, SessionMessages.SESSIONS_GET_ALL_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @PostMapping("/")
    public ResponseEntity<DataResult<GetSessionDto>> addSession(@RequestBody CreateSessionDto createSessionDto, HttpServletRequest request){
        var result = sessionService.addSession(createSessionDto);
        var dto = sessionMapper.toDto(result);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(dto, SessionMessages.SESSION_CREATED_SUCCESSFULLY, HttpStatus.CREATED, request.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DataResult<Void>> deleteSession(@PathVariable int id, HttpServletRequest request){
        sessionService.deleteSession(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(null, SessionMessages.SESSION_DELETED_SUCCESSFULLY, HttpStatus.OK, request.getRequestURI()));
    }




}
