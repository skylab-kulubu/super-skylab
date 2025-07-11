package com.skylab.superapp.entities.DTOs.User;

import com.skylab.superapp.entities.DTOs.Image.ImageDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UserDto {

    private UUID id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private ImageDto profilePicture;

    private String linkedin;

    private LocalDateTime birthday;

    private String university;

    private String faculty;

    private String department;

    private LocalDateTime lastLogin;


    public UserDto(UUID id, String username, String email, String firstName, String lastName, ImageDto profilePicture,
                   String linkedin, LocalDateTime birthday, String university, String faculty, String department,
                   LocalDateTime lastLogin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
        this.linkedin = linkedin;
        this.birthday = birthday;
        this.university = university;
        this.faculty = faculty;
        this.department = department;
        this.lastLogin = lastLogin;
    }
}
