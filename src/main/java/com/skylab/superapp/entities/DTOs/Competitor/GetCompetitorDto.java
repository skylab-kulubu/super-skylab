package com.skylab.superapp.entities.DTOs.Competitor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skylab.superapp.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetCompetitorDto {

    private int id;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private double totalPoints;

    private int competitionCount;

    public GetCompetitorDto(User user) {
     this.id = user.getId();
     this.firstName = user.getFirstName();
     this.lastName = user.getLastName();
     this.username = user.getUsername();
     this.email = user.getEmail();
    }

    public static List<GetCompetitorDto> buildListGetUserDto(List<User> users) {
        return users.stream()
                .map(GetCompetitorDto::new)
                .collect(Collectors.toList());
    }
}
