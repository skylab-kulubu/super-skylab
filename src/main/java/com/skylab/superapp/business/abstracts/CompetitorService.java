package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.Competitor;
import com.skylab.superapp.entities.DTOs.Competitor.CreateCompetitorDto;
import com.skylab.superapp.entities.DTOs.Competitor.GetCompetitorDto;

import java.util.List;

public interface CompetitorService {

    Result addCompetitor(CreateCompetitorDto createCompetitorDto);

    Result deleteCompetitor(String id);

    Result updateCompetitor(GetCompetitorDto getCompetitorDto);

    DataResult<List<GetCompetitorDto>> getAllCompetitors();

    DataResult<List<GetCompetitorDto>> getAllCompetitorsByTenant(String tenant);

    DataResult<Competitor> getCompetitorEntityById(String id);

    DataResult<List<GetCompetitorDto>> getAllBySeasonId(int seasonId);

    Result addPointsToCompetitor(String competitorId, double points);

}
