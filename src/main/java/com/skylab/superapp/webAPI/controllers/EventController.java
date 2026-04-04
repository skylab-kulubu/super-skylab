package com.skylab.superapp.webAPI.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.Event.CreateEventRequest;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
import com.skylab.superapp.entities.DTOs.Event.UpdateEventRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<DataResult<List<EventDto>>> getAllEvents() {
        log.info("REST request to get all events");
        var result = eventService.getAllEvents();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_GET_ALL_EVENTS, HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<EventDto>> getEventById(@PathVariable UUID id) {
        log.info("REST request to get event by id: {}", id);
        var result = eventService.getEventById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_GET_EVENT_BY_ID, HttpStatus.OK));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Add new event", description = "Adds a new event with optional cover image")
    public ResponseEntity<DataResult<EventDto>> addEvent(

            @RequestPart(value = "coverImage", required = false)
            MultipartFile coverImageFile,

            @Valid
            @RequestPart("data")
            CreateEventRequest createEventRequest

    ) {
        log.info("REST request to add a new event");

        var eventResult = eventService.addEvent(createEventRequest, coverImageFile);

        log.info("Successfully created event with id: {}", eventResult.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(eventResult, EventMessages.SUCCESS_ADD_EVENT, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResult<EventDto>> updateEvent(@PathVariable UUID id, @RequestBody UpdateEventRequest updateEventRequest) {
        log.info("REST request to update event with id: {}", id);
        var result = eventService.updateEvent(id, updateEventRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_UPDATE_EVENT, HttpStatus.OK));
    }

    @GetMapping("/active")
    public ResponseEntity<DataResult<List<EventDto>>> getActiveEvents() {
        log.info("REST request to get all active events");
        var result = eventService.getAllEventByIsActive(true);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_GET_ACTIVE_EVENTS, HttpStatus.OK));
    }

    @GetMapping("/type/{eventTypeName}")
    public ResponseEntity<DataResult<List<EventDto>>> getAllByEventType(@PathVariable String eventTypeName) {
        log.info("REST request to get events by type: {}", eventTypeName);
        var result = eventService.getAllEventsByEventTypeName(eventTypeName);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_GET_ALL_EVENTS, HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteEvent(@PathVariable UUID id) {
        log.info("REST request to delete event with id: {}", id);
        eventService.deleteEvent(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(EventMessages.SUCCESS_DELETE_EVENT, HttpStatus.OK));
    }
}