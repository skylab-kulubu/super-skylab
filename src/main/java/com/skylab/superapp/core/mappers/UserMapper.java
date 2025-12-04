package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.LdapUser;
import com.skylab.superapp.entities.UserProfile;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class UserMapper {

    private final ImageMapper imageMapper;

    public UserMapper(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    public UserDto toDto(UserProfile userProfile, LdapUser ldapUser, Set<String> roles) {
        if (userProfile == null || ldapUser == null) {
            return null;
        }

        UserDto userDto = new UserDto();
        userDto.setId(userProfile.getId());
        userDto.setUsername(ldapUser.getUsername());
        userDto.setEmail(ldapUser.getEmail());
        userDto.setFirstName(ldapUser.getFirstName());
        userDto.setLastName(ldapUser.getLastName());

        userDto.setProfilePictureUrl(imageMapper.toString(userProfile.getProfilePicture()));

        userDto.setLinkedin(userProfile.getLinkedin());
        userDto.setUniversity(userProfile.getUniversity());
        userDto.setFaculty(userProfile.getFaculty());
        userDto.setDepartment(userProfile.getDepartment());

        userDto.setRoles(roles != null ? roles : Collections.emptySet());

        return userDto;
    }


}
