package com.skylab.superapp.core.security.authz;

/**
 * Bir kaynak turu icin PDP'ye gonderilecek baglami uretir.
 * Her resource type icin bir @Component implementasyonu yazilir; aspect otomatik kesfeder.
 * Resolver yoksa kaynak ozniteliksiz degerlendirilir (rol-bazli rego kurallari icin yeterli).
 */
public interface ResourceContextResolver {

    /** Hangi resource type'i cozuyor (orn. "EVENT"). @Authorize.resource() ile eslesir. */
    String resourceType();

    /**
     * @param action  CREATE / UPDATE / DELETE / ...
     * @param key     @AuthzKey ile isaretli parametrenin degeri (id veya istek nesnesi); yoksa null
     */
    ResourceContext resolve(String action, Object key);
}
