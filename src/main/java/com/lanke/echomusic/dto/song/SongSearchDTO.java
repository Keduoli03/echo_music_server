package com.lanke.echomusic.dto.song;

import lombok.Data;

@Data
public class SongSearchDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    // 替换ID为名称查询
    private String songNameLike;        // 歌曲名模糊查询
    private String singerNameLike;      // 歌手名模糊查询（代替singerId）
    private String albumNameLike;       // 专辑名模糊查询（代替albumId）
    private Integer durationMin;
    private Integer durationMax;
    private String orderBy;
}