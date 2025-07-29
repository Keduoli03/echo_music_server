package com.lanke.echomusic.dto.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Schema(description = "操作日志查询参数DTO")
public class OperationLogSearchDTO {

    @Schema(description = "当前页码（默认1）", example = "1", defaultValue = "1", minimum = "1")
    private long current = 1;

    @Schema(description = "每页大小（默认10）", example = "10", defaultValue = "10", minimum = "1", maximum = "100")
    private long size = 10;

    @Schema(description = "用户名模糊查询", example = "admin")
    private String username;

    @Schema(description = "操作模块", example = "歌曲管理")
    private String module;

    @Schema(description = "操作类型", example = "新增")
    private String operationType;

    @Schema(description = "用户角色", example = "ADMIN")
    private String userRole;

    @Schema(description = "操作结果", example = "成功")
    private String result;

    @Schema(description = "开始时间", example = "2024-01-01 00:00:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2024-12-31 23:59:59")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "排序字段", example = "operationTime,desc")
    private String orderBy;
}