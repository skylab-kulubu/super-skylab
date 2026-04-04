package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.SeasonService;
import com.skylab.superapp.core.constants.SeasonMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.season.CreateSeasonRequest;
import com.skylab.superapp.entities.DTOs.season.SeasonDto;
import com.skylab.superapp.entities.DTOs.season.UpdateSeasonRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/seasons")
@RequiredArgsConstructor
public class SeasonController {

    private final SeasonService seasonService;

    @GetMapping
    public ResponseEntity<DataResult<List<SeasonDto>>> getAllSeasons() {
        log.info("REST request to get all seasons");
        var result = seasonService.getAllSeasons();

        if (result.isEmpty()) {
            log.debug("No seasons found, returning NO_CONTENT");
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new SuccessDataResult<>(SeasonMessages.SEASONS_NO_CONTENT, HttpStatus.NO_CONTENT));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASONS_GET_ALL_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<SeasonDto>> getSeasonById(@PathVariable UUID id) {
        log.info("REST request to get season by id: {}", id);
        var result = seasonService.getSeasonById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASON_GET_SUCCESS, HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<DataResult<SeasonDto>> addSeason(@RequestBody CreateSeasonRequest request) {
        log.info("REST request to add new season with name: {}", request.getName());
        var result = seasonService.addSeason(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASON_CREATED_SUCCESSFULLY, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResult<SeasonDto>> updateSeason(@PathVariable UUID id, @RequestBody UpdateSeasonRequest request) {
        log.info("REST request to update season with id: {}", id);
        var result = seasonService.updateSeason(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASON_UPDATED_SUCCESSFULLY, HttpStatus.OK));
    }

    @GetMapping("/active")
    public ResponseEntity<DataResult<List<SeasonDto>>> getActiveSeasons() {
        log.info("REST request to get all active seasons");
        var result = seasonService.getActiveSeasons();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASONS_GET_ALL_SUCCESS, HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteSeason(@PathVariable UUID id) {
        log.info("REST request to delete season with id: {}", id);
        seasonService.deleteSeason(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(SeasonMessages.SEASON_DELETED_SUCCESSFULLY, HttpStatus.OK));
    }

    @PostMapping("/{seasonId}/events/{eventId}")
    public ResponseEntity<Result> addEventToSeason(@PathVariable UUID seasonId, @PathVariable UUID eventId) {
        log.info("REST request to add event id: {} to season id: {}", eventId, seasonId);
        seasonService.addEventToSeason(seasonId, eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(SeasonMessages.EVENT_ADDED_TO_SEASON, HttpStatus.OK));
    }

    @DeleteMapping("/{seasonId}/events/{eventId}")
    public ResponseEntity<Result> removeEventFromSeason(@PathVariable UUID seasonId, @PathVariable UUID eventId) {
        log.info("REST request to remove event id: {} from season id: {}", eventId, seasonId);
        seasonService.removeEventFromSeason(seasonId, eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(SeasonMessages.EVENT_REMOVED_FROM_SEASON, HttpStatus.OK));
    }
}