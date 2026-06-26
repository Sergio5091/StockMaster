package com.backend.stockmaster.core.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;

    @After("@annotation(com.backend.stockmaster.core.audit.Auditable)")
    public void auditOperation(JoinPoint joinPoint) {
        try {
            String userName = getCurrentUser();
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            Object[] args = joinPoint.getArgs();

            AuditLog auditLog = AuditLog.builder()
                    .entityName(className)
                    .operationType("UPDATE")
                    .operationDescription(methodName)
                    .performedBy(userName)
                    .performedAt(LocalDateTime.now())
                    .details(buildDetails(className, methodName, args))
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Audit logged: {} by {}", methodName, userName);
        } catch (Exception e) {
            log.error("Failed to log audit", e);
        }
    }

    private String getCurrentUser() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication != null ? authentication.getName() : "system";
        } catch (Exception e) {
            return "system";
        }
    }

    private String buildDetails(String className, String methodName, Object[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("Entity: ").append(className).append(", ");
        sb.append("Method: ").append(methodName).append(", ");
        sb.append("Args: [");

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg != null) {
                sb.append(arg.getClass().getSimpleName());
            } else {
                sb.append("null");
            }
            if (i < args.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
