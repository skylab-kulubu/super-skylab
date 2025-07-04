package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.DTOs.Auth.ChangePassword;
import com.skylab.superapp.entities.DTOs.Competitor.GetCompetitorDto;
import com.skylab.superapp.entities.DTOs.User.CreateUserDto;
import com.skylab.superapp.entities.DTOs.User.GetUserDto;
import com.skylab.superapp.entities.Role;
import com.skylab.superapp.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    void addUser(CreateUserDto createUserDto);

    void deleteUser(int id);

    void changePassword(ChangePassword changePassword);

    void resetPassword(CreateUserDto createUserDto);

    List<User> getAllUsers();

    User getUserById(int id);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    //boolean tenantCheck(String tenant, String username);

    void addRoleToUser(String username, Role role);

    void removeRoleFromUser(String username, Role role);

    void setLastLoginWithUsername(String username);

    String getAuthenticatedUsername();

    List<User> getAllStaffs();

    List<User> getStaffsByRole(Role role);

    List<User> getAllUsersByIds(List<Integer> userIds);

}
