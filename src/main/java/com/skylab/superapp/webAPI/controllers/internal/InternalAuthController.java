package com.skylab.superapp.webAPI.controllers.internal;

import com.skylab.superapp.business.abstracts.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/api/auth")
public class InternalAuthController {

    private final AuthService authService;

    public InternalAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/oauth2")
    public ResponseEntity<RegisterResponseDto> registerOauth2User(@RequestBody RegisterRequestDto registerRequestDto){

        var result = authService.registerOauth2(registerRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(result);

    }



}
