package com.lanke.echomusic.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lanke.echomusic.common.Result;
import com.lanke.echomusic.dto.log.OperationLogSearchDTO;
import com.lanke.echomusic.service.IOperationLogService;
import com.lanke.echomusic.vo.log.OperationLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "操作日志管理", description = "提供操作日志查询功能")
@RestController
@RequestMapping("/api/log")
public class OperationLogController {

    @Autowired
    private IOperationLogService operationLogService;

    @Operation(summary = "获取操作日志列表", description = "支持分页、条件筛选的操作日志查询接口")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/list")
    public Result<IPage<OperationLogVO>> getLogList(@ParameterObject OperationLogSearchDTO searchDTO) {
        IPage<OperationLogVO> result = operationLogService.searchLogs(searchDTO);
        return Result.success("查询成功", result);
    }

    @Operation(summary = "清理过期日志", description = "清理指定天数之前的操作日志")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/clean")
    public Result<Void> cleanExpiredLogs(@RequestParam(defaultValue = "30") int retentionDays) {
        operationLogService.cleanExpiredLogs(retentionDays);
        return Result.success("清理完成");
    }
}