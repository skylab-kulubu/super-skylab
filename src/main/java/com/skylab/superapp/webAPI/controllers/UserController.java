package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.core.utilities.keycloak.KeycloakRole;
import com.skylab.superapp.entities.DTOs.User.CreateUserRequest;
import com.skylab.superapp.entities.DTOs.User.UpdateUserRequest;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/addUser")
    public ResponseEntity<Result> addUser(@RequestBody CreateUserRequest createUserRequest) {
        userService.addUser(createUserRequest);
        return ResponseEntity.status(201)
                .body(new SuccessResult(UserMessages.USER_ADDED_SUCCESS, HttpStatus.CREATED));
    }

    @GetMapping("/me")
    public ResponseEntity<DataResult<UserDto>> getAuthenticatedUser() {
     var result = userService.getAuthenticatedUser();
     return ResponseEntity.status(HttpStatus.OK)
             .body(new SuccessDataResult<>(result, UserMessages.USER_GET_SUCCESS,
                     HttpStatus.OK));
    }

    @PutMapping("/me")
    public ResponseEntity<DataResult<UserDto>> updateAuthenticatedUser(@RequestBody UpdateUserRequest updateUserRequest) {
       var result =userService.updateAuthenticatedUser(updateUserRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.USER_UPDATED_SUCCESS, HttpStatus.OK));
    }


    @GetMapping("/")
    public ResponseEntity<DataResult<List<UserDto>>> getAllUsers() {
        var result = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.ALL_USERS_RETRIEVED_SUCCESS,
                        HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<UserDto>> getUserById(@PathVariable UUID id) {
        var result = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.USER_GET_SUCCESS, HttpStatus.OK));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResult<UserDto>> updateUserById(@PathVariable UUID id,
                                                 @RequestBody UpdateUserRequest updateUserRequest) {
        var result = userService.updateUser(id, updateUserRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.USER_UPDATED_SUCCESS,
                        HttpStatus.OK));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(UserMessages.USER_DELETED_SUCCESS, HttpStatus.OK));
    }

    @PutMapping("/addRole/{username}")
    public ResponseEntity<Result> addRoleToUser(@PathVariable String username, @RequestParam KeycloakRole role) {
        userService.addRoleToUser(username, role);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(UserMessages.ROLE_ADDED_SUCCESS, HttpStatus.OK));
    }



    /*
    @PostMapping("/resetPassword")
    public ResponseEntity<Result> resetPassword(@RequestBody CreateUserDto createUserDto, HttpServletRequest request) {
        userService.resetPassword(createUserDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(UserMessages.PASSWORD_RESET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

     */



    /*

    @GetMapping("/getStaffByRole")
    public ResponseEntity<DataResult<List<User>>> getStaffsByRole(@RequestParam Role role, HttpServletRequest request){
        List<User> result = userService.getStaffsByRole(role);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.STAFFS_RETRIEVED_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/getAllStaffs")
    public ResponseEntity<DataResult<List<User>>> getAllStaffs(HttpServletRequest request){
        List<User> result = userService.getAllStaffs();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.ALL_STAFFS_RETRIEVED_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }


     */






}
