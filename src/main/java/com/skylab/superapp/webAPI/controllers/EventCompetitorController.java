package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.CompetitorService;
import com.skylab.superapp.core.constants.CompetitorMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.entities.DTOs.Competitor.CompetitorDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/events/{eventId}/competitors")
@RequiredArgsConstructor
public class EventCompetitorController {

    private final CompetitorService competitorService;

    @GetMapping
    public ResponseEntity<DataResult<List<CompetitorDto>>> getCompetitorsByEventId(@PathVariable UUID eventId) {
        log.info("REST request to get competitors for event id: {}", eventId);
        var result = competitorService.getCompetitorsByEventId(eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/winner")
    public ResponseEntity<DataResult<CompetitorDto>> getEventWinner(@PathVariable UUID eventId) {
        log.info("REST request to get the winner for event id: {}", eventId);
        var result = competitorService.getEventWinner(eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK));
    }
}