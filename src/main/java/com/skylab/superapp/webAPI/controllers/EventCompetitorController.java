package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.CompetitorService;
import com.skylab.superapp.core.constants.CompetitorMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.entities.DTOs.Competitor.CompetitorDto;
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
@RequestMapping("/api/events/{eventId}/competitors")
@RequiredArgsConstructor
@Tag(name = "Etkinlik - Yarışmacılar Alt Kaynağı", description = "Belirli bir etkinliğe bağlı yarışmacı işlemlerinin yönetimi.")
public class EventCompetitorController {

    private final CompetitorService competitorService;

    @GetMapping
    @Operation(summary = "Etkinlik Yarışmacılarını Listele", description = "Belirtilen UUID'ye sahip etkinlikteki tüm yarışmacı kayıtlarını getirir. Genel erişime açıktır.")
    public ResponseEntity<DataResult<List<CompetitorDto>>> getCompetitorsByEventId(@Parameter(description = "Etkinlik UUID") @PathVariable UUID eventId) {
        var result = competitorService.getCompetitorsByEventId(eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/winner")
    @Operation(summary = "Etkinlik Kazananını Getir", description = "Belirtilen etkinliği kazanan yarışmacı bilgisini döner. Genel erişime açıktır.")
    public ResponseEntity<DataResult<CompetitorDto>> getEventWinner(@Parameter(description = "Etkinlik UUID") @PathVariable UUID eventId) {
        var result = competitorService.getEventWinner(eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK));
    }
}