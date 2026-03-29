package com.skylab.superapp.entities.DTOs.User;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromoteUserRequest {

    private String targetRole;

    private String initialPassword;


}
