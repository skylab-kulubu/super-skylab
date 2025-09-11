package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.utilities.keycloak.KeycloakRole;
import com.skylab.superapp.entities.DTOs.User.*;
import com.skylab.superapp.entities.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

public interface UserService{

    UserDto addUser(CreateUserRequest createUpdateRequest);

    void deleteUser(UUID id);

    List<UserDto> getAllUsers();

    UserDto getUserById(UUID id);

    UserDto getUserByUsername(String username);

    UserDto getUserByEmail(String email);

    void addRoleToUser(String username, KeycloakRole role);

    void setLastLoginWithUsername(String username);

    List<UserDto> getAllUsersByIds(List<UUID> userIds);

    UserDto updateAuthenticatedUser(UpdateUserRequest updateUserRequest);

    UserDto updateUser(UUID userId, UpdateUserRequest updateUserRequest);

    void changePassword(UUID userId, String newPassword);

    UserDto getAuthenticatedUser();

    User getUserEntityById(UUID id);

    User getUserEntityByUsername(String username);

    User getUserEntityByEmail(String email);

    User getAuthenticatedUserEntity();


}
