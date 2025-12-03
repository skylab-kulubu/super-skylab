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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {


    private final EventService eventService;
    private final ObjectMapper objectMapper;

    public EventController(EventService eventService, ObjectMapper objectMapper) {
        this.eventService = eventService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public ResponseEntity<DataResult<List<EventDto>>> getAllEvents(@RequestParam(defaultValue = "false") boolean includeEventType,
                                                                   @RequestParam(defaultValue = "false") boolean includeSession,
                                                                   @RequestParam(defaultValue = "false") boolean includeCompetitors,
                                                                   @RequestParam(defaultValue = "false") boolean includeImages,
                                                                   @RequestParam(defaultValue = "false") boolean includeSeason,
                                                                   @RequestParam(defaultValue = "false") boolean includeCompetition){
        var result = eventService.getAllEvents(includeEventType, includeSession,
                includeCompetitors, includeImages,
                includeSeason, includeCompetition);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_GET_ALL_EVENTS,
                        HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<EventDto>> getEventById(@PathVariable UUID id,
                                                             @RequestParam(defaultValue = "false") boolean includeEventType,
                                                             @RequestParam(defaultValue = "false") boolean includeSession,
                                                             @RequestParam(defaultValue = "false") boolean includeCompetitors,
                                                             @RequestParam(defaultValue = "false") boolean includeImages,
                                                             @RequestParam(defaultValue = "false") boolean includeSeason,
                                                             @RequestParam(defaultValue = "false") boolean includeCompetition) {
       var result = eventService.getEventById(id, includeEventType, includeSession,
                includeCompetitors, includeImages,
                includeSeason, includeCompetition);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_GET_EVENT_BY_ID,
                        HttpStatus.OK));
    }


    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Add new event",
            description = "Adds a new event with optional cover image"
    )
    public ResponseEntity<DataResult<EventDto>> addEvent(
            @RequestPart("coverImage") MultipartFile coverImageFile,
            @Parameter(required = true, schema = @Schema(implementation = CreateEventRequest.class))
            @RequestPart("data") String createEventRequestJson
    ) throws JsonProcessingException {

        CreateEventRequest createEventRequest = objectMapper
                .readValue(createEventRequestJson, CreateEventRequest.class);

        var eventResult = eventService.addEvent(createEventRequest, coverImageFile);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(eventResult, EventMessages.SUCCESS_ADD_EVENT,
                        HttpStatus.CREATED));
    }



    @GetMapping("/active")
    public ResponseEntity<DataResult<List<EventDto>>> getActiveEvents(@RequestParam(defaultValue = "false") boolean includeEventType,
                                                                      @RequestParam(defaultValue = "false") boolean includeSession,
                                                                      @RequestParam(defaultValue = "false") boolean includeCompetitors,
                                                                      @RequestParam(defaultValue = "false") boolean includeImages,
                                                                      @RequestParam(defaultValue = "false") boolean includeSeason,
                                                                      @RequestParam(defaultValue = "false") boolean includeCompetition) {
        var result = eventService.getAllEventByIsActive(true, includeEventType, includeSession,
                includeCompetitors, includeImages,
                includeSeason, includeCompetition);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_GET_ACTIVE_EVENTS, HttpStatus.OK));
    }

    @GetMapping("/event-type")
    public ResponseEntity<?> getAllByEventType(@RequestParam String eventTypeName,
                                               @RequestParam(defaultValue = "false") boolean includeEventType,
                                               @RequestParam(defaultValue = "false") boolean includeSession,
                                               @RequestParam(defaultValue = "false") boolean includeCompetitors,
                                               @RequestParam(defaultValue = "false") boolean includeImages,
                                               @RequestParam(defaultValue = "false") boolean includeSeason,
                                               @RequestParam(defaultValue = "false") boolean includeCompetition) {

        var result = eventService.getAllEventsByEventTypeName(eventTypeName,
                includeEventType, includeSession,
                includeCompetitors, includeImages,
                includeSeason, includeCompetition);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_GET_ALL_EVENTS, HttpStatus.OK));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(EventMessages.SUCCESS_DELETE_EVENT, HttpStatus.OK));
    }

}