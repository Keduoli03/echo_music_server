package com.lanke.echomusic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 歌单-歌曲关联表
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
@TableName("l_playlist_song")
public class PlaylistSong implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID（用于分页）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 歌单ID
     */
    private Long playlistId;

    /**
     * 歌曲ID
     */
    private Long songId;

    /**
     * 歌曲在歌单中的排序（数值越小越靠前）
     */
    private Integer sort;

    /**
     * 添加时间
     */
    private LocalDateTime addTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Long playlistId) {
        this.playlistId = playlistId;
    }

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    @Override
    public String toString() {
        return "PlaylistSong{" +
            "id = " + id +
            ", playlistId = " + playlistId +
            ", songId = " + songId +
            ", sort = " + sort +
            ", addTime = " + addTime +
            "}";
    }
}
