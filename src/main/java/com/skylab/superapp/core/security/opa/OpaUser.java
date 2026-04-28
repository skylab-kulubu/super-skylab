package com.skylab.superapp.core.security.opa;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OpaUser {
    private String id;
    private List<String> roles;
}