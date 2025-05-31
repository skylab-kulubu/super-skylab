package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.CompetitorService;
import com.skylab.superapp.entities.DTOs.Competitor.AddPointsToUserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/competitors")
public class CompetitorController {

    private final CompetitorService competitorService;

    public CompetitorController(CompetitorService competitorService) {
        this.competitorService = competitorService;
    }

    @GetMapping("/getAllCompetitors")
    public ResponseEntity<?> getAllCompetitors() {
        var result = competitorService.getAllCompetitors();
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getAllCompetitorsByEventType")
    public ResponseEntity<?> getAllCompetitorsByEventType(@RequestParam String eventTypeName) {
        var result = competitorService.getAllCompetitorsByEventType(eventTypeName);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getAllBySeasonId")
    public ResponseEntity<?> getAllBySeasonId(@RequestParam int seasonId) {
        var result = competitorService.getAllBySeasonId(seasonId);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/addPointsToUser")
    public ResponseEntity<?> addPointsToUser(@RequestBody AddPointsToUserDto addPointsToUserDto) {
        var result = competitorService.addPointsToUser(
                addPointsToUserDto.getUserId(),
                addPointsToUserDto.getEventId(),
                addPointsToUserDto.getPoints(),
                addPointsToUserDto.isWinner(),
                addPointsToUserDto.getAward()
        );
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getWeeklyWinner")
    public ResponseEntity<?> getWeeklyWinner(@RequestParam int eventId) {
        var result = competitorService.getWeeklyWinner(eventId);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getSeasonWinner")
    public ResponseEntity<?> getSeasonWinner(@RequestParam int seasonId) {
        var result = competitorService.getSeasonWinner(seasonId);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getCompetitorById")
    public ResponseEntity<?> getCompetitorById(@RequestParam int id) {
        var result = competitorService.getCompetitorEntityById(id);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }
}