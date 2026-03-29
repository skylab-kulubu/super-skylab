package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.User;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {

    private final ImageMapper imageMapper;

    public UserMapper(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());

        userDto.setSkyNumber(user.getSkyNumber());

        userDto.setLdapUser(user.isLdapUser());

        if (user.getProfilePicture() != null) {
            userDto.setProfilePictureUrl(imageMapper.toString(user.getProfilePicture()));
        }

        userDto.setLinkedin(user.getLinkedin());
        userDto.setUniversity(user.getUniversity());
        userDto.setFaculty(user.getFaculty());
        userDto.setDepartment(user.getDepartment());

        return userDto;
    }


}
