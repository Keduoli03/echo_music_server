package com.lanke.echomusic.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lanke.echomusic.dto.log.OperationLogSearchDTO;
import com.lanke.echomusic.entity.OperationLog;
import com.lanke.echomusic.vo.log.OperationLogVO;

/**
 * 操作日志服务接口
 */
public interface IOperationLogService extends IService<OperationLog> {

    /**
     * 记录操作日志
     */
    void saveLog(String module, String operationType, String description, 
                 String requestMethod, String requestUrl, String requestParams,
                 String result, String errorMsg, Long executionTime);

    /**
     * 分页查询操作日志
     */
    IPage<OperationLogVO> searchLogs(OperationLogSearchDTO searchDTO);

    /**
     * 清理过期日志（保留指定天数）
     */
    void cleanExpiredLogs(int retentionDays);
}