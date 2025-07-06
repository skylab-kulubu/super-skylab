package com.skylab.superapp.entities.DTOs.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateUserDto {

    private String firstName;

    private String lastName;

    private String email;

    private String linkedin;

    private String university;

    private String faculty;

    private String department;
}
