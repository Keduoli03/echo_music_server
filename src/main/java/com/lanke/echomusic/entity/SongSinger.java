package com.lanke.echomusic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * <p>
 * 歌曲-歌手关联表
 * </p>
 *
 * @author lanke
 * @since 2025-06-02
 */
@TableName("l_song_singer")
public class SongSinger implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 歌曲ID
     */
    private Long songId;

    /**
     * 歌手ID
     */
    private Long singerId;

    /**
     * 歌手类型（1-主唱，2-合唱，3-伴奏）
     */
    private Integer singerType;

    /**
     * 排序（多个歌手时的显示顺序）
     */
    private Integer sort;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public Long getSingerId() {
        return singerId;
    }

    public void setSingerId(Long singerId) {
        this.singerId = singerId;
    }

    public Integer getSingerType() {
        return singerType;
    }

    public void setSingerType(Integer singerType) {
        this.singerType = singerType;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "SongSinger{" +
            "id = " + id +
            ", songId = " + songId +
            ", singerId = " + singerId +
            ", singerType = " + singerType +
            ", sort = " + sort +
            "}";
    }
}
