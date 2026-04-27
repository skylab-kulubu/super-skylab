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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Etkinlik Yönetimi", description = "Etkinliklerin listelenmesi, oluşturulması, güncellenmesi ve silinmesi işlemleri")
public class EventController {

    private final EventService eventService;

    @GetMapping
    @Operation(summary = "Tüm Etkinlikleri Getir", description = "Sistemdeki tüm etkinlikleri listeler. Filtreleme parametreleri opsiyoneldir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etkinlikler başarıyla listelendi.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessDataResult.class)))
    })
    public ResponseEntity<DataResult<List<EventDto>>> getAllEvents(

            @Parameter(description = "Etkinlik türü adına göre filtreleme", example = "AGC")
            @RequestParam(required = false) String typeName

    ) {
        log.info("REST request to get all events. Type filter: {}", typeName);

        List<EventDto> result;
        if (typeName != null && !typeName.isBlank()) {
            result = eventService.getAllEventsByEventTypeName(typeName);
        } else {
            result = eventService.getAllEvents();
        }
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessDataResult<>(result, EventMessages.SUCCESS_GET_ALL_EVENTS, HttpStatus.OK));
    }


    @GetMapping("/active")
    @Operation(summary = "Aktif Etkinlikleri Getir", description = "Yalnızca aktif durumu true olan etkinlikleri listeler.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aktif etkinlikler başarıyla listelendi.", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<DataResult<List<EventDto>>> getActiveEvents() {
        log.info("REST request to get all active events");
        var result = eventService.getAllEventByIsActive(true);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_GET_ACTIVE_EVENTS, HttpStatus.OK));
    }



    @GetMapping("/{id}")
    @Operation(summary = "Etkinlik Detayını Getir", description = "Belirtilen UUID değerine sahip etkinliğin detaylarını döner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etkinlik detayları getirildi."),
            @ApiResponse(responseCode = "404", description = "Etkinlik bulunamadı.", content = @Content)
    })
    public ResponseEntity<DataResult<EventDto>> getEventById(@Parameter(description = "Etkinlik UUID", required = true) @PathVariable UUID id) {
        log.info("REST request to get event by id: {}", id);
        var result = eventService.getEventById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_GET_EVENT_BY_ID, HttpStatus.OK));

    }


    @PostMapping
    @PreAuthorize("hasAnyRole('events.create', 'events.moderator')")
    @Operation(summary = "Yeni Etkinlik Oluştur", description = "Yeni bir etkinlik kaydı oluşturur. Kapak fotoğrafı opsiyoneldir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Etkinlik başarıyla oluşturuldu."),
            @ApiResponse(responseCode = "400", description = "Validasyon hatası veya geçersiz veri."),
            @ApiResponse(responseCode = "401", description = "Yetkilendirme hatası (Token geçersiz).", content = @Content),
            @ApiResponse(responseCode = "403", description = "Erişim reddedildi (Yetersiz rol).", content = @Content)
    })
    public ResponseEntity<DataResult<EventDto>> addEvent(
            @Parameter(description = "Etkinlik verileri (JSON)") @Valid @RequestPart("data") CreateEventRequest createEventRequest) {

        log.info("REST request to add a new event");

        var eventResult = eventService.addEvent(createEventRequest);

        log.info("Successfully created event with id: {}", eventResult.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(eventResult, EventMessages.SUCCESS_ADD_EVENT, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('events.update', 'events.moderator')")
    @Operation(summary = "Etkinliği Güncelle", description = "Var olan bir etkinliğin bilgilerini günceller.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etkinlik başarıyla güncellendi."),
            @ApiResponse(responseCode = "403", description = "Erişim reddedildi (Yetersiz rol).", content = @Content),
            @ApiResponse(responseCode = "404", description = "Güncellenmek istenen etkinlik bulunamadı.", content = @Content)
    })
    public ResponseEntity<DataResult<EventDto>> updateEvent(@Parameter(description = "Etkinlik UUID", required = true) @PathVariable UUID id,
                                                            @RequestBody UpdateEventRequest updateEventRequest) {
        log.info("REST request to update event with id: {}", id);
        var result = eventService.updateEvent(id, updateEventRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventMessages.SUCCESS_UPDATE_EVENT, HttpStatus.OK));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('events.delete', 'events.moderator')")
    @Operation(summary = "Etkinliği Sil", description = "Belirtilen etkinliği sistemden kalıcı olarak siler.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etkinlik başarıyla silindi."),
            @ApiResponse(responseCode = "403", description = "Erişim reddedildi (Yetersiz rol).", content = @Content),
            @ApiResponse(responseCode = "404", description = "Silinmek istenen etkinlik bulunamadı.", content = @Content)
    })
    public ResponseEntity<Result> deleteEvent(
            @Parameter(description = "Etkinlik UUID", required = true) @PathVariable UUID id) {
        log.info("REST request to delete event with id: {}", id);
        eventService.deleteEvent(id);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResult("Etkinlik başarıyla silindi.", HttpStatus.OK));
    }


}