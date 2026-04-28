package com.skylab.superapp.core.utilities.security;

import com.skylab.superapp.core.security.opa.OpaClient;
import org.springframework.stereotype.Component;

@Component
public class CompetitorSecurityUtils extends BaseSecurityUtils{

    private static final String RESOURCE = "COMPETITOR";

    public CompetitorSecurityUtils(OpaClient opaClient) {
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

    public void checkList() {
        checkPermission(RESOURCE, "LIST", null);
    }

    public void checkRead() {
        checkPermission(RESOURCE, "READ", null);
    }

    public void checkReadMe() {
        checkPermission(RESOURCE, "READ_ME", null);
    }


}
