package com.skylab.superapp.core.security.authz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Deklaratif yetki (PEP). Metodun ustune konur; AuthorizationAspect calismadan once
 * OPA'ya sorar. Business logic'e dokunmaz, unutulamaz, denetlenebilir.
 *
 * Ornek:
 *   @Authorize(resource = "EVENT", action = "UPDATE")
 *   public EventDto updateEvent(@AuthzKey UUID id, ...) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorize {
    String resource();
    String action();
}
