package com.lanke.echomusic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志表
 */
@Data
@TableName("l_operation_log")
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户角色
     */
    private String userRole;

    /**
     * 操作模块（如：歌曲管理、专辑管理、用户管理等）
     */
    private String module;

    /**
     * 操作类型（如：新增、修改、删除、查询、登录、登出等）
     */
    private String operationType;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 请求方法（GET、POST、PUT、DELETE等）
     */
    private String requestMethod;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应结果（成功/失败）
     */
    private String result;

    /**
     * 错误信息（如果操作失败）
     */
    private String errorMsg;

    /**
     * 操作IP地址
     */
    private String ipAddress;

    /**
     * 用户代理（浏览器信息）
     */
    private String userAgent;

    /**
     * 执行时间（毫秒）
     */
    private Long executionTime;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;
}