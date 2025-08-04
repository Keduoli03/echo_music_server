package com.lanke.echomusic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 歌曲表
 * </p>
 *
 * @author lanke
 * @since 2025-06-02
 */
@TableName("l_song")
public class Song implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 歌曲ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 歌曲名称
     */
    private String name;

    /**
     * 原名
     */
    private String originalName;

    /**
     * 作词
     */
    private String lyricist;

    /**
     * 作曲
     */
    private String composer;

    /**
     * 编曲
     */
    private String arranger;

    /**
     * 所属专辑ID
     */
    private Long albumId;

    /**
     * 时长（秒）
     */
    private Integer duration;

    /**
     * 发行日期
     */
    private LocalDate releaseDate;

    /**
     * 语言
     */
    private String language;

    /**
     * 风格/流派
     */
    private String genre;

    /**
     * 音乐类型ID（关联l_music_type表）
     */
    private Integer musicType;

    /**
     * 歌词
     */
    private String lyrics;

    /**
     * 播放URL
     */
    private String playUrl;

    /**
     * 封面URL
     */
    private String coverUrl;

    /**
     * 状态（1-启用，0-禁用）
     */
    private Integer status;

    /**
     * 播放次数
     */
    private Long playCount;

    /**
     * 收藏/喜欢次数
     */
    private Long likeCount;

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

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getLyricist() {
        return lyricist;
    }

    public void setLyricist(String lyricist) {
        this.lyricist = lyricist;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getArranger() {
        return arranger;
    }

    public void setArranger(String arranger) {
        this.arranger = arranger;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getMusicType() {
        return musicType;
    }

    public void setMusicType(Integer musicType) {
        this.musicType = musicType;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Long playCount) {
        this.playCount = playCount;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
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
        return "Song{" +
            "id = " + id +
            ", name = " + name +
            ", originalName = " + originalName +
            ", lyricist = " + lyricist +
            ", composer = " + composer +
            ", arranger = " + arranger +
            ", albumId = " + albumId +
            ", duration = " + duration +
            ", releaseDate = " + releaseDate +
            ", language = " + language +
            ", genre = " + genre +
            ", lyrics = " + lyrics +
            ", playUrl = " + playUrl +
            ", coverUrl = " + coverUrl +
            ", status = " + status +
            ", playCount = " + playCount +
            ", likeCount = " + likeCount +
            ", createdAt = " + createdAt +
            ", updatedAt = " + updatedAt +
            "}";
    }
}
