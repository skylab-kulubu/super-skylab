package com.skylab.superapp.entities.DTOs.User;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDto {

    @EqualsAndHashCode.Include
    private UUID id;

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private String linkedin;
    private String university;
    private String faculty;
    private String department;

    private Set<String> roles;

}
