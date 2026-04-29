package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.core.constants.EventMessages;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessResult;
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
@RequestMapping("/api/events/{eventId}/images")
@RequiredArgsConstructor
@Tag(name = "Etkinlik Görselleri", description = "Etkinliklere görsel ekleme ve çıkarma işlemleri.")
public class EventImageController {

    private final EventService eventService;

    @PostMapping
    @Operation(summary = "Etkinliğe Görsel Ekle", description = "Belirtilen etkinliğe bir veya birden fazla görsel atar.")
    public ResponseEntity<Result> addImagesToEvent(
            @Parameter(description = "Etkinlik UUID") @PathVariable UUID eventId,
            @RequestBody List<UUID> imageIds) {
        eventService.addImagesToEvent(eventId, imageIds);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(EventMessages.SUCCESS_ADD_IMAGES_TO_EVENT, HttpStatus.OK));
    }

    @DeleteMapping
    @Operation(summary = "Etkinlikten Görsel Çıkar", description = "Belirtilen etkinlikten bir veya birden fazla görseli kaldırır.")
    public ResponseEntity<Result> removeImagesFromEvent(
            @Parameter(description = "Etkinlik UUID") @PathVariable UUID eventId,
            @RequestBody List<UUID> imageIds) {
        eventService.removeImagesFromEvent(eventId, imageIds);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(EventMessages.SUCCESS_REMOVE_IMAGES_FROM_EVENT, HttpStatus.OK));
    }
}