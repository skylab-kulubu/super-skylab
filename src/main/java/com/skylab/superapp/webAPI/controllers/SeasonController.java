package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.SeasonService;
import com.skylab.superapp.core.constants.SeasonMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.season.CreateSeasonRequest;
import com.skylab.superapp.entities.DTOs.season.SeasonDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/seasons")
public class SeasonController {

    private final SeasonService seasonService;

    public SeasonController(SeasonService seasonService) {
        this.seasonService = seasonService;
    }

    @GetMapping("/")
    public ResponseEntity<DataResult<List<SeasonDto>>> getAllSeasons(@RequestParam(defaultValue = "false") boolean includeEvents,
                                                                     HttpServletRequest request) {
        var result = seasonService.getAllSeasons(includeEvents);

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new SuccessDataResult<>(SeasonMessages.SEASONS_NO_CONTENT,
                            HttpStatus.NO_CONTENT, request.getRequestURI()));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASONS_GET_ALL_SUCCESS,
                        HttpStatus.OK, request.getRequestURI()));

    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<SeasonDto>> getSeasonById(@PathVariable UUID id,
                                                               @RequestParam(defaultValue = "false") boolean includeEvents,
                                                               HttpServletRequest request) {
        var result = seasonService.getSeasonById(id, includeEvents);
       return ResponseEntity.status(HttpStatus.OK)
               .body(new SuccessDataResult<>(result, SeasonMessages.SEASON_GET_SUCCESS,
                       HttpStatus.OK, request.getRequestURI()));
    }



    @PostMapping("/")
    public ResponseEntity<DataResult<SeasonDto>> addSeason(@RequestBody CreateSeasonRequest createSeasonRequest,
                                                           HttpServletRequest request) {
        var result = seasonService.addSeason(createSeasonRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASON_CREATED_SUCCESSFULLY,
                        HttpStatus.CREATED, request.getRequestURI()));

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteSeason(@PathVariable UUID id, HttpServletRequest request) {
        seasonService.deleteSeason(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(SeasonMessages.SEASON_DELETED_SUCCESSFULLY, HttpStatus.OK, request.getRequestURI()));

    }


    @PostMapping("/removeEventFromSeason")
    public ResponseEntity<Result> removeEventFromSeason(@RequestParam UUID seasonId, @RequestParam UUID eventId,
                                                        HttpServletRequest request) {
        seasonService.removeEventFromSeason(seasonId, eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(SeasonMessages.EVENT_REMOVED_FROM_SEASON, HttpStatus.OK, request.getRequestURI()));
    }


    @PostMapping("/addEventToSeason")
    public ResponseEntity<Result> addEventToSeason(@RequestParam UUID seasonId, @RequestParam UUID eventId, HttpServletRequest request) {
        seasonService.addEventToSeason(seasonId, eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(SeasonMessages.EVENT_ADDED_TO_SEASON, HttpStatus.OK, request.getRequestURI()));
    }

}
