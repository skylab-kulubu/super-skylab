package com.skylab.superapp.core.utilities.security;

import com.skylab.superapp.core.security.opa.OpaClient;
import org.springframework.stereotype.Component;

@Component
public class TicketSecurityUtils extends BaseSecurityUtils {

    private static final String RESOURCE = "TICKET";

    public TicketSecurityUtils(OpaClient opaClient) {
        super(opaClient);
    }

    public void checkValidate(String eventTypeName) {
        checkPermission(RESOURCE, "VALIDATE", eventTypeName);
    }

    public void checkRead() {
        checkPermission(RESOURCE, "READ", null);
    }

    public void checkReadMe() {
        checkPermission(RESOURCE, "READ_ME", null);
    }
}