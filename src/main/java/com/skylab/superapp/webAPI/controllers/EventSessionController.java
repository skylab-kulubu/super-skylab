package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.SessionService;
import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.entities.DTOs.sessions.SessionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events/{eventId}/sessions")
@RequiredArgsConstructor
@Tag(name = "Etkinlik Oturumları", description = "Etkinlik oturumları ile ilgili işlemler")
public class EventSessionController {

    private final SessionService sessionService;

    @GetMapping
    @Operation(summary = "Etkinliğin Oturumlarını Getir", description = "Verilen etkinlik ID'sine ait tüm oturumları listeler.")
    public ResponseEntity<DataResult<List<SessionDto>>> getSessionsByEventId(@PathVariable UUID eventId, @RequestParam(required = false) UUID eventDayId) {
        List<SessionDto> result;
        if (eventDayId != null) {
            result = sessionService.getSessionsByEventDayId(eventDayId);
        } else {
            result = sessionService.getSessionsByEventId(eventId);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_GET_SESSIONS_BY_EVENT_ID, HttpStatus.OK));
    }


}
