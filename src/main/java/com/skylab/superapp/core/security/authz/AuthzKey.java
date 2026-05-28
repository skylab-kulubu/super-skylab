package com.skylab.superapp.core.security.authz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Yetki kararinda kullanilacak kaynagi isaretler (pozisyonel tahmin yok).
 * Degeri, ilgili ResourceContextResolver'a verilir:
 *   - UPDATE/DELETE: kaynagin id'si (orn. UUID eventId)
 *   - CREATE:        istek nesnesi (orn. CreateEventRequest)
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthzKey {
}
