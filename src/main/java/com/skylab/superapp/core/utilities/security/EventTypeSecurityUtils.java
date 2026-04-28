package com.skylab.superapp.core.utilities.security;

import com.skylab.superapp.core.security.opa.OpaClient;
import org.springframework.stereotype.Component;

@Component
public class EventTypeSecurityUtils extends BaseSecurityUtils {

    private static final String RESOURCE = "EVENT_TYPE";

    public EventTypeSecurityUtils(OpaClient opaClient) {
        super(opaClient);
    }

    public void checkCreate() {
        checkPermission(RESOURCE, "CREATE", null);
    }

    public void checkUpdate() {
        checkPermission(RESOURCE, "UPDATE", null);
    }

    public void checkDelete() {
        checkPermission(RESOURCE, "DELETE", null);
    }
}