package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.User.*;
import com.skylab.superapp.entities.LdapUser;
import com.skylab.superapp.entities.UserProfile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserService{

    UserDto addUser(CreateUserRequest createUpdateRequest);

    void deleteUser(UUID id);

    List<UserDto> getAllUsers();

    UserDto getUserById(UUID id);

    UserDto getUserByUsername(String username);

    UserDto getUserByEmail(String email);

    List<UserDto> getUsersByRoleNames(Collection<String> roles);

    Map<UUID, UserDto> mapProfilesToUsers(List<UserProfile> profiles);

    void assignRoleToUser(String username, String role);

    UserDto updateAuthenticatedUser(UpdateUserRequest updateUserRequest);

    UserDto updateUser(UUID userId, UpdateUserRequest updateUserRequest);

    void changePassword(UUID userId, String newPassword);

    UserDto getAuthenticatedUser();

    UserProfile getUserEntityById(UUID id);

    UserProfile getAuthenticatedUserEntity();

    void uploadProfilePictureOfAuthenticatedUser(MultipartFile imageFile);


    List<UserDto> getUsersByRoleName(String role);

    void removeRoleFromUser(String username, String role);
}
