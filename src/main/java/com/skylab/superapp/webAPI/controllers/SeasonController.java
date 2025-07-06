package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.SeasonService;
import com.skylab.superapp.core.constants.SeasonMessages;
import com.skylab.superapp.core.mappers.SeasonMapper;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.Season.CreateSeasonDto;
import com.skylab.superapp.entities.DTOs.Season.GetSeasonDto;
import com.skylab.superapp.entities.Season;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seasons")
public class SeasonController {

    private final SeasonService seasonService;
    private final SeasonMapper seasonMapper;

    public SeasonController(SeasonService seasonService, SeasonMapper seasonMapper) {
        this.seasonService = seasonService;
        this.seasonMapper = seasonMapper;
    }

    @GetMapping("/")
    public ResponseEntity<DataResult<List<GetSeasonDto>>> getAllSeasons(HttpServletRequest request) {
        var result = seasonService.getAllSeasons();

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new SuccessDataResult<>(SeasonMessages.SEASONS_NO_CONTENT, HttpStatus.NO_CONTENT, request.getRequestURI()));
        }
        var dtos = seasonMapper.toDtoList(result);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(dtos, SeasonMessages.SEASONS_GET_ALL_SUCCESS, HttpStatus.OK, request.getRequestURI()));

    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<GetSeasonDto>> getSeasonById(@PathVariable int id, HttpServletRequest request) {
        var result = seasonService.getSeasonById(id);
        var dto = seasonMapper.toDto(result);
       return ResponseEntity.status(HttpStatus.OK)
               .body(new SuccessDataResult<>(dto, SeasonMessages.SEASON_GET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }



    @PostMapping("/")
    public ResponseEntity<DataResult<GetSeasonDto>> addSeason(@RequestBody CreateSeasonDto createSeasonDto, HttpServletRequest request) {
        var result = seasonService.addSeason(createSeasonDto);
        var dto = seasonMapper.toDto(result);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(dto, SeasonMessages.SEASON_CREATED_SUCCESSFULLY, HttpStatus.CREATED, request.getRequestURI()));

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteSeason(@PathVariable int id, HttpServletRequest request) {
        seasonService.deleteSeason(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(SeasonMessages.SEASON_DELETED_SUCCESSFULLY, HttpStatus.OK, request.getRequestURI()));

    }


    @PostMapping("/removeEventFromSeason")
    public ResponseEntity<Result> removeEventFromSeason(@RequestParam int seasonId, @RequestParam int eventId, HttpServletRequest request) {
        seasonService.removeEventFromSeason(seasonId, eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(SeasonMessages.EVENT_REMOVED_FROM_SEASON, HttpStatus.OK, request.getRequestURI()));
    }


    @PostMapping("/addEventToSeason")
    public ResponseEntity<Result> addEventToSeason(@RequestParam int seasonId, @RequestParam int eventId, HttpServletRequest request) {
        seasonService.addEventToSeason(seasonId, eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(SeasonMessages.EVENT_ADDED_TO_SEASON, HttpStatus.OK, request.getRequestURI()));
    }






}
