package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.TicketService;
import com.skylab.superapp.core.constants.TicketMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.entities.DTOs.ticket.response.GetTicketResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events/{eventId}/tickets")
@RequiredArgsConstructor
@Tag(name = "Etkinlik Biletleri", description = "Etkinliğe ait biletlerin listelenmesi")
public class EventTicketController {

    private final TicketService ticketService;

    @GetMapping
    @Operation(summary = "Etkinliğe Ait Biletleri Listele", description = "Belirtilen etkinliğe ait tüm biletleri getirir.")
    public ResponseEntity<DataResult<List<GetTicketResponseDto>>> getTicketsByEventId(
            @Parameter(description = "Etkinlik UUID") @PathVariable UUID eventId) {

        var result = ticketService.getTicketsByEventId(eventId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, TicketMessages.SUCCESS_GET_TICKETS_BY_EVENT_ID, HttpStatus.OK));
    }
}