package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.CompetitorService;
import com.skylab.superapp.business.abstracts.SeasonService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.business.constants.CompetitorMessages;
import com.skylab.superapp.core.results.*;
import com.skylab.superapp.dataAccess.CompetitorDao;
import com.skylab.superapp.entities.Competitor;
import com.skylab.superapp.entities.DTOs.Competitor.CreateCompetitorDto;
import com.skylab.superapp.entities.DTOs.Competitor.GetCompetitorDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CompetitorManager implements CompetitorService {

    private final UserService userService;
    private CompetitorDao competitorDao;
    private SeasonService seasonService;

    public CompetitorManager(CompetitorDao competitorDao, @Lazy SeasonService seasonService, UserService userService) {
        this.competitorDao = competitorDao;
        this.seasonService = seasonService;
        this.userService = userService;
    }

    @Override
    public DataResult<String> addCompetitor(CreateCompetitorDto createCompetitorDto) {
        if(createCompetitorDto.getName() == null || createCompetitorDto.getName().isEmpty()) {
            return new ErrorDataResult<>(CompetitorMessages.CompetitorNameCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        Competitor competitor = Competitor.builder()
                .competitionCount(createCompetitorDto.getCompetitionCount())
                .createdAt(new Date())
                .name(createCompetitorDto.getName())
                .isActive(createCompetitorDto.isActive())
                .tenant(createCompetitorDto.getTenant())
                .totalPoints(createCompetitorDto.getTotalPoints())
                .competitionCount(createCompetitorDto.getCompetitionCount())
                .build();
        competitorDao.save(competitor);
        return new SuccessDataResult<>(competitor.getId(),CompetitorMessages.CompetitorAddedSuccess, HttpStatus.CREATED);
    }

    @Override
    public Result deleteCompetitor(String id) {
        var result = competitorDao.findById(id);
        if(result.isEmpty()) {
            return new ErrorResult(CompetitorMessages.CompetitorNotFound, HttpStatus.NOT_FOUND);
        }

        Competitor competitor = result.get();
        competitorDao.delete(competitor);
        return new SuccessResult(CompetitorMessages.CompetitorDeletedSuccess, HttpStatus.OK);
    }

    @Override
    public Result updateCompetitor(GetCompetitorDto getCompetitorDto) {
        var result = competitorDao.findById(getCompetitorDto.getId());
        if(result.isEmpty()) {
            return new ErrorResult(CompetitorMessages.CompetitorNotFound, HttpStatus.NOT_FOUND);
        }

        var competitor = result.get();
        competitor.setName(getCompetitorDto.getName() == null ? competitor.getName() : getCompetitorDto.getName());
        competitor.setActive(getCompetitorDto.isActive());
        competitor.setTotalPoints(getCompetitorDto.getTotalPoints() == 0 ? competitor.getTotalPoints() : getCompetitorDto.getTotalPoints());
        competitor.setUpdatedAt(new Date());

        competitorDao.save(competitor);
        return new SuccessResult(CompetitorMessages.CompetitorUpdatedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetCompetitorDto>> getAllCompetitors() {
        var result = competitorDao.findAll();
        if(result.isEmpty()) {
            return new ErrorDataResult<>(CompetitorMessages.CompetitorNotFound, HttpStatus.NOT_FOUND);
        }

        var returnCompetitors = new GetCompetitorDto().buildListGetCompetitorDto(result);
        return new SuccessDataResult<>(returnCompetitors, CompetitorMessages.CompetitorListedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetCompetitorDto>> getAllCompetitorsByTenant(String tenant) {
        var result = competitorDao.findAllByTenant(tenant);
        if(result.isEmpty()) {
            return new ErrorDataResult<>(CompetitorMessages.CompetitorNotFound, HttpStatus.NOT_FOUND);
        }

        var returnCompetitors = new GetCompetitorDto().buildListGetCompetitorDto(result);
        return new SuccessDataResult<>(returnCompetitors, CompetitorMessages.CompetitorListedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<Competitor> getCompetitorEntityById(String id) {
        var result = competitorDao.findById(id);
        if(result.isEmpty()) {
            return new ErrorDataResult<>(CompetitorMessages.CompetitorNotFound, HttpStatus.NOT_FOUND);
        }

        Competitor competitor = result.get();
        return new SuccessDataResult<>(competitor, CompetitorMessages.CompetitorListedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetCompetitorDto>> getAllBySeasonId(int seasonId) {
        var season = seasonService.getSeasonEntityById(seasonId);
        if(!season.isSuccess()){
            return new ErrorDataResult<>(season.getMessage(), season.getHttpStatus());
        }

        var result = competitorDao.findAllBySeasons_IdOrderByTotalPointsDesc(seasonId);
        if(result.isEmpty()) {
            return new ErrorDataResult<>(CompetitorMessages.CompetitorNotFound, HttpStatus.NOT_FOUND);
        }

        var returnCompetitors = new GetCompetitorDto().buildListGetCompetitorDto(result);
        return new SuccessDataResult<>(returnCompetitors, CompetitorMessages.CompetitorListedSuccess, HttpStatus.OK);
    }

    @Override
    public Result addPointsToCompetitor(String competitorId, double points) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getName();

        var result = competitorDao.findById(competitorId);
        if(result.isEmpty()) {
            return new ErrorResult(CompetitorMessages.CompetitorNotFound, HttpStatus.NOT_FOUND);
        }

        var tenantCheck = userService.tenantCheck(result.get().getTenant(), username);
        if(!tenantCheck){
            return new ErrorResult(CompetitorMessages.TenantCheckFailed, HttpStatus.UNAUTHORIZED);
        }

        var competitor = result.get();
        competitor.setTotalPoints(competitor.getTotalPoints() + points);
        competitor.setUpdatedAt(new Date());

        competitorDao.save(competitor);
        return new SuccessResult(CompetitorMessages.CompetitorPointsAddedSuccess, HttpStatus.OK);
    }
}
