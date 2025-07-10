package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    private final ImageMapper imageMapper;

    public UserMapper(@Lazy ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    public UserDto toDto(User user) {

        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfilePicture() != null ? imageMapper.toDto(user.getProfilePicture()) : null,
                user.getLinkedin(),
                user.getBirthday(),
                user.getUniversity(),
                user.getFaculty(),
                user.getDepartment(),
                user.getLastLogin(),
                user.getAuthorities());

    }

    public List<UserDto> toDtoList(List<User> users) {
        return users.stream()
                .map(this::toDto)
                .toList();
    }
}