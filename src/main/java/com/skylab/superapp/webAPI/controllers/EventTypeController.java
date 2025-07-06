package com.skylab.superapp.webAPI.controllers;


import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.core.constants.EventTypeMessages;
import com.skylab.superapp.core.mappers.EventTypeMapper;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.eventType.CreateEventTypeDto;
import com.skylab.superapp.entities.DTOs.eventType.GetEventTypeDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventTypes")
public class EventTypeController {

    private final EventTypeService eventTypeService;
    private final EventTypeMapper eventTypeMapper;

    public EventTypeController(EventTypeService eventTypeService, EventTypeMapper eventTypeMapper) {
        this.eventTypeService = eventTypeService;
        this.eventTypeMapper = eventTypeMapper;
    }


    @GetMapping("/")
    public ResponseEntity<DataResult<List<GetEventTypeDto>>> getAllEventTypes(HttpServletRequest request) {
        var result = eventTypeService.getAllEventTypes();

        if (result.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new SuccessDataResult(EventTypeMessages.EVENT_TYPES_NO_CONTENT, HttpStatus.OK, request.getRequestURI()));
        }
        var dtos = eventTypeMapper.toDtoList(result);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(dtos, EventTypeMessages.EVENT_TYPES_LISTED, HttpStatus.OK, request.getRequestURI()));

    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<GetEventTypeDto>> getEventTypeById(@PathVariable int id, HttpServletRequest request) {
        var result = eventTypeService.getEventTypeById(id);
        var dto = eventTypeMapper.toDto(result);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(dto, EventTypeMessages.EVENT_TYPE_FOUND, HttpStatus.OK, request.getRequestURI()));
    }


    @PostMapping("/")
    public ResponseEntity<DataResult<GetEventTypeDto>> addEventType(@RequestBody CreateEventTypeDto createEventTypeDto, HttpServletRequest request) {
        var result = eventTypeService.addEventType(createEventTypeDto);
        var dto = eventTypeMapper.toDto(result);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(dto, EventTypeMessages.EVENT_TYPE_ADDED, HttpStatus.CREATED, request.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result> updateEventType(@PathVariable int id, @RequestBody CreateEventTypeDto createEventTypeDto, HttpServletRequest request) {
        eventTypeService.updateEventType(id, createEventTypeDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult( EventTypeMessages.EVENT_TYPE_UPDATED, HttpStatus.OK, request.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteEventType(@PathVariable int id, HttpServletRequest request) {
        eventTypeService.deleteEventType(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(EventTypeMessages.EVENT_TYPE_DELETED, HttpStatus.OK, request.getRequestURI()));
    }






}
