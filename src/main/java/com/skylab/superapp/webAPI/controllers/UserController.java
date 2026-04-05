package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.User.PromoteUserRequest;
import com.skylab.superapp.entities.DTOs.User.UpdateUserRequest;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/me")
    public ResponseEntity<DataResult<UserDto>> getAuthenticatedUser() {
     var result = userService.getAuthenticatedUser();
     return ResponseEntity.status(HttpStatus.OK)
             .body(new SuccessDataResult<>(result, UserMessages.USER_GET_SUCCESS,
                     HttpStatus.OK));
    }

    @PatchMapping("/me")
    public ResponseEntity<DataResult<UserDto>> updateAuthenticatedUser(@RequestBody UpdateUserRequest updateUserRequest) {
       var result =userService.updateAuthenticatedUser(updateUserRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.USER_UPDATED_SUCCESS, HttpStatus.OK));
    }

    @PostMapping("/me/profile-picture")
    public ResponseEntity<Result> updateProfilePicture(@RequestParam("image") MultipartFile image) {
        userService.uploadProfilePictureOfAuthenticatedUser(image);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(UserMessages.PROFILE_PICTURE_UPDATED_SUCCESS, HttpStatus.OK));
    }


    @GetMapping
    public ResponseEntity<DataResult<List<UserDto>>> getAllUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) List<String> roles) {

        var result = userService.getAllUsers(email, roles);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.USERS_LISTED_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<UserDto>> getUserById(@PathVariable UUID id) {
        var result = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.USER_GET_SUCCESS, HttpStatus.OK));
    }

    @PatchMapping("/{id}")
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


    @PostMapping("/{id}/promote")
    public ResponseEntity<Result> promoteUserToLdap(@PathVariable UUID id, @RequestBody PromoteUserRequest promoteUserRequest){

        userService.promoteUserToLdap(id, promoteUserRequest.getTargetRole(), promoteUserRequest.getInitialPassword());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(UserMessages.USER_PROMOTED_SUCCESS, HttpStatus.OK));


    }
}
