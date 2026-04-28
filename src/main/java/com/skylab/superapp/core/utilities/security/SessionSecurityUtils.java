package com.skylab.superapp.core.utilities.security;

import com.skylab.superapp.core.security.opa.OpaClient;
import org.springframework.stereotype.Component;

@Component
public class SessionSecurityUtils extends BaseSecurityUtils {

    private static final String RESOURCE = "SESSION";

    public SessionSecurityUtils(OpaClient opaClient) {
        super(opaClient);
    }

    public void checkCreate(String eventTypeName) {
        checkPermission(RESOURCE, "CREATE", eventTypeName);
    }

    public void checkUpdate(String eventTypeName) {
        checkPermission(RESOURCE, "UPDATE", eventTypeName);
    }

    public void checkDelete(String eventTypeName) {
        checkPermission(RESOURCE, "DELETE", eventTypeName);
    }

    public void checkRead() {
        checkPermission(RESOURCE, "READ", null);
    }
}