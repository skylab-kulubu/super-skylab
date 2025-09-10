package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.User.ChangePasswordRequest;
import com.skylab.superapp.entities.DTOs.User.CreateUserRequest;
import com.skylab.superapp.entities.DTOs.User.UpdateUserRequest;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<Result> addUser(@RequestBody CreateUserRequest createUserRequest, HttpServletRequest request) {
        userService.addUser(createUserRequest);
        return ResponseEntity.status(201)
                .body(new SuccessResult(UserMessages.USER_ADDED_SUCCESS, HttpStatus.CREATED, request.getRequestURI()));
    }

    @GetMapping("/me")
    public ResponseEntity<DataResult<UserDto>> getAuthenticatedUser(HttpServletRequest request) {
     var result = userService.getAuthenticatedUser(request);
     return ResponseEntity.status(HttpStatus.OK)
             .body(new SuccessDataResult<>(result, UserMessages.USER_GET_SUCCESS,
                     HttpStatus.OK, request.getRequestURI()));
    }

    @PutMapping("/me")
    public ResponseEntity<DataResult<UserDto>> updateAuthenticatedUser(@RequestBody UpdateUserRequest updateUserRequest,
                                                           HttpServletRequest request) {
       var result =userService.updateAuthenticatedUser(updateUserRequest, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.USER_UPDATED_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }


    @GetMapping("/")
    public ResponseEntity<DataResult<List<UserDto>>> getAllUsers(HttpServletRequest request) {
        var result = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.ALL_USERS_RETRIEVED_SUCCESS,
                        HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<UserDto>> getUserById(@PathVariable UUID id, HttpServletRequest request) {
        var result = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.USER_GET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResult<UserDto>> updateUserById(@PathVariable UUID id,
                                                 @RequestBody UpdateUserRequest updateUserRequest,
                                                 HttpServletRequest request) {
        var result = userService.updateUser(id, updateUserRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.USER_UPDATED_SUCCESS,
                        HttpStatus.OK, request.getRequestURI()));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteUser(@PathVariable UUID id, HttpServletRequest request) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(UserMessages.USER_DELETED_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @PutMapping("/addRole/{username}")
    public ResponseEntity<Result> addRoleToUser(@PathVariable String username, @RequestParam String role,
                                                HttpServletRequest request) {
        userService.addRoleToUser(username, role);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(UserMessages.ROLE_ADDED_SUCCESS, HttpStatus.OK, request.getRequestURI()));
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
