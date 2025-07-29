package com.lanke.echomusic.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "操作日志VO")
public class OperationLogVO {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户角色")
    private String userRole;

    @Schema(description = "操作模块")
    private String module;

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "操作描述")
    private String description;

    @Schema(description = "请求方法")
    private String requestMethod;

    @Schema(description = "请求URL")
    private String requestUrl;

    @Schema(description = "操作结果")
    private String result;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "执行时间（毫秒）")
    private Long executionTime;

    @Schema(description = "操作时间")
    private LocalDateTime operationTime;
}