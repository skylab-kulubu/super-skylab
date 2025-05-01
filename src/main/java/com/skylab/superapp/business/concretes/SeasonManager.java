package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.CompetitorService;
import com.skylab.superapp.business.abstracts.SeasonService;
import com.skylab.superapp.business.constants.SeasonMessages;
import com.skylab.superapp.core.results.*;
import com.skylab.superapp.dataAccess.SeasonDao;
import com.skylab.superapp.entities.DTOs.Season.CreateSeasonDto;
import com.skylab.superapp.entities.DTOs.Season.GetSeasonDto;
import com.skylab.superapp.entities.Season;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeasonManager implements SeasonService {

    private SeasonDao seasonDao;
    private CompetitorService competitorService;

    public SeasonManager(SeasonDao seasonDao, @Lazy CompetitorService competitorService) {
        this.seasonDao = seasonDao;
        this.competitorService = competitorService;
    }

    @Override
    public Result addSeason(CreateSeasonDto createSeasonDto) {

        if(createSeasonDto.getName() == null || createSeasonDto.getName().isEmpty()) {
            return new ErrorResult(SeasonMessages.NameCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        if(seasonDao.existsByName(createSeasonDto.getName())) {
            return new ErrorResult(SeasonMessages.NameAlreadyExists, HttpStatus.BAD_REQUEST);
        }

        Season season = Season.builder()
                .name(createSeasonDto.getName())
                .startDate(createSeasonDto.getStartDate())
                .endDate(createSeasonDto.getEndDate())
                .isActive(createSeasonDto.isActive())
                .tenant(createSeasonDto.getTenant())
                .build();

        seasonDao.save(season);
        return new SuccessResult(SeasonMessages.SeasonAddedSuccess, HttpStatus.CREATED);
    }

    @Override
    public Result deleteSeason(int id) {

        var season = seasonDao.findById(id);
        if(season == null) {
            return new ErrorResult(SeasonMessages.SeasonNotFound, HttpStatus.NOT_FOUND);
        }

        seasonDao.delete(season);
        return new SuccessResult(SeasonMessages.SeasonDeletedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetSeasonDto>> getAllSeasonsByTenant(String tenant) {
        if(tenant == null || tenant.isEmpty()) {
            return new ErrorDataResult<>(SeasonMessages.TenantCannotBeNull, HttpStatus.BAD_REQUEST);
        }
        var result = seasonDao.findAllByTenant(tenant);
        if(result.isEmpty()) {
            return new ErrorDataResult<>(SeasonMessages.SeasonNotFound, HttpStatus.NOT_FOUND);
        }
        var returnSeasons = GetSeasonDto.buildListGetSeasonDto(result);
        return new SuccessDataResult<>(returnSeasons, SeasonMessages.SeasonListedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetSeasonDto>> getAllSeasons() {
        var result = seasonDao.findAll();
        if(result.isEmpty()) {
            return new ErrorDataResult<>(SeasonMessages.SeasonNotFound, HttpStatus.NOT_FOUND);
        }

        var returnSeasons = GetSeasonDto.buildListGetSeasonDto(result);
        return new SuccessDataResult<>(returnSeasons, SeasonMessages.SeasonListedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<GetSeasonDto> getSeasonByName(String name) {
        if(name == null || name.isEmpty()) {
            return new ErrorDataResult<>(SeasonMessages.NameCannotBeNull, HttpStatus.BAD_REQUEST);
        }
        var result = seasonDao.findByName(name);
        if(result == null) {
            return new ErrorDataResult<>(SeasonMessages.SeasonNotFound, HttpStatus.NOT_FOUND);
        }

        var returnSeason = new GetSeasonDto(result);
        return new SuccessDataResult<>(returnSeason, SeasonMessages.SeasonListedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<Season> getSeasonEntityById(int id) {
        var result = seasonDao.findById(id);
        if(result == null) {
            return new ErrorDataResult<>(SeasonMessages.SeasonNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<>(result, SeasonMessages.SeasonListedSuccess, HttpStatus.OK);
    }

    @Override
    public Result addCompetitorToSeason(int seasonId, String competitorId) {
        var season = seasonDao.findById(seasonId);
        if(season == null) {
            return new ErrorResult(SeasonMessages.SeasonNotFound, HttpStatus.NOT_FOUND);
        }

        var competitor = competitorService.getCompetitorEntityById(competitorId);
        if(!competitor.isSuccess()){
            return new ErrorResult(competitor.getMessage(), competitor.getHttpStatus());
        }

        if(season.getCompetitors().contains(competitor.getData())) {
            return new ErrorResult(SeasonMessages.CompetitorAlreadyInSeason, HttpStatus.BAD_REQUEST);
        }

        season.getCompetitors().add(competitor.getData());
        seasonDao.save(season);

        return new SuccessResult(SeasonMessages.CompetitorAddedSuccess, HttpStatus.OK);
    }
}
