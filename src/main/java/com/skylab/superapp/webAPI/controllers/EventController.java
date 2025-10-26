package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.Event.CreateEventRequest;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
import com.skylab.superapp.entities.DTOs.Event.UpdateEventRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {


    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
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

    @PostMapping("/")
    public ResponseEntity<DataResult<EventDto>> addEvent(@RequestPart("data") @Valid CreateEventRequest createEventRequest,
                                                         @RequestPart(value = "coverImage", required = false) MultipartFile coverImageFile) {
        var eventResult = eventService.addEvent(createEventRequest, coverImageFile);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(eventResult, EventMessages.SUCCESS_ADD_EVENT,
                        HttpStatus.CREATED));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DataResult<EventDto>> updateEvent(@PathVariable UUID id, @RequestBody UpdateEventRequest updateEventRequest) {
         var result = eventService.updateEvent(id, updateEventRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_UPDATE_EVENT, HttpStatus.OK));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(EventMessages.SUCCESS_DELETE_EVENT, HttpStatus.OK));
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

}