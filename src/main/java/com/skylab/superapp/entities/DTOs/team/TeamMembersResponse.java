package com.skylab.superapp.entities.DTOs.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamMembersResponse {
    private String team;
    private LocalizedText displayName;
    private LocalizedText description;
    private int count;
    private List<TeamMemberDto> members;
}
