package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.EventDayService;
import com.skylab.superapp.core.constants.EventDayMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.eventDay.CreateEventDayRequest;
import com.skylab.superapp.entities.DTOs.eventDay.GetEventDayResponseDto;
import com.skylab.superapp.entities.DTOs.eventDay.UpdateEventDayRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/event-days")
@RequiredArgsConstructor
@Tag(name = "Etkinlik Günleri", description = "Çok günlü etkinliklerin günlük program yönetimi.")
public class EventDayController {

    private final EventDayService eventDayService;

    @GetMapping("/{id}")
    @Operation(summary = "Etkinlik Günü Detayını Getir", description = "Belirtilen UUID'ye sahip etkinlik gününün detaylarını döner.")
    public ResponseEntity<DataResult<GetEventDayResponseDto>> getEventDayById(
            @Parameter(description = "Etkinlik Günü UUID") @PathVariable UUID id) {
        var result = eventDayService.getEventDayById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventDayMessages.EVENT_DAY_GET_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Etkinliğe Ait Günleri Listele")
    public ResponseEntity<DataResult<List<GetEventDayResponseDto>>> getEventDaysByEventId(
            @Parameter(description = "Etkinlik UUID") @PathVariable UUID eventId) {
        var result = eventDayService.getEventDaysByEventId(eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventDayMessages.EVENT_DAY_GET_ALL_SUCCESS, HttpStatus.OK));
    }

    @PostMapping
    @Operation(summary = "Etkinlik Günü Oluştur")
    public ResponseEntity<DataResult<GetEventDayResponseDto>> createEventDay(
            @RequestBody CreateEventDayRequest request) {
        var result = eventDayService.createEventDay(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(result, EventDayMessages.EVENT_DAY_CREATED_SUCCESSFULLY, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Etkinlik Günü Güncelle")
    public ResponseEntity<DataResult<GetEventDayResponseDto>> updateEventDay(
            @Parameter(description = "Etkinlik Günü UUID") @PathVariable UUID id,
            @RequestBody UpdateEventDayRequest request) {
        var result = eventDayService.updateEventDay(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventDayMessages.EVENT_DAY_UPDATED_SUCCESSFULLY, HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Etkinlik Günü Sil", description = "Bağlı oturumu olmayan etkinlik gününü siler.")
    public ResponseEntity<Result> deleteEventDay(
            @Parameter(description = "Etkinlik Günü UUID") @PathVariable UUID id) {
        eventDayService.deleteEventDay(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(EventDayMessages.EVENT_DAY_DELETED_SUCCESSFULLY, HttpStatus.OK));
    }
}