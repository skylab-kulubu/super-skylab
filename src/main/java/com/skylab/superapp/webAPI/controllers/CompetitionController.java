package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.CompetitionService;
import com.skylab.superapp.core.constants.CompetitionMessages;
import com.skylab.superapp.core.mappers.CompetitionMapper;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.competition.CreateCompetitionDto;
import com.skylab.superapp.entities.DTOs.competition.GetCompetitionDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {

    private final CompetitionService competitionService;
    private final CompetitionMapper competitionMapper;

    public CompetitionController(CompetitionService competitionService, CompetitionMapper competitionMapper) {
        this.competitionService = competitionService;
        this.competitionMapper = competitionMapper;
    }



    @GetMapping("/")
    public ResponseEntity<DataResult<List<GetCompetitionDto>>> getAllCompetitions(HttpServletRequest request){
        var competitions = competitionService.getAllCompetitions();
        var dtos = competitionMapper.toDtoList(competitions);

        if (dtos.isEmpty() ){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new SuccessDataResult<>(CompetitionMessages.COMPETITIONS_NO_CONTENT, HttpStatus.NO_CONTENT, request.getRequestURI()));
        }


        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(dtos, CompetitionMessages.COMPETITION_GET_LIST_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<GetCompetitionDto>> getCompetitionById(@PathVariable int id, HttpServletRequest request) {
        var result = competitionService.getCompetitionById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(competitionMapper.toDto(result), CompetitionMessages.COMPETITION_GET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @PostMapping("/")
    public ResponseEntity<DataResult<GetCompetitionDto>> addCompetition(@RequestBody CreateCompetitionDto createCompetitionDto, HttpServletRequest request) {
        var result = competitionService.addCompetition(createCompetitionDto);
        var dto = competitionMapper.toDto(result);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(dto, CompetitionMessages.COMPETITION_ADD_SUCCESS, HttpStatus.CREATED, request.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result> updateCompetition(@RequestBody CreateCompetitionDto createCompetitionDto, @PathVariable int id, HttpServletRequest request) {
        competitionService.updateCompetition(createCompetitionDto, id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResult(CompetitionMessages.COMPETITION_UPDATE_SUCCESS, HttpStatus.CREATED, request.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteCompetition(@PathVariable int id, HttpServletRequest request) {
        competitionService.deleteCompetition(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(CompetitionMessages.COMPETITION_DELETE_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/active")
    public ResponseEntity<DataResult<List<GetCompetitionDto>>> getActiveCompetitions(HttpServletRequest request) {
        var competitions = competitionService.getActiveCompetitions();
        var dtos = competitionMapper.toDtoList(competitions);

        if (dtos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new SuccessDataResult<>(CompetitionMessages.COMPETITIONS_NO_CONTENT, HttpStatus.NO_CONTENT, request.getRequestURI()));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(dtos, CompetitionMessages.COMPETITION_GET_LIST_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }


}
