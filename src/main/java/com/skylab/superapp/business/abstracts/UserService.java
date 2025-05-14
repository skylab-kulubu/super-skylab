package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.DTOs.User.CreateUserDto;
import com.skylab.superapp.entities.DTOs.User.GetUserDto;
import com.skylab.superapp.entities.Role;
import com.skylab.superapp.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    Result addUser(CreateUserDto createUserDto);

    Result deleteUser(int id);

    DataResult<List<GetUserDto>> getAllUsers();

    DataResult<User> getUserEntityById(int id);

    DataResult<GetUserDto> getUserById(int id);

    DataResult<User> getUserEntityByUsername(String username);

    boolean tenantCheck(String tenant, String username);

    Result addRoleToUser(String username, Role role);

    Result removeRoleFromUser(String username, Role role);

    Result setLastLogin(String username);

    DataResult<User> getAuthenticatedUser();
}
