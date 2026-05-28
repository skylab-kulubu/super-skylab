package com.skylab.superapp.core.security.authz;

import com.skylab.superapp.core.security.opa.OpaClient;
import com.skylab.superapp.core.security.opa.OpaInput;
import com.skylab.superapp.core.security.opa.OpaResource;
import com.skylab.superapp.core.security.opa.OpaUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * PEP (Policy Enforcement Point). @Authorize ile isaretli metotlardan once calisir,
 * OPA'ya sorar.
 */
@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthorizationAspect {

    private final OpaClient opaClient;
    private final Map<String, ResourceContextResolver> resolvers;

    public AuthorizationAspect(OpaClient opaClient, List<ResourceContextResolver> resolverList) {
        this.opaClient = opaClient;
        this.resolvers = resolverList.stream()
                .collect(Collectors.toMap(ResourceContextResolver::resourceType, Function.identity()));
    }

    @Before("@annotation(authorize)")
    public void enforce(JoinPoint joinPoint, Authorize authorize) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            log.warn("authz-decision",
                    kv("decision", "DENIED"),
                    kv("reason", "unauthenticated"),
                    kv("resource", authorize.resource()),
                    kv("action", authorize.action()));
            throw new AccessDeniedException("user.not.authenticated");
        }

        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", ""))
                .collect(Collectors.toList());

        List<String> groups = List.of();
        if (auth.getPrincipal() instanceof Jwt jwt) {
            List<String> claim = jwt.getClaimAsStringList("groups");
            if (claim != null) {
                groups = claim;
            }
        }

        // Kaynak baglamini coz (varsa resolver + @AuthzKey)
        ResourceContext ctx = resolveContext(joinPoint, authorize.resource(), authorize.action());

        String effectiveOwner = (ctx.getOwnerGroup() != null && !ctx.getOwnerGroup().isBlank())
                ? ctx.getOwnerGroup() : ctx.getEventType();

        OpaInput input = OpaInput.builder()
                .user(OpaUser.builder().id(auth.getName()).roles(roles).groups(groups).build())
                .resource(OpaResource.builder()
                        .type(authorize.resource())
                        .eventType(ctx.getEventType())
                        .ownerGroup(effectiveOwner)
                        .ownerId(ctx.getOwnerId())
                        .build())
                .action(authorize.action())
                .build();

        // Karar korelasyonu: her karara benzersiz id; traceId loglara MDC'den otomatik gelir.
        String decisionId = UUID.randomUUID().toString();
        long start = System.currentTimeMillis();

        boolean allowed;
        try {
            allowed = opaClient.isAllowed(input);
        } catch (RuntimeException e) {
            // OpaClient OPA'ya ulasamazsa AccessDenied firlatir (FAIL-CLOSED). Audit'le, sonra propagate et.
            audit(decisionId, authorize, auth.getName(), effectiveOwner, ctx, "DENIED", "opa-unavailable",
                    System.currentTimeMillis() - start);
            throw e;
        }

        long duration = System.currentTimeMillis() - start;

        if (!allowed) {
            audit(decisionId, authorize, auth.getName(), effectiveOwner, ctx, "DENIED", "policy", duration);
            throw new AccessDeniedException("security.access.denied");
        }

        audit(decisionId, authorize, auth.getName(), effectiveOwner, ctx, "GRANTED", "policy", duration);
    }

    /** Her yetki karari yapisal JSON audit log (traceId MDC'den otomatik) -> Loki/Elastic. */
    private void audit(String decisionId, Authorize authorize, String userId, String ownerGroup,
                       ResourceContext ctx, String decision, String reason, long durationMs) {
        log.info("authz-decision",
                kv("decisionId", decisionId),
                kv("decision", decision),
                kv("reason", reason),
                kv("resource", authorize.resource()),
                kv("action", authorize.action()),
                kv("userId", userId),
                kv("ownerGroup", ownerGroup),
                kv("ownerId", ctx.getOwnerId()),
                kv("eventType", ctx.getEventType()),
                kv("durationMs", durationMs));
    }

    private ResourceContext resolveContext(JoinPoint joinPoint, String resource, String action) {
        ResourceContextResolver resolver = resolvers.get(resource);
        if (resolver == null) {
            return ResourceContext.empty();
        }
        Object key = findAuthzKey(joinPoint);
        return resolver.resolve(action, key);
    }

    /** @AuthzKey ile isaretli parametrenin degerini bulur. */
    private Object findAuthzKey(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Annotation[][] paramAnnotations = signature.getMethod().getParameterAnnotations();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation instanceof AuthzKey) {
                    return args[i];
                }
            }
        }
        return null;
    }
}
