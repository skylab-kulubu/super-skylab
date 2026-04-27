package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ImageMapper.class})
public interface UserMapper {


    @Mapping(source = "profilePicture", target = "profilePictureUrl")
    UserDto toDto(User user);

}