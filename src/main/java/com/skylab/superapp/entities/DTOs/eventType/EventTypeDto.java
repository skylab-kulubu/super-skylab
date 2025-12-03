package com.skylab.superapp.entities.DTOs.eventType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventTypeDto {

    private UUID id;

    private String name;

    private Set<String> authorizedRoles;

}
