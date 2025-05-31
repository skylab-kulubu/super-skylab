package com.skylab.superapp.entities.DTOs.User;

import com.skylab.superapp.entities.Role;
import com.skylab.superapp.entities.User;
import com.skylab.superapp.entities.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

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

    private List<Role> roles;

    private Date createdAt;

    private Date lastLogin;

    public GetUserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.roles = user.getAuthorities().stream().toList();
        this.createdAt = user.getCreatedAt();
        this.lastLogin = user.getLastLogin();
    }

    public static List<GetUserDto> buildListGetUserDto(List<User> users) {
        return users.stream()
                .map(GetUserDto::new)
                .toList();
    }
}
