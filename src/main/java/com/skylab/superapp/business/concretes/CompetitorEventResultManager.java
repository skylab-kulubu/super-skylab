package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.CompetitorEventResultService;
import com.skylab.superapp.business.abstracts.CompetitorService;
import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.business.constants.CREMessages;
import com.skylab.superapp.core.results.*;
import com.skylab.superapp.dataAccess.CompetitorEventResultDao;
import com.skylab.superapp.entities.CompetitorEventResult;
import com.skylab.superapp.entities.DTOs.CompetitorEventResult.CreateCompetitorEventResultDto;
import com.skylab.superapp.entities.DTOs.CompetitorEventResult.GetCompetitorEventResultDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompetitorEventResultManager implements CompetitorEventResultService {

    private CompetitorEventResultDao competitorEventResultDao;
    private CompetitorService competitorService;
    private EventService eventService;

    public CompetitorEventResultManager(CompetitorEventResultDao competitorEventResultDao, @Lazy CompetitorService competitorService, @Lazy EventService eventService) {
        this.competitorEventResultDao = competitorEventResultDao;
        this.competitorService = competitorService;
        this.eventService = eventService;
    }


    @Override
    public DataResult<Long> addCompetitorEventResult(CreateCompetitorEventResultDto createCompetitorEventResultDto) {
        var competitor = competitorService.getCompetitorEntityById(createCompetitorEventResultDto.getCompetitorId());
        if(!competitor.isSuccess()){
            return new ErrorDataResult<>(competitor.getMessage(), competitor.getHttpStatus());
        }

        var eventResult = eventService.getEventEntityById(createCompetitorEventResultDto.getEventId());
        if(!eventResult.isSuccess()){
            return new ErrorDataResult<>(eventResult.getMessage(), eventResult.getHttpStatus());
        }


        if(competitorEventResultDao.existsByCompetitorIdAndEventId(createCompetitorEventResultDto.getCompetitorId(), createCompetitorEventResultDto.getEventId())){
            return new ErrorDataResult<>(CREMessages.competitorEventResultAlreadyExists, HttpStatus.BAD_REQUEST);
        }

        CompetitorEventResult competitorEventResult = CompetitorEventResult.builder()
                .competitor(competitor.getData())
                .event(eventResult.getData())
                .points(createCompetitorEventResultDto.getPoints())
                .build();


        competitorEventResultDao.save(competitorEventResult);
        return new SuccessDataResult<>(competitorEventResult.getId(), CREMessages.competitorEventResultAdded, HttpStatus.CREATED);
    }

    @Override
    public Result deleteCompetitorEventResult(Long id) {
        var compe = competitorEventResultDao.findById(id);

        if (compe.isEmpty()) {
            return new ErrorResult(CREMessages.competitorEventResultNotFound, HttpStatus.NOT_FOUND);
        }

        competitorEventResultDao.delete(compe.get());
        return new SuccessResult(CREMessages.competitorEventResultDeleted, HttpStatus.OK);
    }

    @Override
    public Result updateCompetitorEventResult(GetCompetitorEventResultDto getCompetitorEventResultDto) {
        var competitorEventResult = competitorEventResultDao.findById(getCompetitorEventResultDto.getId());
        if (competitorEventResult.isEmpty()) {
            return new ErrorResult(CREMessages.competitorEventResultNotFound, HttpStatus.NOT_FOUND);
        }

        competitorEventResult.get().setPoints(getCompetitorEventResultDto.getPoints());
        competitorEventResultDao.save(competitorEventResult.get());
        return new SuccessResult(CREMessages.competitorEventResultUpdated, HttpStatus.OK);
    }

    @Override
    public DataResult<GetCompetitorEventResultDto> getByCompetitorIdAndEventId(String competitorId, int eventId) {
        var competitorEventResult = competitorEventResultDao.findByCompetitorIdAndEventId(competitorId, eventId);
        if(competitorEventResult == null){
            return new ErrorDataResult<>(CREMessages.competitorEventResultNotFound, HttpStatus.NOT_FOUND);
        }

        var returnDto = new GetCompetitorEventResultDto(competitorEventResult);
        return new SuccessDataResult<>(returnDto, CREMessages.competitorEventResultListed, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetCompetitorEventResultDto>> getAllByCompetitorIdWithEvent(String competitorId) {
        var competitorEventResults = competitorEventResultDao.findAllByCompetitorIdWithEvent(competitorId);
        if (competitorEventResults.isEmpty()) {
            return new ErrorDataResult<>(CREMessages.competitorEventResultsNotFound, HttpStatus.NOT_FOUND);
        }

        var returnDtos = GetCompetitorEventResultDto.buildListGetCompetitorEventResultDto(competitorEventResults);
        return new SuccessDataResult<>(returnDtos, CREMessages.competitorEventResultsListed, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetCompetitorEventResultDto>> getAllByEventId(int eventId) {
        var competitorEventResults = competitorEventResultDao.findAllByEventId(eventId);
        if (competitorEventResults.isEmpty()) {
            return new ErrorDataResult<>(CREMessages.competitorEventResultsNotFound, HttpStatus.NOT_FOUND);
        }

        var returnDtos = GetCompetitorEventResultDto.buildListGetCompetitorEventResultDto(competitorEventResults);
        return new SuccessDataResult<>(returnDtos, CREMessages.competitorEventResultsListed, HttpStatus.OK);
    }
}
