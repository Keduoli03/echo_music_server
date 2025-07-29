package com.lanke.echomusic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 专辑表
 * </p>
 *
 * @author lanke
 * @since 2025-06-02
 */
@TableName("l_album")
public class Album implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 专辑ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 专辑名称
     */
    private String name;

    /**
     * 歌手ID
     */
    private Long singerId;

    /**
     * 发行日期
     */
    private LocalDate releaseDate;

    /**
     * 类型（1-专辑，2-EP，3-单曲）
     */
    private Byte type;

    /**
     * 专辑描述
     */
    private String description;

    /**
     * 封面URL
     */
    private String coverUrl;

    /**
     * 状态（0-下架，1-上架）
     */
    private Byte status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSingerId() {
        return singerId;
    }

    public void setSingerId(Long singerId) {
        this.singerId = singerId;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Album{" +
            "id = " + id +
            ", name = " + name +
            ", singerId = " + singerId +
            ", releaseDate = " + releaseDate +
            ", type = " + type +
            ", description = " + description +
            ", coverUrl = " + coverUrl +
            ", status = " + status +
            ", createdAt = " + createdAt +
            ", updatedAt = " + updatedAt +
            "}";
    }
}
