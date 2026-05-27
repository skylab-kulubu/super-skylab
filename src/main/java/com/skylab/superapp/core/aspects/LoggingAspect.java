package com.skylab.superapp.core.aspects;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final Tracer tracer;

    @Pointcut("within(com.skylab.superapp.webAPI.controllers..*)")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String userId = getCurrentUserId();
        String traceId = resolveTraceId();
        String spanId = resolveSpanId();
        String controller = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String action = joinPoint.getSignature().getName();

        long start = System.currentTimeMillis();

        log.info("http-request",
                kv("traceId", traceId),
                kv("spanId", spanId),
                kv("direction", "in"),
                kv("method", request.getMethod()),
                kv("path", request.getRequestURI()),
                kv("userId", userId),
                kv("controller", controller),
                kv("action", action),
                kv("paramCount", joinPoint.getArgs().length));

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            long duration = System.currentTimeMillis() - start;
            log.error("http-error",
                    kv("traceId", traceId),
                    kv("spanId", spanId),
                    kv("direction", "out"),
                    kv("method", request.getMethod()),
                    kv("path", request.getRequestURI()),
                    kv("userId", userId),
                    kv("controller", controller),
                    kv("action", action),
                    kv("duration", duration),
                    kv("exceptionClass", throwable.getClass().getSimpleName()),
                    kv("exceptionMessage", throwable.getMessage()));
            throw throwable;
        }

        long duration = System.currentTimeMillis() - start;
        int status = resolveHttpStatus(result);

        log.info("http-response",
                kv("traceId", traceId),
                kv("spanId", spanId),
                kv("direction", "out"),
                kv("method", request.getMethod()),
                kv("path", request.getRequestURI()),
                kv("userId", userId),
                kv("controller", controller),
                kv("action", action),
                kv("status", status),
                kv("duration", duration));

        return result;
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        return "anonymous";
    }

    private String resolveTraceId() {
        Span span = tracer.currentSpan();
        if (span != null) {
            return span.context().traceId();
        }
        return "unknown";
    }

    private String resolveSpanId() {
        Span span = tracer.currentSpan();
        if (span != null) {
            return span.context().spanId();
        }
        return "0000000000000000";
    }

    private int resolveHttpStatus(Object result) {
        if (result instanceof ResponseEntity<?> response) {
            return response.getStatusCode().value();
        }
        return 200;
    }
}