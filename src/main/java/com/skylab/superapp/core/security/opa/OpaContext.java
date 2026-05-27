package com.skylab.superapp.core.security.opa;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OpaContext {
    private String ipAddress;
    private long timestamp;
    private String traceId;
    private String channel;
}
