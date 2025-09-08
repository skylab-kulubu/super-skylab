package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.CompetitorService;
import com.skylab.superapp.core.constants.CompetitorMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.Competitor.CompetitorDto;
import com.skylab.superapp.entities.DTOs.Competitor.CreateCompetitorRequest;
import com.skylab.superapp.entities.DTOs.Competitor.UpdateCompetitorRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/competitors")
public class CompetitorController {

    private final CompetitorService competitorService;

    public CompetitorController(CompetitorService competitorService) {
        this.competitorService = competitorService;
    }

    @PostMapping("/")
    public ResponseEntity<DataResult<CompetitorDto>> addCompetitor(@RequestBody CreateCompetitorRequest createCompetitorRequest,
                                                                   HttpServletRequest request) {
        var result = competitorService.addCompetitor(createCompetitorRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_ADD_SUCCESS,
                        HttpStatus.CREATED, request.getRequestURI()));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteCompetitor(@PathVariable UUID id, HttpServletRequest request) {
        competitorService.deleteCompetitor(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(CompetitorMessages.COMPETITOR_DELETE_SUCCESS,
                        HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/my")
    public ResponseEntity<DataResult<List<CompetitorDto>>> getMyCompetitors(@RequestParam(defaultValue = "false") boolean includeUser,
                                                                            @RequestParam(defaultValue = "false") boolean includeEvent,
                                                                            HttpServletRequest request) {
        var result = competitorService.getMyCompetitors(includeUser, includeEvent);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResult<CompetitorDto>> updateCompetitor(@PathVariable UUID id,
                                                                       @RequestBody UpdateCompetitorRequest updateCompetitorRequest,
                                                                       HttpServletRequest request) {
        var result = competitorService.updateCompetitor(id, updateCompetitorRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_ADD_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<DataResult<List<CompetitorDto>>> getCompetitorsByUserId(@PathVariable UUID userId,
                                                                                  @RequestParam(defaultValue = "false") boolean includeUser,
                                                                                  @RequestParam(defaultValue = "false") boolean includeEvent,
                                                                                  HttpServletRequest request) {
        var result = competitorService.getCompetitorsByUserId(userId, includeUser, includeEvent);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }


    @GetMapping("/event/{eventId}")
    public ResponseEntity<DataResult<List<CompetitorDto>>> getCompetitorsByEventId(@PathVariable UUID eventId,
                                                                                   @RequestParam(defaultValue = "false") boolean includeUser,
                                                                                   @RequestParam(defaultValue = "false") boolean includeEvent,
                                                                                   HttpServletRequest request) {
        var result = competitorService.getCompetitorsByEventId(eventId, includeUser, includeEvent);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }


    @GetMapping("/leaderboard/{competitionId}")
    public ResponseEntity<DataResult<List<CompetitorDto>>> getLeaderboard(@PathVariable UUID competitionId,
                                                                          @RequestParam(defaultValue = "false") boolean includeUser,
                                                                          @RequestParam(defaultValue = "false") boolean includeEvent,
                                                                          HttpServletRequest request) {
        var result = competitorService.getCompetitionLeaderboard(competitionId, includeUser, includeEvent);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_LEADERBOARD_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

}