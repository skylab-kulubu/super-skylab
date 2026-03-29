package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.User.*;
import com.skylab.superapp.entities.Image;
import com.skylab.superapp.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserService{

    User getAuthenticatedUserEntity();

    List<UserDto> getAllUsers();

    UserDto getUserById(UUID id);

    UserDto updateAuthenticatedUser(UpdateUserRequest updateUserRequest);

    void promoteUserToLdap(UUID userId, String targetRole, String initialPassword);


    UserDto getAuthenticatedUser();

    List<UserDto> getAllUsersByIds(List<UUID> ids);

    Image uploadProfilePictureOfAuthenticatedUser(MultipartFile image);

    UserDto updateUser(UUID id, UpdateUserRequest updateUserRequest);

    void deleteUser(UUID id);

    List<UserDto> getUsersByRoleNames(Set<String> roles);

    User getUserEntityById(UUID id);

}
