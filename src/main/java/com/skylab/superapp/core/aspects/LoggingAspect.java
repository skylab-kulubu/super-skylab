package com.skylab.superapp.core.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
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

        long start = System.currentTimeMillis();

        log.info("Incoming REST Request: [{} {}] | Method: {}.{}() | Params: {}",
                request.getMethod(),
                request.getRequestURI(),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("REST Request Failed: [{} {}] | Error: {}",
                    request.getMethod(), request.getRequestURI(), throwable.getMessage());
            throw throwable;
        }

        long executionTime = System.currentTimeMillis() - start;

        log.info("REST Request Completed: [{} {}] | Time: {}ms | Response: {}",
                request.getMethod(),
                request.getRequestURI(),
                executionTime,
                result != null ? result.toString() : "void");

        return result;
    }
}