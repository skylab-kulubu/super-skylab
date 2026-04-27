package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.season.SeasonDto;
import com.skylab.superapp.entities.Season;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EventMapper.class})
public interface SeasonMapper {

    SeasonDto toDto(Season season);

}