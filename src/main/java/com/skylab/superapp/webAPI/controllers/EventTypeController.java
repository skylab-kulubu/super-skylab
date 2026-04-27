package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.core.constants.EventTypeMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.eventType.CreateEventTypeRequest;
import com.skylab.superapp.entities.DTOs.eventType.EventTypeDto;
import com.skylab.superapp.entities.DTOs.eventType.UpdateEventTypeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/event-types")
@RequiredArgsConstructor
@Tag(name = "Etkinlik Türü Yönetimi", description = "Sistemdeki etkinlik kategorilerinin (AGC, GECEKODU vb.) tanımlanması ve yönetilmesi.")
public class EventTypeController {

    private final EventTypeService eventTypeService;

    @GetMapping
    @Operation(summary = "Tüm Etkinlik Türlerini Getir", description = "Sistemdeki kayıtlı tüm etkinlik kategorilerini listeler.")
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
    @Operation(summary = "Etkinlik Türü Detayı Getir", description = "Belirtilen UUID'ye sahip etkinlik kategorisinin detaylarını getirir.")
    public ResponseEntity<DataResult<EventTypeDto>> getEventTypeById(@PathVariable UUID id) {
        log.info("REST request to get event type by id: {}", id);
        var result = eventTypeService.getEventTypeById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventTypeMessages.EVENT_TYPE_FOUND, HttpStatus.OK));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('event_types.create', 'event_types.moderator')")
    @Operation(summary = "Etkinlik Türü Ekle", description = "Yeni bir etkinlik kategorisi ve yetkili rolleri tanımlar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Etkinlik türü başarıyla eklendi."),
            @ApiResponse(responseCode = "403", description = "Yetkisiz erişim.", content = @Content)
    })
    public ResponseEntity<DataResult<EventTypeDto>> addEventType(@RequestBody CreateEventTypeRequest request) {
        log.info("REST request to add new event type with name: {}", request.getName());
        var result = eventTypeService.addEventType(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(result, EventTypeMessages.EVENT_TYPE_ADDED, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('event_types.update', 'event_types.moderator')")
    @Operation(summary = "Etkinlik Türünü Güncelle", description = "Etkinlik kategorisinin ismini veya yetkili rollerini günceller.")
    public ResponseEntity<DataResult<EventTypeDto>> updateEventType(@PathVariable UUID id, @RequestBody UpdateEventTypeRequest request) {
        log.info("REST request to update event type with id: {}", id);
        var result = eventTypeService.updateEventType(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventTypeMessages.EVENT_TYPE_UPDATED, HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('event_types.delete', 'event_types.moderator')")
    @Operation(summary = "Etkinlik Türü Sil", description = "Belirtilen etkinlik kategorisini sistemden siler.")
    public ResponseEntity<Result> deleteEventType(@PathVariable UUID id) {
        log.info("REST request to delete event type with id: {}", id);
        eventTypeService.deleteEventType(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(EventTypeMessages.EVENT_TYPE_DELETED, HttpStatus.OK));
    }
}