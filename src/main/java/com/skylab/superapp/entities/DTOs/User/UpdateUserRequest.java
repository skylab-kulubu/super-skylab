package com.skylab.superapp.entities.DTOs.User;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateUserRequest {

    private UUID id;

    private String firstName;

    private String lastName;

    private String linkedin;

    private String university;

    private String faculty;

    private String department;


}
