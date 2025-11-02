package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.CompetitionService;
import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.business.abstracts.EventTypeService;
import com.skylab.superapp.core.constants.CompetitionMessages;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.exceptions.ValidationException;
import com.skylab.superapp.core.mappers.CompetitionMapper;
import com.skylab.superapp.dataAccess.CompetitionDao;
import com.skylab.superapp.entities.Competition;
import com.skylab.superapp.entities.DTOs.competition.CompetitionDto;
import com.skylab.superapp.entities.DTOs.competition.CreateCompetitionRequest;
import com.skylab.superapp.entities.DTOs.competition.UpdateCompetitionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CompetitionManager implements CompetitionService {

    private final CompetitionDao competitionDao;
    private final EventTypeService eventTypeService;
    private final CompetitionMapper competitionMapper;
    private final Logger logger = LoggerFactory.getLogger(CompetitionManager.class);
    private final EventService eventService;


    public CompetitionManager(CompetitionDao competitionDao, @Lazy EventTypeService eventTypeService,
                              CompetitionMapper competitionMapper, EventService eventService) {
        this.competitionDao = competitionDao;
        this.eventTypeService = eventTypeService;
        this.competitionMapper = competitionMapper;
        this.eventService = eventService;
    }




    @Override
    public CompetitionDto getCompetitionById(UUID competitionId, boolean includeEvent, boolean includeEventType) {
        return competitionMapper.toDto(getCompetitionEntityById(competitionId));

    }

    @Override
    public List<CompetitionDto> getAllCompetitions(boolean includeEvent, boolean includeEventType) {
        return competitionMapper.toDtoList(competitionDao.findAll(), includeEvent, includeEventType);
    }

    @Override
    public CompetitionDto updateCompetition(UpdateCompetitionRequest updateCompetitionRequest, UUID id) {
        var competition = getCompetitionEntityById(id);

        competition.setName(updateCompetitionRequest.getName());
        competition.setStartDate(updateCompetitionRequest.getStartDate());
        competition.setEndDate(updateCompetitionRequest.getEndDate());
        competition.setActive(updateCompetitionRequest.isActive());

        return competitionMapper.toDto(competitionDao.save(competition));
    }

    @Override
    public void deleteCompetition(UUID competitionId) {
        var competition = getCompetitionEntityById(competitionId);
        competitionDao.delete(competition);
    }

    @Override
    public CompetitionDto addCompetition(CreateCompetitionRequest createCompetitionRequest) {
        var eventType = eventTypeService.getEventTypeEntityById(createCompetitionRequest.getEventTypeId());

        var competition = Competition.builder()
                .name(createCompetitionRequest.getName())
                .startDate(createCompetitionRequest.getStartDate())
                .endDate(createCompetitionRequest.getEndDate())
                .active(createCompetitionRequest.isActive())
                .eventType(eventType)
                .build();

        return competitionMapper.toDto(competitionDao.save(competition));
    }

    @Override
    public List<CompetitionDto> getActiveCompetitions(boolean includeEvent, boolean includeEventType) {
        return competitionMapper.toDtoList(competitionDao.findAllByActive(true), includeEvent, includeEventType);
    }

    public Competition getCompetitionEntityById(UUID id){
        return competitionDao.findById(id).orElseThrow(() -> new ResourceNotFoundException(CompetitionMessages.COMPETITION_NOT_FOUND));
    }

    @Override
    public void addEventToCompetition(UUID competitionId, UUID eventId) {
        logger.info("Adding event with ID {} to competition with ID {}", eventId, competitionId);
        var competition = getCompetitionEntityById(competitionId);

        var event = eventService.getEventEntityById(eventId);

        if (competition.getEvents().contains(event)) {
            logger.info("Event with ID {} is already in competition with ID {}", eventId, competitionId);
            throw new ValidationException(CompetitionMessages.EVENT_ALREADY_IN_COMPETITION);
        }


        competition.getEvents().add(event);
        event.setCompetition(competition);

        competitionDao.save(competition);

        logger.info("Event with ID {} successfully added to competition with ID {}", eventId, competitionId);
    }
}
