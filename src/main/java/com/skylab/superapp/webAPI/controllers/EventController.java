package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.entities.DTOs.Event.CreateEventDto;
import com.skylab.superapp.entities.DTOs.Event.GetBizbizeEventDto;
import com.skylab.superapp.entities.DTOs.Event.GetEventDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/addEvent")
    public ResponseEntity<?> addEvent(@RequestBody CreateEventDto createEventDto) {
        var result = eventService.addEvent(createEventDto);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/deleteEvent")
    public ResponseEntity<?> deleteEvent(@RequestParam int id) {
        var result = eventService.deleteEvent(id);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/updateEvent")
    public ResponseEntity<?> updateEvent(@RequestBody GetEventDto getEventDto) {
        var result = eventService.updateEvent(getEventDto);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

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
        var result = eventService.getAllFutureEventsByTenant(tenant);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }
}