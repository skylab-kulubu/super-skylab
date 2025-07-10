package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.User.*;
import com.skylab.superapp.entities.Role;
import com.skylab.superapp.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

public interface UserService extends UserDetailsService {

    UserDto addUser(CreateUserRequest createUpdateRequest);

    void deleteUser(UUID id);

    void changePassword(ChangePasswordRequest changePasswordRequest);

    void resetPassword(ResetPasswordRequest resetPasswordRequest);

    List<UserDto> getAllUsers();

    UserDto getUserById(UUID id);

    UserDto getUserByUsername(String username);

    UserDto getUserByEmail(String email);

    void addRoleToUser(String username, Role role);

    void removeRoleFromUser(String username, Role role);

    void setLastLoginWithUsername(String username);

    String getAuthenticatedUsername();

    List<UserDto> getAllStaffs();

    List<UserDto> getStaffsByRole(Role role);

    List<UserDto> getAllUsersByIds(List<UUID> userIds);

    UserDto updateAuthenticatedUser(UpdateUserRequest updateUserRequest);

    UserDto updateUser(UUID userId, UpdateUserRequest updateUserRequest);

    UserDto getAuthenticatedUser();

    User getUserEntityById(UUID id);

    User getUserEntityByUsername(String username);

    User getUserEntityByEmail(String email);

    User getAuthenticatedUserEntity();


}
