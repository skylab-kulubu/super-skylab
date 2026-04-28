package com.skylab.superapp.core.security.opa;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OpaResource {
    private String type;
    private String eventType;
    private String tenant;      // for cms -- later
}
