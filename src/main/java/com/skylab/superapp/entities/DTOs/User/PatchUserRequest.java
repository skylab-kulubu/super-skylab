package com.skylab.superapp.entities.DTOs.User;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PatchUserRequest {
    private String firstName;
    private String lastName;
    private String linkedin;
    private String university;
    private String faculty;
    private String department;
}
