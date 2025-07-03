package com.skylab.superapp.entities.DTOs.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetUserDto {
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String linkedin;
    private String university;
    private String faculty;
    private String department;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private Date createdAt;
}