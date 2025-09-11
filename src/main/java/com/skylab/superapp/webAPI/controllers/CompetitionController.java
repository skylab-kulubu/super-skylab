package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.CompetitionService;
import com.skylab.superapp.core.constants.CompetitionMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.competition.CompetitionDto;
import com.skylab.superapp.entities.DTOs.competition.CreateCompetitionRequest;
import com.skylab.superapp.entities.DTOs.competition.UpdateCompetitionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {

    private final CompetitionService competitionService;

    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }


    @GetMapping("/")
    public ResponseEntity<DataResult<List<CompetitionDto>>> getAllCompetitions(@RequestParam(defaultValue = "false") boolean includeEvent,
                                                                               @RequestParam(defaultValue = "false") boolean includeEventType){
        var competitions = competitionService.getAllCompetitions(includeEvent, includeEventType);

        if (competitions.isEmpty() ){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new SuccessDataResult<>(CompetitionMessages.COMPETITIONS_NO_CONTENT, HttpStatus.NO_CONTENT));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(competitions, CompetitionMessages.COMPETITION_GET_LIST_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<CompetitionDto>> getCompetitionById(@PathVariable UUID id,
                                                                         @RequestParam(defaultValue = "false") boolean includeEvent,
                                                                         @RequestParam(defaultValue = "false") boolean includeEventType) {
        var result = competitionService.getCompetitionById(id, includeEvent, includeEventType);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitionMessages.COMPETITION_GET_SUCCESS, HttpStatus.OK));
    }

    @PostMapping("/")
    public ResponseEntity<DataResult<CompetitionDto>> addCompetition(@RequestBody CreateCompetitionRequest createCompetitionDto) {
        var result = competitionService.addCompetition(createCompetitionDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(result, CompetitionMessages.COMPETITION_ADD_SUCCESS, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResult<CompetitionDto>> updateCompetition(@RequestBody UpdateCompetitionRequest updateCompetitionRequest,
                                                    @PathVariable UUID id) {
        var competition = competitionService.updateCompetition(updateCompetitionRequest, id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(competition, CompetitionMessages.COMPETITION_UPDATE_SUCCESS,
                        HttpStatus.CREATED));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteCompetition(@PathVariable UUID id) {
        competitionService.deleteCompetition(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(CompetitionMessages.COMPETITION_DELETE_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/active")
    public ResponseEntity<DataResult<List<CompetitionDto>>> getActiveCompetitions(@RequestParam(defaultValue = "false") boolean includeEvent,
                                                                                  @RequestParam(defaultValue = "false") boolean includeEventType) {
        var competitions = competitionService.getActiveCompetitions(includeEvent, includeEventType);

        if (competitions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new SuccessDataResult<>(CompetitionMessages.COMPETITIONS_NO_CONTENT, HttpStatus.NO_CONTENT));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(competitions, CompetitionMessages.COMPETITION_GET_LIST_SUCCESS, HttpStatus.OK));
    }


}
