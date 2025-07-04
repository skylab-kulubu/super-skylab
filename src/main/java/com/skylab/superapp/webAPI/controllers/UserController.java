package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.Auth.ChangePassword;
import com.skylab.superapp.entities.DTOs.User.CreateUserDto;
import com.skylab.superapp.entities.Role;
import com.skylab.superapp.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/addUser")
    public ResponseEntity<Result> addUser(@RequestBody CreateUserDto createUserDto, HttpServletRequest request) {
        userService.addUser(createUserDto);
        return ResponseEntity.status(201)
                .body(new SuccessResult(UserMessages.USER_ADDED_SUCCESS, HttpStatus.CREATED, request.getRequestURI()));
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<Result> deleteUser(@PathVariable int id, HttpServletRequest request) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(UserMessages.USER_DELETED_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @PostMapping("/changePassword")
    public ResponseEntity<Result> changePassword(@RequestBody ChangePassword changePassword, HttpServletRequest request) {
        userService.changePassword(changePassword);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(UserMessages.PASSWORD_CHANGED_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Result> resetPassword(@RequestBody CreateUserDto createUserDto, HttpServletRequest request) {
        userService.resetPassword(createUserDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(UserMessages.PASSWORD_RESET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @PutMapping("/addRole/{username}")
    public ResponseEntity<Result> addRoleToUser(@PathVariable String username, @RequestParam Role role, HttpServletRequest request) {
        userService.addRoleToUser(username, role);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(UserMessages.ROLE_ADDED_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @DeleteMapping("/removeRole/{username}")
    public ResponseEntity<Result> removeRoleFromUser(@PathVariable String username, @RequestParam Role role, HttpServletRequest request) {
        userService.removeRoleFromUser(username, role);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(UserMessages.ROLE_REMOVED_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

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


    @GetMapping("/getAll")
    public ResponseEntity<DataResult<List<User>>> getAllUsers(HttpServletRequest request) {
        List<User> result = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.ALL_USERS_RETRIEVED_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<DataResult<User>> getUserById(@PathVariable int id, HttpServletRequest request) {
        User result = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.USER_GET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

}
