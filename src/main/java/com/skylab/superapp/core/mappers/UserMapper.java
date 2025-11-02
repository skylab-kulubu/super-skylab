package com.skylab.superapp.core.mappers;

import com.skylab.superapp.core.utilities.ldap.LdapService;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.LdapUser;
import com.skylab.superapp.entities.UserProfile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    private final LdapService ldapService;
    private final ImageMapper imageMapper;

    public UserMapper(LdapService ldapService, ImageMapper imageMapper) {
        this.ldapService = ldapService;
        this.imageMapper = imageMapper;
    }

    public UserDto toDto(UserProfile userProfile, LdapUser ldapUser){
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

        if (ldapUser.getEmployeeNumber() != null) {
            userDto.setRoles(ldapService.getUserGroups(ldapUser.getEmployeeNumber()));
        } else {
            userDto.setRoles(List.of());
        }


        return userDto;
    }

    public UserDto toDto(UserProfile userProfile) {
        if (userProfile == null) {
            return null;
        }

        LdapUser ldapUser = ldapService.findByEmployeeNumber(userProfile.getLdapSkyNumber());

        if (ldapUser == null) {
            ldapUser = new LdapUser();
        }
        return toDto(userProfile, ldapUser);
    }





}
