package com.skylab.superapp.business.abstracts;

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

    void addRoleToUser(String username, String role);

    void setLastLoginWithUsername(String username);

    List<UserDto> getAllUsersByIds(List<UUID> userIds);

    UserDto updateAuthenticatedUser(UpdateUserRequest updateUserRequest, HttpServletRequest request);

    UserDto updateUser(UUID userId, UpdateUserRequest updateUserRequest);

    UserDto getAuthenticatedUser(HttpServletRequest request);

    User getUserEntityById(UUID id);

    User getUserEntityByUsername(String username);

    User getUserEntityByEmail(String email);

    User getAuthenticatedUserEntity(HttpServletRequest request);


}
