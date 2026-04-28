package com.skylab.superapp.core.security.opa;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OpaInput {
    private OpaUser user;
    private OpaResource resource;
    private String action;
}