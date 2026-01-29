package com.prothsync.prothsync.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j(topic = "API_PERFORMANCE")
@Aspect
@Component
public class ExecutionTimeConfig {

    @Around("execution(* com.prothsync.prothsync.controller..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        ServletRequestAttributes attributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String methodName = joinPoint.getSignature().toShortString();
        String httpMethod = null;
        String requestUri = null;

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            httpMethod = request.getMethod();
            requestUri = request.getRequestURI();
        }

        try {
            // 실제 메서드 실행
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            // 실행 시간 로깅
            if (attributes != null) {
                log.info("[{}] {} {} - 실행 시간: {}ms",
                    httpMethod, requestUri, methodName, executionTime);
            } else {
                log.info("{} - 실행시간: {}ms", methodName, executionTime);
            }

            // 성능 경고 (1초 이상 소요시)
            if (executionTime > 1000) {
                log.warn("⚠️ SLOW API DETECTED: {} took {}ms", methodName, executionTime);
            }

            return result;

        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;

            if (attributes != null) {
                log.error("[{}] {} {} - Failed after {}ms - Error: {}",
                    httpMethod, requestUri, methodName, executionTime,
                    throwable.getMessage());
            } else {
                log.error("{} - Failed after {}ms - Error: {}",
                    methodName, executionTime, throwable.getMessage());
            }

            throw throwable;
        }
    }

}
