package com.skylab.superapp.core.utilities.security;

import com.skylab.superapp.core.security.opa.OpaClient;
import org.springframework.stereotype.Component;

@Component
public class UserSecurityUtils extends BaseSecurityUtils {

    private static final String RESOURCE = "USER";

    public UserSecurityUtils(OpaClient opaClient) {
        super(opaClient);
    }

    public void checkRead() {
        checkPermission(RESOURCE, "READ", null);
    }

    public void checkUpdate() {
        checkPermission(RESOURCE, "UPDATE", null);
    }

    public void checkDelete() {
        checkPermission(RESOURCE, "DELETE", null);
    }

    public void checkPromote() {
        checkPermission(RESOURCE, "PROMOTE", null);
    }

    public void checkReadMe() {
        checkPermission(RESOURCE, "READ_ME", null);
    }

    public void checkUpdateMe() {
        checkPermission(RESOURCE, "UPDATE_ME", null);
    }
}