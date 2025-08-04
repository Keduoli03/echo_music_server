package com.lanke.echomusic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 音乐类型配置表
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
@Data
@TableName("l_music_type")
public class MusicType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 音乐类型ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 音乐类型名称
     */
    private String name;

    /**
     * 音乐类型描述
     */
    private String description;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 是否启用（1-启用，0-禁用）
     */
    private Boolean isActive;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}