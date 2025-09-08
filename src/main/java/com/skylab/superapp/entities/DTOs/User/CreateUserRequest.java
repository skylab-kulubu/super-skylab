package com.skylab.superapp.entities.DTOs.User;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String linkedin;

    private String birthdate;

    private String university;

    private String faculty;

    private String department;




}
