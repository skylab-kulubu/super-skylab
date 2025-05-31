package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.DTOs.Competitor.GetCompetitorDto;
import com.skylab.superapp.entities.DTOs.CompetitorEventResult.CreateCompetitorEventResultDto;
import com.skylab.superapp.entities.DTOs.CompetitorEventResult.GetCompetitorEventResultDto;
import com.skylab.superapp.entities.EventType;

import java.util.List;

public interface UserEventResultService {

    DataResult<Long> addCompetitorEventResult(CreateCompetitorEventResultDto createCompetitorEventResultDto);

    Result deleteCompetitorEventResult(Long id);

    Result updateCompetitorEventResult(GetCompetitorEventResultDto getCompetitorEventResultDto);

    DataResult<GetCompetitorEventResultDto> getByCompetitorIdAndEventId(String competitorId, int eventId);

    DataResult<List<GetCompetitorEventResultDto>> getAllByCompetitorIdWithEvent(String competitorId);

    DataResult<List<GetCompetitorEventResultDto>> getAllByEventId(int eventId);

    DataResult<List<GetCompetitorDto>> getAllCompetitorsByEventType(int eventTypeId);

    Result isCompetitorExist(int competitorsId);


}
