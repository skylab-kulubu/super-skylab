package com.skylab.superapp.entities.DTOs.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateUserDto {

    private String username;

    private String password;
}
