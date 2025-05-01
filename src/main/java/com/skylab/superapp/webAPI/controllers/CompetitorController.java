package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.CompetitorService;
import com.skylab.superapp.entities.DTOs.Competitor.CreateCompetitorDto;
import com.skylab.superapp.entities.DTOs.Competitor.GetCompetitorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/competitors")
public class CompetitorController {

    private final CompetitorService competitorService;

    public CompetitorController(CompetitorService competitorService) {
        this.competitorService = competitorService;
    }

    @PostMapping("/addCompetitor")
    public ResponseEntity<?> addCompetitor(@RequestBody CreateCompetitorDto createCompetitorDto) {
        var result = competitorService.addCompetitor(createCompetitorDto);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/deleteCompetitor")
    public ResponseEntity<?> deleteCompetitor(@RequestParam String id) {
        var result = competitorService.deleteCompetitor(id);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/updateCompetitor")
    public ResponseEntity<?> updateCompetitor(@RequestBody GetCompetitorDto getCompetitorDto) {
        var result = competitorService.updateCompetitor(getCompetitorDto);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/addPointsToCompetitor")
    public ResponseEntity<?> addPointsToCompetitor(@RequestParam String id, @RequestParam double points) {
        var result = competitorService.addPointsToCompetitor(id, points);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getAllCompetitors")
    public ResponseEntity<?> getAllCompetitors() {
        var result = competitorService.getAllCompetitors();
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getAllCompetitorsByTenant")
    public ResponseEntity<?> getAllCompetitorsByTenant(@RequestParam String tenant) {
        var result = competitorService.getAllCompetitorsByTenant(tenant);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getAllBySeasonId")
    public ResponseEntity<?> getAllBySeasonId(@RequestParam int seasonId) {
        var result = competitorService.getAllBySeasonId(seasonId);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }




}
