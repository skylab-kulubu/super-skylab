package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.DTOs.CompetitorEventResult.CreateCompetitorEventResultDto;
import com.skylab.superapp.entities.DTOs.CompetitorEventResult.GetCompetitorEventResultDto;

import java.util.List;

public interface CompetitorEventResultService {

    DataResult<Long> addCompetitorEventResult(CreateCompetitorEventResultDto createCompetitorEventResultDto);

    Result deleteCompetitorEventResult(Long id);

    Result updateCompetitorEventResult(GetCompetitorEventResultDto getCompetitorEventResultDto);

    DataResult<GetCompetitorEventResultDto> getByCompetitorIdAndEventId(String competitorId, int eventId);

    DataResult<List<GetCompetitorEventResultDto>> getAllByCompetitorIdWithEvent(String competitorId);

    DataResult<List<GetCompetitorEventResultDto>> getAllByEventId(int eventId);


}
