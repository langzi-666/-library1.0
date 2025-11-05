package com.library.aspect;

import com.library.annotation.LogOperation;
import com.library.service.OperationLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 操作日志切面
 */
@Aspect
@Component
public class OperationLogAspect {
    
    @Autowired
    private OperationLogService operationLogService;
    
    @Pointcut("@annotation(com.library.annotation.LogOperation)")
    public void logPointcut() {
    }
    
    @AfterReturning(pointcut = "logPointcut()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            LogOperation logOperation = method.getAnnotation(LogOperation.class);
            
            if (logOperation != null) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes == null) {
                    return;
                }
                HttpServletRequest request = attributes.getRequest();
                String userId = (String) request.getAttribute("userId");
                String ipAddress = getIpAddress(request);
                String userAgent = request.getHeader("User-Agent");
                
                String operationType = logOperation.operationType();
                String operationContent = logOperation.operationContent();
                
                // 如果操作内容为空，尝试从方法参数中获取
                if (operationContent.isEmpty()) {
                    operationContent = buildOperationContent(joinPoint, method);
                }
                
                // 如果userId为空，跳过日志记录
                if (userId != null) {
                    operationLogService.saveLog(userId, operationType, operationContent, ipAddress, userAgent, "成功");
                }
            }
        } catch (Exception e) {
            // 日志记录失败不影响业务
            e.printStackTrace();
        }
    }
    
    /**
     * 构建操作内容
     */
    private String buildOperationContent(JoinPoint joinPoint, Method method) {
        StringBuilder content = new StringBuilder();
        content.append(method.getName());
        
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            content.append("(");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    content.append(", ");
                }
                content.append(args[i]);
            }
            content.append(")");
        }
        
        return content.toString();
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}


