package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.core.constants.EventTypeMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.DTOs.eventType.CreateEventTypeRequest;
import com.skylab.superapp.entities.DTOs.eventType.EventTypeDto;
import com.skylab.superapp.entities.DTOs.eventType.UpdateEventTypeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/event-types")
@RequiredArgsConstructor
public class EventTypeController {

    private final EventTypeService eventTypeService;

    @GetMapping
    public ResponseEntity<DataResult<List<EventTypeDto>>> getAllEventTypes() {
        log.info("REST request to get all event types");
        var result = eventTypeService.getAllEventTypes();

        if (result.isEmpty()){
            log.debug("No event types found, returning NO_CONTENT");
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new SuccessDataResult<>(EventTypeMessages.EVENT_TYPES_NO_CONTENT, HttpStatus.NO_CONTENT));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventTypeMessages.EVENT_TYPES_LISTED, HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<EventTypeDto>> getEventTypeById(@PathVariable UUID id) {
        log.info("REST request to get event type by id: {}", id);
        var result = eventTypeService.getEventTypeById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventTypeMessages.EVENT_TYPE_FOUND, HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<DataResult<EventTypeDto>> addEventType(@RequestBody CreateEventTypeRequest request) {
        log.info("REST request to add new event type with name: {}", request.getName());
        var result = eventTypeService.addEventType(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(result, EventTypeMessages.EVENT_TYPE_ADDED, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResult<EventTypeDto>> updateEventType(@PathVariable UUID id, @RequestBody UpdateEventTypeRequest request) {
        log.info("REST request to update event type with id: {}", id);
        var result = eventTypeService.updateEventType(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventTypeMessages.EVENT_TYPE_UPDATED, HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteEventType(@PathVariable UUID id) {
        log.info("REST request to delete event type with id: {}", id);
        eventTypeService.deleteEventType(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(EventTypeMessages.EVENT_TYPE_DELETED, HttpStatus.OK));
    }

    @GetMapping("/{eventTypeName}/coordinators")
    public ResponseEntity<DataResult<Set<UserDto>>> getCoordinatorsByEventType(@PathVariable String eventTypeName) {
        log.info("REST request to get coordinators for event type: {}", eventTypeName);
        Set<UserDto> result = eventTypeService.getCoordinatorsByEventTypeName(eventTypeName);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventTypeMessages.COORDINATORS_FOUND, HttpStatus.OK));
    }
}