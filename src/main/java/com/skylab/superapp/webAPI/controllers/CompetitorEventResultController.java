package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.CompetitorEventResultService;
import com.skylab.superapp.entities.DTOs.CompetitorEventResult.CreateCompetitorEventResultDto;
import com.skylab.superapp.entities.DTOs.CompetitorEventResult.GetCompetitorEventResultDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/competitorEventResults")
public class CompetitorEventResultController {
    private final CompetitorEventResultService competitorEventResultService;

    public CompetitorEventResultController(CompetitorEventResultService competitorEventResultService) {
        this.competitorEventResultService = competitorEventResultService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCompetitorEventResult(@RequestBody CreateCompetitorEventResultDto createCompetitorEventResultDto) {
        var result = competitorEventResultService.addCompetitorEventResult(createCompetitorEventResultDto);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteCompetitorEventResult(@RequestParam Long id) {
        var result = competitorEventResultService.deleteCompetitorEventResult(id);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateCompetitorEventResult(@RequestBody GetCompetitorEventResultDto getCompetitorEventResultDto) {
        var result = competitorEventResultService.updateCompetitorEventResult(getCompetitorEventResultDto);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getByCompetitorIdAndEventId")
    public ResponseEntity<?> getByCompetitorIdAndEventId(@RequestParam String competitorId,@RequestParam int eventId) {
        var result = competitorEventResultService.getByCompetitorIdAndEventId(competitorId, eventId);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getAllByCompetitorId")
    public ResponseEntity<?> getAllByCompetitorIdWithEvent(@RequestParam String competitorId) {
        var result = competitorEventResultService.getAllByCompetitorIdWithEvent(competitorId);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getAllByEventId")
    public ResponseEntity<?> getAllByEventId(@RequestParam int eventId) {
        var result = competitorEventResultService.getAllByEventId(eventId);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }



}
