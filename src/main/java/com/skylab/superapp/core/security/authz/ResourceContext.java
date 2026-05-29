package com.skylab.superapp.core.security.authz;

import lombok.Builder;
import lombok.Getter;

/**
 * PDP'ye gonderilecek kaynak oznitelikleri (ABAC). Resolver doldurur.
 * Genisletilebilir: yeni oznitelik gerekince alan ekle (ownerId, tenant, ...).
 */
@Getter
@Builder
public class ResourceContext {
    private final String eventType;   // etkinlik turu adi (izin matrisi anahtari)
    private final String ownerGroup;  // sahip takim (yetki sahipligi); null ise eventType'a dusulur
    private final String ownerId;     // kaynagin sahibi kullanici id (self/ownership kontrolu icin)

    public static ResourceContext empty() {
        return ResourceContext.builder().build();
    }
}
