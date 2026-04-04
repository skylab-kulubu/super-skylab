package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.CompetitorService;
import com.skylab.superapp.core.constants.CompetitorMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.Competitor.CompetitorDto;
import com.skylab.superapp.entities.DTOs.Competitor.CreateCompetitorRequest;
import com.skylab.superapp.entities.DTOs.Competitor.LeaderboardDto;
import com.skylab.superapp.entities.DTOs.Competitor.UpdateCompetitorRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/competitors")
@RequiredArgsConstructor
public class CompetitorController {

    private final CompetitorService competitorService;

    @PostMapping
    public ResponseEntity<DataResult<CompetitorDto>> addCompetitor(@RequestBody CreateCompetitorRequest request) {
        log.info("REST request to add new competitor for user id: {} to event id: {}", request.getUserId(), request.getEventId());
        var result = competitorService.addCompetitor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_ADD_SUCCESS, HttpStatus.CREATED));
    }

    @GetMapping
    public ResponseEntity<DataResult<List<CompetitorDto>>> getAllCompetitors() {
        log.info("REST request to get all competitors");
        var result = competitorService.getAllCompetitors();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<CompetitorDto>> getCompetitorById(@PathVariable UUID id) {
        log.info("REST request to get competitor by id: {}", id);
        var result = competitorService.getCompetitorById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResult<CompetitorDto>> updateCompetitor(@PathVariable UUID id, @RequestBody UpdateCompetitorRequest request) {
        log.info("REST request to update competitor with id: {}", id);
        var result = competitorService.updateCompetitor(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_ADD_SUCCESS, HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteCompetitor(@PathVariable UUID id) {
        log.info("REST request to delete competitor with id: {}", id);
        competitorService.deleteCompetitor(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(CompetitorMessages.COMPETITOR_DELETE_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/me")
    public ResponseEntity<DataResult<List<CompetitorDto>>> getMyCompetitors() {
        log.info("REST request to get competitors for current authenticated user");
        var result = competitorService.getMyCompetitors();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/leaderboard/type/{eventTypeName}")
    public ResponseEntity<DataResult<List<LeaderboardDto>>> getLeaderboard(@PathVariable String eventTypeName) {
        log.info("REST request to get global leaderboard for event type: {}", eventTypeName);
        List<LeaderboardDto> result = competitorService.getLeaderboardByEventType(eventTypeName);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/leaderboard/season/{seasonId}/type/{eventTypeName}")
    public ResponseEntity<DataResult<List<LeaderboardDto>>> getSeasonLeaderboard(@PathVariable UUID seasonId, @PathVariable String eventTypeName) {
        log.info("REST request to get season leaderboard for season id: {} and event type: {}", seasonId, eventTypeName);
        List<LeaderboardDto> result = competitorService.getLeaderboardBySeasonAndEventType(seasonId, eventTypeName);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK));
    }
}