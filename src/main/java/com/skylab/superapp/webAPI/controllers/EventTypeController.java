package com.skylab.superapp.webAPI.controllers;


import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.core.constants.EventTypeMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.eventType.CreateEventTypeRequest;
import com.skylab.superapp.entities.DTOs.eventType.EventTypeDto;
import com.skylab.superapp.entities.DTOs.eventType.UpdateEventTypeRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/eventTypes")
public class EventTypeController {

    private final EventTypeService eventTypeService;

    public EventTypeController(EventTypeService eventTypeService) {
        this.eventTypeService = eventTypeService;
    }


    @GetMapping("/")
    public ResponseEntity<DataResult<List<EventTypeDto>>> getAllEventTypes(HttpServletRequest request) {
        var result = eventTypeService.getAllEventTypes();

        if (result.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new SuccessDataResult(EventTypeMessages.EVENT_TYPES_NO_CONTENT,
                            HttpStatus.OK, request.getRequestURI()));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventTypeMessages.EVENT_TYPES_LISTED,
                        HttpStatus.OK, request.getRequestURI()));

    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<EventTypeDto>> getEventTypeById(@PathVariable UUID id, HttpServletRequest request) {
        var result = eventTypeService.getEventTypeById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventTypeMessages.EVENT_TYPE_FOUND,
                        HttpStatus.OK, request.getRequestURI()));
    }


    @PostMapping("/")
    public ResponseEntity<DataResult<EventTypeDto>> addEventType(@RequestBody CreateEventTypeRequest createEventTypeRequest,
                                                                 HttpServletRequest request) {
        var result = eventTypeService.addEventType(createEventTypeRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(result, EventTypeMessages.EVENT_TYPE_ADDED,
                        HttpStatus.CREATED, request.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResult<EventTypeDto>> updateEventType(@PathVariable UUID id,
                                                  @RequestBody UpdateEventTypeRequest updateEventTypeRequest,
                                                  HttpServletRequest request) {
        var result = eventTypeService.updateEventType(id, updateEventTypeRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventTypeMessages.EVENT_TYPE_UPDATED,
                        HttpStatus.OK, request.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteEventType(@PathVariable UUID id, HttpServletRequest request) {
        eventTypeService.deleteEventType(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(EventTypeMessages.EVENT_TYPE_DELETED, HttpStatus.OK, request.getRequestURI()));
    }






}
