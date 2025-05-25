package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.SeasonService;
import com.skylab.superapp.entities.DTOs.Season.CreateSeasonDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seasons")
public class SeasonController {

    private final SeasonService seasonService;

    public SeasonController(SeasonService seasonService) {
        this.seasonService = seasonService;
    }

    @PostMapping("/addSeason")
    public ResponseEntity<?> addSeason(@RequestBody CreateSeasonDto createSeasonDto) {
        var result = seasonService.addSeason(createSeasonDto);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/deleteSeason")
    public ResponseEntity<?> deleteSeason(@RequestParam int id) {
        var result = seasonService.deleteSeason(id);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }


    @GetMapping("/getAllSeasonsByTenant")
    public ResponseEntity<?> getAllSeasonsByTenant(@RequestParam String tenant) {
        var result = seasonService.getAllSeasonsByTenant(tenant);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getAllSeasons")
    public ResponseEntity<?> getAllSeasons() {
        var result = seasonService.getAllSeasons();
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getSeasonByName")
    public ResponseEntity<?> getSeasonByName(@RequestParam String name) {
        var result = seasonService.getSeasonByName(name);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }


    @GetMapping("/getSeasonById")
    public ResponseEntity<?> getSeasonById(@RequestParam int id) {
        var result = seasonService.getSeasonById(id);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/addEventToSeason")
    public ResponseEntity<?> addEventToSeason(@RequestParam int seasonId, @RequestParam int eventId) {
        var result = seasonService.addEventToSeason(seasonId, eventId);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/removeEventFromSeason")
    public ResponseEntity<?> removeEventFromSeason(@RequestParam int seasonId, @RequestParam int eventId) {
        var result = seasonService.removeEventFromSeason(seasonId, eventId);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }


}
