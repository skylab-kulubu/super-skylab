package com.skylab.superapp.entities.DTOs.eventType;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UpdateEventTypeRequest {

    private String name;

    private Set<String> authorizedRoles;




}
