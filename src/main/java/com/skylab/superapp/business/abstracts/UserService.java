package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.User.*;
import com.skylab.superapp.entities.UserProfile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService{

    UserDto addUser(CreateUserRequest createUpdateRequest);

    void deleteUser(UUID id);

    List<UserDto> getAllUsers();

    UserDto getUserById(UUID id);

    UserDto getUserByUsername(String username);

    UserDto getUserByEmail(String email);

    void assignRoleToUser(String username, String role);

    UserDto updateAuthenticatedUser(UpdateUserRequest updateUserRequest);

    UserDto updateUser(UUID userId, UpdateUserRequest updateUserRequest);

    void changePassword(UUID userId, String newPassword);

    UserDto getAuthenticatedUser();

    UserProfile getUserEntityById(UUID id);

    UserProfile getAuthenticatedUserEntity();

    void uploadProfilePictureOfAuthenticatedUser(MultipartFile imageFile);


}
