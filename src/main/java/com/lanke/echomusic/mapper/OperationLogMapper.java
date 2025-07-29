package com.lanke.echomusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lanke.echomusic.dto.log.OperationLogSearchDTO;
import com.lanke.echomusic.entity.OperationLog;
import com.lanke.echomusic.vo.log.OperationLogVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 操作日志 Mapper 接口
 */
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    /**
     * 分页查询操作日志
     */
    @Select("<script>" +
        "SELECT id, username, user_role, module, operation_type, description, " +
        "request_method, request_url, result, error_msg, ip_address, " +
        "execution_time, operation_time " +
        "FROM l_operation_log " +
        "<where>" +
        "<if test='dto.username != null and dto.username != \"\"'>" +
        "AND username LIKE CONCAT('%', #{dto.username}, '%') " +
        "</if>" +
        "<if test='dto.module != null and dto.module != \"\"'>" +
        "AND module = #{dto.module} " +
        "</if>" +
        "<if test='dto.operationType != null and dto.operationType != \"\"'>" +
        "AND operation_type = #{dto.operationType} " +
        "</if>" +
        "<if test='dto.userRole != null and dto.userRole != \"\"'>" +
        "AND user_role = #{dto.userRole} " +
        "</if>" +
        "<if test='dto.result != null and dto.result != \"\"'>" +
        "AND result = #{dto.result} " +
        "</if>" +
        "<if test='dto.startTime != null'>" +
        "AND operation_time &gt;= #{dto.startTime} " +
        "</if>" +
        "<if test='dto.endTime != null'>" +
        "AND operation_time &lt;= #{dto.endTime} " +
        "</if>" +
        "</where>" +
        "ORDER BY operation_time DESC" +
        "</script>")
    IPage<OperationLogVO> searchLogs(Page<OperationLogVO> page, @Param("dto") OperationLogSearchDTO dto);
}