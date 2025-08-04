package com.lanke.echomusic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 歌单表
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
@TableName("l_playlist")
public class Playlist implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 歌单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 歌单名称
     */
    private String name;

    /**
     * 用户ID（创建者，关联用户表）
     */
    private Long userId;

    /**
     * 歌单描述
     */
    private String description;

    /**
     * 封面URL
     */
    private String coverUrl;

    /**
     * 是否公开（0-私密，1-公开）
     */
    private Byte isPublic;

    /**
     * 是否在首页展示（0-否，1-是）
     */
    private Byte isHomeDisplay;

    /**
     * 首页展示排序（数值越小越靠前）
     */
    private Integer homeSort;

    /**
     * 歌曲数量（冗余字段，用于快速统计）
     */
    private Integer songCount;

    /**
     * 播放次数
     */
    private Long playCount;

    /**
     * 收藏次数
     */
    private Integer collectCount;

    /**
     * 音乐类型（1-华语流行, 2-欧美流行, 3-日韩流行, 4-古典音乐, 5-民谣, 6-摇滚, 7-电子音乐, 8-说唱, 9-爵士, 10-其他）
     */
    private Integer musicType;

    /**
     * 歌单分类（1-个人歌单, 2-精选歌单, 3-推荐歌单, 4-官方歌单, 5-主题歌单）
     */
    private Integer playlistCategory;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Byte getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Byte isPublic) {
        this.isPublic = isPublic;
    }

    public Byte getIsHomeDisplay() {
        return isHomeDisplay;
    }

    public void setIsHomeDisplay(Byte isHomeDisplay) {
        this.isHomeDisplay = isHomeDisplay;
    }

    public Integer getHomeSort() {
        return homeSort;
    }

    public void setHomeSort(Integer homeSort) {
        this.homeSort = homeSort;
    }

    public Integer getSongCount() {
        return songCount;
    }

    public void setSongCount(Integer songCount) {
        this.songCount = songCount;
    }

    public Long getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Long playCount) {
        this.playCount = playCount;
    }

    public Integer getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Playlist{" +
            "id = " + id +
            ", name = " + name +
            ", userId = " + userId +
            ", description = " + description +
            ", coverUrl = " + coverUrl +
            ", isPublic = " + isPublic +
            ", isHomeDisplay = " + isHomeDisplay +
            ", homeSort = " + homeSort +
            ", songCount = " + songCount +
            ", playCount = " + playCount +
            ", collectCount = " + collectCount +
            ", createTime = " + createTime +
            ", updateTime = " + updateTime +
            "}";
    }

    public Integer getMusicType() {
        return musicType;
    }

    public void setMusicType(Integer musicType) {
        this.musicType = musicType;
    }

    public Integer getPlaylistCategory() {
        return playlistCategory;
    }

    public void setPlaylistCategory(Integer playlistCategory) {
        this.playlistCategory = playlistCategory;
    }
}
