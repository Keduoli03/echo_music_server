package com.lanke.echomusic.dto.song;

import lombok.Data;

@Data
public class SongSearchDTO {
    private long current = 1;
    private long size = 10;

    // 替换ID为名称查询
    private String songName;        // 歌曲名模糊查询
    private String singerName;      // 歌手名模糊查询（代替singerId）
    private String albumName;       // 专辑名模糊查询（代替albumId）
    private Integer durationMin;
    private Integer durationMax;
    private String orderBy;
}