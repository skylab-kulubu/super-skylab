package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.User;
import com.skylab.superapp.entities.DTOs.User.GetUserDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public GetUserDto toDto(User user) {
        return GetUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .linkedin(user.getLinkedin())
                .university(user.getUniversity())
                .faculty(user.getFaculty())
                .department(user.getDepartment())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public List<GetUserDto> toDtoList(List<User> users) {
        return users.stream()
                .map(this::toDto)
                .toList();
    }
}