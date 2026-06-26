package com.skylab.superapp.entities.DTOs.team;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Takim uyesinin HERKESE ACIK gosterilebilecek alanlari.
 * Bilerek PII disarida birakildi: email, schoolEmail, skyNumber, username, id.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamMemberDto {
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private String linkedin;
    private String university;
    private String faculty;
    private String department;
    private boolean leader;
}
