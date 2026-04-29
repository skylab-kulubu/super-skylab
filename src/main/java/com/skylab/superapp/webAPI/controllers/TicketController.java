package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.TicketCheckInService;
import com.skylab.superapp.business.abstracts.TicketService;
import com.skylab.superapp.core.constants.TicketMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.ticket.response.GetTicketResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Bilet İşlemleri", description = "Kullanıcı biletlerinin sorgulanması ve Check-in onaylama işlemleri.")
public class TicketController {

    private final TicketService ticketService;
    private final TicketCheckInService ticketCheckInService;


    @GetMapping("/{ticketId}")
    @Operation(summary = "Bilet Detayını Getir", description = "Sistemdeki biletin sahiplik ve etkinlik detaylarını getirir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bilet detayları başarıyla getirildi."),
            @ApiResponse(responseCode = "404", description = "Bilet bulunamadı.", content = @Content)
    })
    public ResponseEntity<DataResult<GetTicketResponseDto>> getTicketById(@Parameter(description = "Bilet UUID") @PathVariable UUID ticketId) {
        var ticket = ticketService.getTicketById(ticketId);

        return ResponseEntity.status(HttpStatus.OK).body(new SuccessDataResult<>(ticket, TicketMessages.SUCCESS_GET_TICKET_BY_ID, HttpStatus.OK));
    }


    @PostMapping("/{ticketId}/event-days/{eventDayId}/check-in")
    @Operation(summary = "Bilet Doğrulama (Check-In)", description = "Etkinlik girişinde yetkili personel tarafından bilet doğrulama (okutma) işlemi yapar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check-in başarıyla kaydedildi."),
            @ApiResponse(responseCode = "400", description = "Geçersiz işlem (Bilet bu etkinliğe ait değil veya mükerrer okutma).", content = @Content),
            @ApiResponse(responseCode = "403", description = "Yetkisiz erişim. Sadece yetkili personel check-in yapabilir.", content = @Content)
    })
    public Result checkInToEvent(@Parameter(description = "Bilet UUID") @PathVariable UUID ticketId,
                                 @Parameter(description = "Etkinlik Günü UUID") @PathVariable UUID eventDayId) {
            ticketCheckInService.checkInToEvent(ticketId, eventDayId);
            return new SuccessResult(TicketMessages.TICKET_VALIDATED_SUCCESS, HttpStatus.OK);
    }

    @GetMapping("/me")
    @Operation(summary = "Kullanıcı Biletlerini Getir", description = "İstek atan kullanıcının sahip olduğu tüm biletleri listeler.")
    public DataResult<List<GetTicketResponseDto>> getMyTickets() {
        var tickets = ticketService.getMyTickets();
        return new SuccessDataResult<>(tickets, TicketMessages.SUCCESS_GET_MY_TICKETS, HttpStatus.OK);
    }


    @GetMapping("/user/{userId}/event/{eventId}")
    @Operation(summary = "Kullanıcı ve Etkinliğe Göre Bilet Getir")
    public ResponseEntity<DataResult<GetTicketResponseDto>> getTicketByUserIdAndEventId(
            @PathVariable UUID userId,
            @PathVariable UUID eventId) {
        var ticket = ticketService.getTicketByUserIdAndEventId(userId, eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(ticket, TicketMessages.SUCCESS_GET_TICKET_BY_ID, HttpStatus.OK));
    }

    @GetMapping
    @Operation(summary = "Biletleri Filtrele", description = "E-posta veya kullanıcı ID'sine göre bilet listeler.")
    public ResponseEntity<DataResult<List<GetTicketResponseDto>>> getTickets(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UUID userId) {
        List<GetTicketResponseDto> result;
        if (email != null && !email.isBlank()) {
            result = ticketService.getTicketsByUserEmail(email);
        } else if (userId != null) {
            result = ticketService.getTicketsByUserId(userId);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, TicketMessages.SUCCESS_GET_TICKET_BY_ID, HttpStatus.OK));
    }




}
