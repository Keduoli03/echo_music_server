package com.lanke.echomusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanke.echomusic.dto.log.OperationLogSearchDTO;
import com.lanke.echomusic.entity.OperationLog;
import com.lanke.echomusic.mapper.OperationLogMapper;
import com.lanke.echomusic.service.IOperationLogService;
import com.lanke.echomusic.utils.RequestProcessor;
import com.lanke.echomusic.vo.UserVO;
import com.lanke.echomusic.vo.log.OperationLogVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements IOperationLogService {

    @Autowired
    private RequestProcessor requestProcessor;

    @Autowired
    private UserServiceImpl userService;  // 改为注入具体实现类

    @Override
    public void saveLog(String module, String operationType, String description,
                        String requestMethod, String requestUrl, String requestParams,
                        String result, String errorMsg, Long executionTime) {
        try {
            OperationLog log = new OperationLog();
            
            // 获取当前用户信息
            Long userId = requestProcessor.getUserId();
            String username = requestProcessor.getUsername();
            
            // 获取用户详细信息
            UserVO userVO = userService.getProfile(userId);  // 使用getProfile方法
            String userRole = userVO != null ? userVO.getRole() : "UNKNOWN";
            
            log.setUserId(userId);
            log.setUsername(username);
            log.setUserRole(userRole);
            log.setModule(module);
            log.setOperationType(operationType);
            log.setDescription(description);
            log.setRequestMethod(requestMethod);
            log.setRequestUrl(requestUrl);
            log.setRequestParams(requestParams);
            log.setResult(result);
            log.setErrorMsg(errorMsg);
            log.setExecutionTime(executionTime);
            
            // 获取请求信息
            HttpServletRequest request = getCurrentHttpRequest();
            if (request != null) {
                log.setIpAddress(getClientIpAddress(request));
                log.setUserAgent(request.getHeader("User-Agent"));
            }
            log.setOperationTime(LocalDateTime.now());
            
            // 保存日志
            baseMapper.insert(log);
        } catch (Exception e) {
            // 记录日志保存失败，但不影响业务流程
            System.err.println("保存操作日志失败: " + e.getMessage());
        }
    }

    @Override
    public IPage<OperationLogVO> searchLogs(OperationLogSearchDTO searchDTO) {
        Page<OperationLogVO> page = new Page<>(searchDTO.getCurrent(), searchDTO.getSize());
        return baseMapper.searchLogs(page, searchDTO);
    }

    @Override
    public void cleanExpiredLogs(int retentionDays) {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(retentionDays);
        LambdaQueryWrapper<OperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(OperationLog::getOperationTime, expireTime);
        remove(queryWrapper);
    }

    /**
     * 获取当前HTTP请求
     */
    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}