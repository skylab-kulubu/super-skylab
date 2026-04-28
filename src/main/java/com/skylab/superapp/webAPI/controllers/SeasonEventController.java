package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/seasons/{seasonId}/events")
@RequiredArgsConstructor
@Tag(name = "Sezon - Etkinlik Yönetimi", description = "Etkinlikleri ilgili sezonlara bağlama ve ayırma işlemleri")
public class SeasonEventController {

    private final EventService eventService;



    @GetMapping
    @Operation(summary = "Sezona Ait Etkinlikleri Getir", description = "Belirtilen sezona dahil olan tüm etkinlikleri listeler.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etkinlikler başarıyla listelendi."),
            @ApiResponse(responseCode = "404", description = "Sezon bulunamadı.", content = @Content)
    })
    public ResponseEntity<DataResult<List<EventDto>>> getEventsBySeason(
            @Parameter(description = "Sezon UUID") @PathVariable UUID seasonId) {
        log.info("REST request to get events for season id: {}", seasonId);
        var result = eventService.getEventsBySeasonId(seasonId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.EVENTS_LISTED_FOR_SEASON, HttpStatus.OK));

    }

    @PostMapping("/{eventId}")
    @Operation(summary = "Etkinliği Sezona Ata", description = "Var olan bir etkinliği belirtilen sezona dahil eder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etkinlik sezona başarıyla atandı."),
            @ApiResponse(responseCode = "400", description = "Etkinlik zaten bu sezona atanmış.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Sezon veya etkinlik bulunamadı.", content = @Content)
    })
    public ResponseEntity<Result> addEventToSeason(
            @Parameter(description = "Sezon UUID") @PathVariable UUID seasonId,
            @Parameter(description = "Etkinlik UUID") @PathVariable UUID eventId) {
        log.info("REST request to assign event id: {} to season id: {}", eventId, seasonId);
        eventService.assignSeasonToEvent(eventId, seasonId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(EventMessages.SUCCESS_ASSIGN_SEASON_TO_EVENT, HttpStatus.OK));
    }

    @DeleteMapping("/{eventId}")
    @Operation(summary = "Etkinliği Sezondan Çıkar", description = "Belirtilen etkinliğin sezon ile olan bağını koparır.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etkinlik sezondan başarıyla çıkarıldı."),
            @ApiResponse(responseCode = "400", description = "Etkinlik zaten bir sezona ait değil.", content = @Content)
    })
    public ResponseEntity<Result> removeEventFromSeason(
            @Parameter(description = "Sezon UUID") @PathVariable UUID seasonId,
            @Parameter(description = "Etkinlik UUID") @PathVariable UUID eventId) {
        log.info("REST request to remove event id: {} from season id: {}", eventId, seasonId);
        eventService.removeSeasonFromEvent(eventId);
        //TODO: Check if the event is actually associated with the season before attempting to remove it, and return appropriate response if not.

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(EventMessages.SUCCESS_REMOVE_SEASON_FROM_EVENT, HttpStatus.OK));
    }


}
