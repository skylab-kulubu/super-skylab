package com.skylab.superapp.entities.DTOs.Staff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateStaffDto {

    private String firstName;

    private String lastName;

    private String linkedin;

    private String department;

    private int photoId;

    private String tenant;
}
