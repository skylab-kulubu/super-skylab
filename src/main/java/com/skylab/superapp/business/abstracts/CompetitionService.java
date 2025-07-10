package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.Competition;
import com.skylab.superapp.entities.DTOs.competition.CompetitionDto;
import com.skylab.superapp.entities.DTOs.competition.CreateCompetitionRequest;
import com.skylab.superapp.entities.DTOs.competition.UpdateCompetitionRequest;

import java.util.List;
import java.util.UUID;

public interface CompetitionService {

    CompetitionDto getCompetitionById(UUID competitionId, boolean includeEvent, boolean includeEventType);

    List<CompetitionDto> getAllCompetitions(boolean includeEvent, boolean includeEventType);

    CompetitionDto updateCompetition(UpdateCompetitionRequest updateCompetitionRequest, UUID id);

    void deleteCompetition(UUID competitionId);

    CompetitionDto addCompetition(CreateCompetitionRequest createCompetitionRequest);

    List<CompetitionDto> getActiveCompetitions(boolean includeEvent, boolean includeEventType);


    Competition getCompetitionEntityById(UUID competitionId);
}
