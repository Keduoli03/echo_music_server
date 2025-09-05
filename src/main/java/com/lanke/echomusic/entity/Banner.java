package com.lanke.echomusic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Banner轮播图表
 * </p>
 *
 * @author lanke
 * @since 2025-01-21
 */
@TableName("l_banner")
public class Banner implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Banner ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Banner标题
     */
    private String title;

    /**
     * Banner图片URL
     */
    private String imageUrl;

    /**
     * Banner介绍/描述
     */
    private String description;

    /**
     * 跳转类型：music-歌曲，album-专辑，playlist-歌单
     */
    private String type;

    /**
     * 跳转目标ID（对应歌曲/专辑/歌单的ID）
     */
    private Long linkId;

    /**
     * 排序顺序（数字越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 是否启用（0-禁用，1-启用）
     */
    private Integer isActive;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    // 常量定义
    public static final String TYPE_SONG = "song";
    public static final String TYPE_ALBUM = "album";
    public static final String TYPE_PLAYLIST = "playlist";

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
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

    /**
     * 验证跳转类型是否有效
     */
    public boolean isValidType() {
        return TYPE_SONG.equals(type) || 
               TYPE_ALBUM.equals(type) || 
               TYPE_PLAYLIST.equals(type);
    }

    @Override
    public String toString() {
        return "Banner{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", linkId=" + linkId +
                ", sortOrder=" + sortOrder +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}