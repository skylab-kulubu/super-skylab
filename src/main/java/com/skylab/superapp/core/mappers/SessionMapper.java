package com.skylab.superapp.core.mappers;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skylab.superapp.entities.DTOs.Image.GetImageDto;
import com.skylab.superapp.entities.DTOs.sessions.GetSessionDto;
import com.skylab.superapp.entities.Session;
import com.skylab.superapp.entities.SessionType;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class SessionMapper {

    private final ImageMapper imageMapper;

    public SessionMapper(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    public GetSessionDto toDto(Session session) {
        return GetSessionDto.builder()
                .id(session.getId())
                .title(session.getTitle())
                .speakerName(session.getSpeakerName())
                .speakerLinkedin(session.getSpeakerLinkedin())
                .speakerImage(session.getSpeakerImage() != null ? imageMapper.toDto(session.getSpeakerImage()) : null)
                .description(session.getDescription())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .orderIndex(session.getOrderIndex())
                .eventId(session.getEvent().getId())
                .sessionType(session.getSessionType())
                .build();
    }

    public List<GetSessionDto> toDtoList(List<Session> sessions) {
        return sessions.stream()
                .map(this::toDto)
                .toList();
    }
}
