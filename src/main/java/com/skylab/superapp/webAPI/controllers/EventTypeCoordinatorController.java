package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.core.constants.EventTypeMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/event-types/{eventTypeName}/coordinators")
@RequiredArgsConstructor
@Tag(name = "Etkinlik Türü - Koordinatörler", description = "Etkinlik kategorilerinin organizasyon yetkililerini listeleme.")
public class EventTypeCoordinatorController {

    private final EventTypeService eventTypeService;

    @GetMapping
    @Operation(summary = "Kategori Koordinatörlerini Getir", description = "Belirtilen etkinlik türünün (Örn: AGC) sorumlu koordinatör listesini getirir. Genel erişime açıktır.")
    public ResponseEntity<DataResult<Set<UserDto>>> getCoordinatorsByEventType(@PathVariable String eventTypeName) {
        log.info("REST request to get coordinators for event type: {}", eventTypeName);

        Set<UserDto> result = eventTypeService.getCoordinatorsByEventTypeName(eventTypeName);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, EventTypeMessages.COORDINATORS_FOUND, HttpStatus.OK));
    }

}