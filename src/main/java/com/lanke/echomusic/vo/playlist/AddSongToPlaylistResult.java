package com.lanke.echomusic.vo.playlist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "添加歌曲到歌单结果")
public class AddSongToPlaylistResult {
    @Schema(description = "成功添加的歌曲数量")
    private Integer addedCount;
    
    @Schema(description = "重复的歌曲数量")
    private Integer duplicateCount;
    
    @Schema(description = "重复的歌曲ID列表")
    private List<Long> duplicateSongIds;
    
    @Schema(description = "操作结果消息")
    private String message;
    
    public AddSongToPlaylistResult(Integer addedCount, Integer duplicateCount, List<Long> duplicateSongIds) {
        this.addedCount = addedCount;
        this.duplicateCount = duplicateCount;
        this.duplicateSongIds = duplicateSongIds;
        
        if (duplicateCount > 0 && addedCount > 0) {
            this.message = String.format("成功添加%d首歌曲，%d首歌曲已存在于歌单中", addedCount, duplicateCount);
        } else if (duplicateCount > 0) {
            this.message = "所有歌曲都已存在于歌单中";
        } else {
            this.message = String.format("成功添加%d首歌曲到歌单", addedCount);
        }
    }
}