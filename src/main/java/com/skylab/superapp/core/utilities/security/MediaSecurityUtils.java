package com.skylab.superapp.core.utilities.security;

import com.skylab.superapp.core.security.opa.OpaClient;
import org.springframework.stereotype.Component;

@Component
public class MediaSecurityUtils extends BaseSecurityUtils {

    private static final String RESOURCE = "MEDIA";

    public MediaSecurityUtils(OpaClient opaClient) {
        super(opaClient);
    }

    public void checkUpload() {
        checkPermission(RESOURCE, "UPLOAD", null);
    }

    public void checkDelete() {
        checkPermission(RESOURCE, "DELETE", null);
    }

    public void checkRead() {
        checkPermission(RESOURCE, "READ", null);
    }
}