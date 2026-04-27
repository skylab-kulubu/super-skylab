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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Bilet İşlemleri", description = "Kullanıcı biletlerinin sorgulanması ve Check-in onaylama işlemleri.")
public class TicketController {

    private final TicketService ticketService;
    private final TicketCheckInService ticketCheckInService;


    public TicketController(TicketService ticketService, TicketCheckInService ticketCheckInService) {
        this.ticketService = ticketService;
        this.ticketCheckInService = ticketCheckInService;
    }

    @GetMapping("/{ticketId}")
    @PreAuthorize("hasAnyRole('tickets.get', 'tickets.moderator')")
    @Operation(summary = "Bilet Detayını Getir", description = "Sistemdeki biletin sahiplik ve etkinlik detaylarını getirir.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bilet detayları başarıyla getirildi."),
            @ApiResponse(responseCode = "404", description = "Bilet bulunamadı.", content = @Content)
    })
    public ResponseEntity<DataResult<GetTicketResponseDto>> getTicketById(@Parameter(description = "Bilet UUID") @PathVariable UUID ticketId) {
        var ticket = ticketService.getTicketById(ticketId);

        return ResponseEntity.status(HttpStatus.OK).body(new SuccessDataResult<>(ticket, TicketMessages.SUCCESS_GET_TICKET_BY_ID, HttpStatus.OK));
    }


    @PostMapping("/{ticketId}/event-days/{eventDayId}/check-in")
    @PreAuthorize("hasAnyRole('tickets.validator', 'tickets.moderator')")
    @Operation(summary = "Bilet Doğrulama (Check-In)", description = "Etkinlik girişinde yetkili personel tarafından bilet doğrulama (okutma) işlemi yapar.", security = @SecurityRequirement(name = "bearerAuth"))
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
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Kullanıcı Biletlerini Getir", description = "İstek atan kullanıcının sahip olduğu tüm biletleri listeler.", security = @SecurityRequirement(name = "bearerAuth"))
    public DataResult<List<GetTicketResponseDto>> getMyTickets() {
        var tickets = ticketService.getMyTickets();
        return new SuccessDataResult<>(tickets, TicketMessages.SUCCESS_GET_MY_TICKETS, HttpStatus.OK);
    }




}
