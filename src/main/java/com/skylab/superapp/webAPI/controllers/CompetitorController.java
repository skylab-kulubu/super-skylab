package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.CompetitorService;
import com.skylab.superapp.core.constants.CompetitorMessages;
import com.skylab.superapp.core.mappers.CompetitorMapper;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.entities.DTOs.Competitor.AddPointsToUserDto;
import com.skylab.superapp.entities.DTOs.Competitor.CreateCompetitorDto;
import com.skylab.superapp.entities.DTOs.Competitor.GetCompetitorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitors")
public class CompetitorController {

    private final CompetitorService competitorService;
    private final CompetitorMapper competitorMapper;

    public CompetitorController(CompetitorService competitorService, CompetitorMapper competitorMapper) {
        this.competitorService = competitorService;
        this.competitorMapper = competitorMapper;
    }


    @PostMapping("/")
    public ResponseEntity<DataResult<GetCompetitorDto>> addCompetitor(@RequestBody CreateCompetitorDto createCompetitorDto, HttpServletRequest request) {
        var result = competitorService.addCompetitor(createCompetitorDto);
        var dtos = competitorMapper.toDto(result);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(dtos, CompetitorMessages.COMPETITOR_ADD_SUCCESS, HttpStatus.CREATED, request.getRequestURI()));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteCompetitor(@PathVariable int id, HttpServletRequest request) {
        competitorService.deleteCompetitor(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Result(true, CompetitorMessages.COMPETITOR_DELETE_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/my")
    public ResponseEntity<DataResult<List<GetCompetitorDto>>> getMyCompetitors(HttpServletRequest request) {
        var result = competitorService.getMyCompetitors();
        var dtos = competitorMapper.toDtoList(result);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(dtos, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<DataResult<List<GetCompetitorDto>>> getCompetitorsByUserId(@PathVariable int userId, HttpServletRequest request) {
        var result = competitorService.getCompetitorsByUserId(userId);
        var dtos = competitorMapper.toDtoList(result);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(dtos, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }


    @GetMapping("/event/{eventId}")
    public ResponseEntity<DataResult<List<GetCompetitorDto>>> getCompetitorsByEventId(@PathVariable int eventId, HttpServletRequest request) {
        var result = competitorService.getCompetitorsByEventId(eventId);
        var dtos = competitorMapper.toDtoList(result);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(dtos, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @PutMapping("{id}/setPoints")
    public ResponseEntity<Result> setCompetitorPoints(@PathVariable int id, @RequestParam double points, HttpServletRequest request) {
        competitorService.setCompetitorPoints(id, points);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Result(true, CompetitorMessages.COMPETITOR_POINTS_SET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @PutMapping("{id}/setWinner")
    public ResponseEntity<Result> setCompetitorWinner(@PathVariable int id, @RequestParam boolean isWinner, HttpServletRequest request) {
        competitorService.setCompetitorWinner(id, isWinner);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Result(true, CompetitorMessages.COMPETITOR_WINNER_SET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }


    @GetMapping("/leaderboard/{competitionId}")
    public ResponseEntity<DataResult<List<GetCompetitorDto>>> getLeaderboard(@PathVariable int competitionId, HttpServletRequest request) {
        var result = competitorService.getCompetitionLeaderboard(competitionId);
        var dtos = competitorMapper.toDtoList(result);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(dtos, CompetitorMessages.COMPETITOR_LEADERBOARD_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

}