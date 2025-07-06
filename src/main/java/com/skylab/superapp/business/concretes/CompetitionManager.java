package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.CompetitionService;
import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.core.exceptions.CompetitionNotFoundException;
import com.skylab.superapp.dataAccess.CompetitionDao;
import com.skylab.superapp.entities.Competition;
import com.skylab.superapp.entities.DTOs.competition.CreateCompetitionDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompetitionManager implements CompetitionService {

    private final CompetitionDao competitionDao;
    private final EventTypeService eventTypeService;



    public CompetitionManager(CompetitionDao competitionDao,@Lazy EventTypeService eventTypeService) {
        this.competitionDao = competitionDao;
        this.eventTypeService = eventTypeService;
    }




    @Override
    public Competition getCompetitionById(int competitionId) {
        return getCompetitionEntity(competitionId);

    }

    @Override
    public List<Competition> getAllCompetitions() {
        return competitionDao.findAll();
    }

    @Override
    public void updateCompetition(CreateCompetitionDto createCompetitionDto, int id) {
        var competition = getCompetitionEntity(id);
        var eventType = eventTypeService.getEventTypeById(createCompetitionDto.getEventType().getId());

        competition.setActive(createCompetitionDto.isActive());
        competition.setName(createCompetitionDto.getName());
        competition.setStartDate(createCompetitionDto.getStartDate());
        competition.setEndDate(createCompetitionDto.getEndDate());
        competition.setEventType(eventType);
        competitionDao.save(competition);
    }

    @Override
    public void deleteCompetition(int competitionId) {
        var competition = getCompetitionEntity(competitionId);
        competitionDao.delete(competition);
    }

    @Override
    public Competition addCompetition(CreateCompetitionDto createCompetitionDto) {
        var eventType = eventTypeService.getEventTypeById(createCompetitionDto.getEventType().getId());

        var competition = Competition.builder()
                .name(createCompetitionDto.getName())
                .startDate(createCompetitionDto.getStartDate())
                .endDate(createCompetitionDto.getEndDate())
                .active(true)
                .eventType(eventType)
                .build();

        return competitionDao.save(competition);
    }

    @Override
    public List<Competition> getActiveCompetitions() {
        return competitionDao.findAllByActive(true);
    }

    private Competition getCompetitionEntity(int id){
        return competitionDao.findById(id).orElseThrow(CompetitionNotFoundException::new);
    }
}
