package com.skylab.superapp.core.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(com.skylab.superapp.webAPI.controllers..*)")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String userId = getCurrentUserId();

        long start = System.currentTimeMillis();

        log.info("Incoming REST Request: [{} {}] | User: {} | Method: {}.{}() | Params: {}",
                request.getMethod(),
                request.getRequestURI(),
                userId,
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("REST Request Failed: [{} {}] | User: {} | Error: {}",
                    request.getMethod(), request.getRequestURI(), userId, throwable.getMessage());
            throw throwable;
        }

        long executionTime = System.currentTimeMillis() - start;

        log.info("REST Request Completed: [{} {}] | User: {} | Time: {}ms",
                request.getMethod(),
                request.getRequestURI(),
                userId,
                executionTime);

        return result;
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        return "anonymous";
    }
}