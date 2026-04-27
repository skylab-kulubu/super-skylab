package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.ticket.request.GuestTicketRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/events/{eventId}/applications")
@RequiredArgsConstructor
@Tag(name = "Etkinlik Başvuruları (Bilet)", description = "e-skylab üyelerinin ve misafirlerin etkinliklere kayıt olma işlemleri")
public class EventApplicationController {

    private final EventService eventService;

    @PostMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Kayıtlı Üye Başvurusu", description = "Token sahibi kullanıcının etkinliğe bilet almasını sağlar.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Başvuru başarılı, bilet oluşturuldu."),
            @ApiResponse(responseCode = "400", description = "Kullanıcı bu etkinliğe zaten kayıtlı veya iş kuralı ihlali.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Yetkilendirme hatası.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Etkinlik bulunamadı.", content = @Content)
    })
    public ResponseEntity<Result> applyToEvent(@Parameter(description = "Etkinlik UUID", required = true) @PathVariable UUID eventId) {
        log.info("Authenticated user application on event with id: {}", eventId);

        eventService.applyToEvent(eventId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResult(EventMessages.SUCCESS_APPLY_TO_EVENT, HttpStatus.CREATED));
    }

    @PostMapping("/guest")
    @Operation(summary = "Misafir Başvurusu", description = "Sisteme kayıtlı olmayan dış kullanıcıların form doldurarak bilet almasını sağlar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Misafir bileti başarıyla oluşturuldu."),
            @ApiResponse(responseCode = "400", description = "Validasyon hatası veya e-posta kullanımda.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Etkinlik bulunamadı.", content = @Content)
    })
    public ResponseEntity<Result> applyToEventAsGuest(
            @Parameter(description = "Etkinlik UUID", required = true) @PathVariable UUID eventId,
            @Valid @RequestBody GuestTicketRequestDto request) {

        log.info("Guest application on event with id: {}", eventId);

        eventService.applyToEventAsGuest(eventId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResult(EventMessages.APPLY_TO_EVENT_AS_GUEST_SUCCESS, HttpStatus.CREATED));
    }


}