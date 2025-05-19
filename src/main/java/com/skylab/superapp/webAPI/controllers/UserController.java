package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.DTOs.Auth.ChangePassword;
import com.skylab.superapp.entities.DTOs.User.CreateUserDto;
import com.skylab.superapp.entities.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody CreateUserDto createUserDto) {
        var result = userService.addUser(createUserDto);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestParam int id) {
        var result = userService.deleteUser(id);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePassword changePassword) {
        var result = userService.changePassword(changePassword);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/changeAuthenticatedUserPassword")
    public ResponseEntity<Result> changeAuthenticatedUserPassword(@RequestBody String newPassword) {
        var result = userService.changeAuthenticatedUserPassword(newPassword);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }


    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody CreateUserDto createUserDto) {
        var result = userService.resetPassword(createUserDto);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/addRole")
    public ResponseEntity<?> addRoleToUser(@RequestParam String username, @RequestParam Role role) {
        var result = userService.addRoleToUser(username, role);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/removeRole")
    public ResponseEntity<?> removeRoleFromUser(@RequestParam String username, @RequestParam Role role) {
        var result = userService.removeRoleFromUser(username, role);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllUsers() {
        var result = userService.getAllUsers();
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getById")
    public ResponseEntity<?> getUserById(@RequestParam int id) {
        var result = userService.getUserById(id);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }
}
