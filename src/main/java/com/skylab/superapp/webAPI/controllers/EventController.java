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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                                                                   @RequestParam(defaultValue = "false") boolean includeCompetition,
                                                                   HttpServletRequest request){
        var result = eventService.getAllEvents(includeEventType, includeSession,
                includeCompetitors, includeImages,
                includeSeason, includeCompetition);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_GET_ALL_EVENTS,
                        HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<EventDto>> getEventById(@PathVariable UUID id,
                                                             @RequestParam(defaultValue = "false") boolean includeEventType,
                                                             @RequestParam(defaultValue = "false") boolean includeSession,
                                                             @RequestParam(defaultValue = "false") boolean includeCompetitors,
                                                             @RequestParam(defaultValue = "false") boolean includeImages,
                                                             @RequestParam(defaultValue = "false") boolean includeSeason,
                                                             @RequestParam(defaultValue = "false") boolean includeCompetition,
                                                             HttpServletRequest request) {
       var result = eventService.getEventById(id, includeEventType, includeSession,
                includeCompetitors, includeImages,
                includeSeason, includeCompetition);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_GET_EVENT_BY_ID,
                        HttpStatus.OK, request.getRequestURI()));
    }

    @PostMapping("/")
    public ResponseEntity<DataResult<EventDto>> addEvent(@RequestBody CreateEventRequest createEventRequest,
                                                         HttpServletRequest request) {
        var eventResult = eventService.addEvent(createEventRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(eventResult, EventMessages.SUCCESS_ADD_EVENT,
                        HttpStatus.CREATED, request.getRequestURI()));
    }

    @PutMapping("/")
    public ResponseEntity<DataResult<EventDto>> updateEvent(@RequestBody UpdateEventRequest updateEventRequest,
                                                            HttpServletRequest request) {
         var result = eventService.updateEvent(updateEventRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_UPDATE_EVENT, HttpStatus.OK, request.getRequestURI()));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteEvent(@PathVariable UUID id,
                                         HttpServletRequest request) {
        eventService.deleteEvent(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(EventMessages.SUCCESS_DELETE_EVENT, HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/active")
    public ResponseEntity<DataResult<List<EventDto>>> getActiveEvents(@RequestParam(defaultValue = "false") boolean includeEventType,
                                                                      @RequestParam(defaultValue = "false") boolean includeSession,
                                                                      @RequestParam(defaultValue = "false") boolean includeCompetitors,
                                                                      @RequestParam(defaultValue = "false") boolean includeImages,
                                                                      @RequestParam(defaultValue = "false") boolean includeSeason,
                                                                      @RequestParam(defaultValue = "false") boolean includeCompetition,
                                                                      HttpServletRequest request) {
        var result = eventService.getAllEventByIsActive(true, includeEventType, includeSession,
                includeCompetitors, includeImages,
                includeSeason, includeCompetition);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_GET_ACTIVE_EVENTS, HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/getAllByEventType")
    public ResponseEntity<?> getAllByEventType(@RequestParam String eventTypeName,
                                               @RequestParam(defaultValue = "false") boolean includeEventType,
                                               @RequestParam(defaultValue = "false") boolean includeSession,
                                               @RequestParam(defaultValue = "false") boolean includeCompetitors,
                                               @RequestParam(defaultValue = "false") boolean includeImages,
                                               @RequestParam(defaultValue = "false") boolean includeSeason,
                                               @RequestParam(defaultValue = "false") boolean includeCompetition,
                                               HttpServletRequest request) {

        var result = eventService.getAllEventsByEventTypeName(eventTypeName,
                includeEventType, includeSession,
                includeCompetitors, includeImages,
                includeSeason, includeCompetition);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_GET_ALL_EVENTS, HttpStatus.OK, request.getRequestURI()));
    }

    /*
    @PostMapping("/updateBizbizeEvent")
    public ResponseEntity<?> updateBizbizeEvent(@RequestBody GetBizbizeEventDto getEventDto) {
        var result = eventService.updateBizbizeEvent(getEventDto);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }



    @GetMapping("/getAllBizbizeEvents")
    public ResponseEntity<?> getAllBizbizeEvents() {
        var result = eventService.getAllBizbizeEvents();
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }


    @PostMapping("/addImagesToEvent")
    public ResponseEntity<?> addImagesToEvent(@RequestParam int id, @RequestBody List<Integer> imageIds) {
        var result = eventService.addImagesToEvent(id, imageIds);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getAllFutureEventsByTenant")
    public ResponseEntity<?> getAllFutureEventsByTenant(@RequestParam String tenant) {
        var result = eventService.getAllFutureEventsByEventType(tenant);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

     */
}