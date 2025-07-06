package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.core.mappers.EventMapper;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.Competitor.GetCompetitorDto;
import com.skylab.superapp.entities.DTOs.Event.CreateEventDto;
import com.skylab.superapp.entities.DTOs.Event.GetBizbizeEventDto;
import com.skylab.superapp.entities.DTOs.Event.GetEventDetailsDto;
import com.skylab.superapp.entities.DTOs.Event.GetEventDto;
import com.skylab.superapp.entities.EventType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @GetMapping("/")
    public ResponseEntity<DataResult<List<GetEventDetailsDto>>> getAllEvents(HttpServletRequest request){
        var result = eventService.getAllEvents();
        var dtos = eventMapper.toDetailsDtoList(result);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(dtos, EventMessages.SUCCESS_GET_ALL_EVENTS, HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<GetEventDetailsDto>> getEventById(@PathVariable int id, HttpServletRequest request) {
       var result = eventService.getEventById(id);
        var dto = eventMapper.toDetailsDto(result);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(dto, EventMessages.SUCCESS_GET_EVENT_BY_ID, HttpStatus.OK, request.getRequestURI()));
    }

    @PostMapping("/")
    public ResponseEntity<DataResult<GetEventDto>> addEvent(@RequestBody CreateEventDto createEventDto, HttpServletRequest request) {
        var eventResult = eventService.addEvent(createEventDto);
        var event = eventMapper.toDto(eventResult);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(event, EventMessages.SUCCESS_ADD_EVENT, HttpStatus.CREATED, request.getRequestURI()));
    }

    @PutMapping("/")
    public ResponseEntity<Result> updateEvent(@RequestBody GetEventDto getEventDto, HttpServletRequest request) {
        eventService.updateEvent(getEventDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(EventMessages.SUCCESS_UPDATE_EVENT, HttpStatus.OK, request.getRequestURI()));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@RequestParam int id, HttpServletRequest request) {
       eventService.deleteEvent(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(EventMessages.SUCCESS_DELETE_EVENT, HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/active")
    public ResponseEntity<DataResult<List<GetEventDto>>> getActiveEvents(HttpServletRequest request) {
        var result = eventService.getAllEventByIsActive(true);
        var dtos = eventMapper.toDtoList(result);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(dtos, EventMessages.SUCCESS_GET_ACTIVE_EVENTS, HttpStatus.OK, request.getRequestURI()));
    }



    /*
    @PostMapping("/updateBizbizeEvent")
    public ResponseEntity<?> updateBizbizeEvent(@RequestBody GetBizbizeEventDto getEventDto) {
        var result = eventService.updateBizbizeEvent(getEventDto);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getAllByEventType")
    public ResponseEntity<?> getAllEventsByTenant(@RequestParam String tenant) {
        var result = eventService.getAllEventsByEventType(tenant);
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