package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.Competition;
import com.skylab.superapp.entities.DTOs.competition.CreateCompetitionDto;

import java.util.List;

public interface CompetitionService {

    Competition getCompetitionById(int competitionId);

    List<Competition> getAllCompetitions();

    void updateCompetition(CreateCompetitionDto createCompetitionDto, int id);

    void deleteCompetition(int competitionId);

    Competition addCompetition(CreateCompetitionDto createCompetitionDto);

    List<Competition> getActiveCompetitions();
}
