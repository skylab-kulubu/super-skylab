package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.DTOs.Event.GetEventDto;
import com.skylab.superapp.entities.DTOs.Event.GetEventDetailsDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventMapper {

    private final ImageMapper imageMapper;
    private final SessionMapper sessionMapper;
    private final SeasonMapper seasonMapper;

    public EventMapper(ImageMapper imageMapper, SessionMapper sessionMapper, @Lazy SeasonMapper seasonMapper) {
        this.imageMapper = imageMapper;
        this.sessionMapper = sessionMapper;
        this.seasonMapper = seasonMapper;
    }

    public GetEventDto toDto(Event event) {
        return GetEventDto.builder()
                .id(event.getId())
                .title(event.getName())
                .description(event.getDescription())
                .date(event.getDate())
                .isActive(event.isActive())
                .images(event.getImages()== null ? null : imageMapper.toDtoList(event.getImages()))
                .type(event.getType()==null ? null : event.getType().getName())
                .formUrl(event.getFormUrl())
                .build();
    }

    public GetEventDetailsDto toDetailsDto(Event event) {
        return GetEventDetailsDto.builder()
                .id(event.getId())
                .title(event.getName())
                .description(event.getDescription())
                .date(event.getDate())
                .isActive(event.isActive())
                .images(imageMapper.toDtoList(event.getImages()))
                .type(event.getType().getName())
                .formUrl(event.getFormUrl())
                .linkedin(event.getLinkedin())
                .sessions(sessionMapper.toDtoList(event.getSessions()))
                .build();
    }

    public List<GetEventDetailsDto> toDetailsDtoList(List<Event> events) {
        return events.stream()
                .map(this::toDetailsDto)
                .toList();
    }

    public List<GetEventDto> toDtoList(List<Event> events) {
        return events.stream()
                .map(this::toDto)
                .toList();
    }
}