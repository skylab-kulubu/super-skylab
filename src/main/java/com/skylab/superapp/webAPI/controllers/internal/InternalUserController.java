package com.skylab.superapp.webAPI.controllers.internal;

import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/api/users")
public class InternalUserController {


    private final UserService userService;


    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/authenticated-user")
    public ResponseEntity<DataResult<UserDto>> getAuthenticatedUser() {
        var result = userService.getAuthenticatedUser();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.USER_GET_SUCCESS,
                        HttpStatus.OK));
    }


    @GetMapping("/{id}")
    public ResponseEntity<DataResult<UserDto>> getUserById(@PathVariable UUID id) {
        var result = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.USER_GET_SUCCESS, HttpStatus.OK));
    }



    @GetMapping("/batch")
    public ResponseEntity<DataResult<List<UserDto>>> getUsersByIds(@RequestBody List<UUID> ids){
        var result = userService.getAllUsersByIds(ids);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.USERS_LISTED_SUCCESS, HttpStatus.OK));
    }



}
