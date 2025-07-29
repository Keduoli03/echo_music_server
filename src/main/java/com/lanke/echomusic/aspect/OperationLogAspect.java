package com.lanke.echomusic.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lanke.echomusic.annotation.OperationLog;
import com.lanke.echomusic.service.IOperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private IOperationLogService operationLogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Around("@annotation(com.lanke.echomusic.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        String requestMethod = request != null ? request.getMethod() : "";
        String requestUrl = request != null ? request.getRequestURI() : "";
        String requestParams = "";
        
        // 记录请求参数
        if (operationLog.recordParams() && joinPoint.getArgs().length > 0) {
            try {
                Object[] args = joinPoint.getArgs();
                // 过滤掉文件上传参数
                Object[] filteredArgs = Arrays.stream(args)
                    .filter(arg -> !(arg instanceof MultipartFile))
                    .collect(Collectors.toList())
                    .toArray();
                requestParams = objectMapper.writeValueAsString(filteredArgs);
            } catch (Exception e) {
                requestParams = "参数序列化失败";
            }
        }
        
        String result = "成功";
        String errorMsg = null;
        
        try {
            // 执行目标方法
            Object returnValue = joinPoint.proceed();
            return returnValue;
        } catch (Exception e) {
            result = "失败";
            errorMsg = e.getMessage();
            throw e;
        } finally {
            // 记录日志
            long executionTime = System.currentTimeMillis() - startTime;
            operationLogService.saveLog(
                operationLog.module(),
                operationLog.operationType(),
                operationLog.description(),
                requestMethod,
                requestUrl,
                requestParams,
                result,
                errorMsg,
                executionTime
            );
        }
    }
}