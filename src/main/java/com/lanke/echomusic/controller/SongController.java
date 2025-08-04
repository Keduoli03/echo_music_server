package com.lanke.echomusic.controller;

import com.lanke.echomusic.common.Result;
import com.lanke.echomusic.dto.song.SongDetailDTO;
import com.lanke.echomusic.dto.song.SongInfoDTO;
import com.lanke.echomusic.dto.song.SongSearchDTO;
import com.lanke.echomusic.dto.song.UpdateSongDTO;
import com.lanke.echomusic.service.IUserLikeService;
import com.lanke.echomusic.utils.RequestProcessor;
import com.lanke.echomusic.vo.song.SongPageVO;
import com.lanke.echomusic.annotation.OperationLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import com.lanke.echomusic.service.IMusicTypeService;
import com.lanke.echomusic.service.ISongService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 歌曲表 前端控制器
 * </p>
 *
 * @author lanke
 * @since 2025-06-02
 */

@Controller
@Tag(name = "歌曲信息接口", description = "提供增加歌曲、播放歌曲等功能")
@RestController
@RequestMapping("/api/song")
public class SongController {

    @Autowired
    private ISongService songService;

    @Autowired
    private IMusicTypeService musicTypeService;

    @Operation(summary = "创建歌曲信息", description = "创建新歌曲的基本信息，不包含文件上传")
    @OperationLog(
        module = "歌曲管理", 
        operationType = "新增", 
        description = "创建歌曲：#{#songInfoDTO.songName}，歌手：#{#songInfoDTO.singerName}"
    )
    @PostMapping("/create")
    public Result<?> createSong(@RequestBody @Valid SongInfoDTO songInfoDTO) {
        try {
            // 调用服务层创建歌曲
            Long songId = songService.createSong(songInfoDTO);
    
            // 返回成功结果
            return Result.success("歌曲创建成功", songId);
        } catch (Exception e) {
            // 处理异常
            return Result.error("歌曲创建失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取歌曲信息", description = "获取歌曲信息，包括对应的歌手、专辑信息")
    @GetMapping("/detail/{id}")
    public Result<SongDetailDTO> getSongDetail(@PathVariable Long id) {
        SongDetailDTO detail = songService.getSongDetail(id);
        return Result.success("查询成功", detail);
    }


    @Operation(summary = "上传歌曲文件", description = "为指定歌曲上传音频文件（支持MP3/WAV等格式）")
    @PostMapping(value = "/upload/music", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadSongFile(
            @RequestParam("songId") Long songId,    // 关联的歌曲ID
            @RequestParam("file") MultipartFile file // 歌曲文件
    ) {
        try {
            // 1. 基础校验
            if (songId == null || songId <= 0) {
                return Result.error(400, "歌曲ID不能为空");
            }
            if (file.isEmpty()) {
                return Result.error(400, "请选择要上传的歌曲文件");
            }
            if (!isValidAudioType(file)) {
                return Result.error(400, "仅支持MP3、WAV、FLAC格式");
            }

            // 2. 调用服务层处理上传
            songService.updateSongPlayUrl(songId, file);
            return Result.success("歌曲文件上传成功");
        } catch (Exception e) {
            return Result.error(500, "上传失败：" + e.getMessage());
        }
    }

    /**
     * 校验音频文件类型（可扩展支持更多格式）
     */
    private boolean isValidAudioType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("audio/mpeg") ||   // MP3
                        contentType.equals("audio/wav") ||    // WAV
                        contentType.equals("audio/flac")     // FLAC
        );
    }


    /**
     * 删除歌曲（包括关联数据和文件）
     */
    @Operation(summary = "删除歌曲", description = "根据ID删除歌曲及其关联的文件和数据")
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteSong(@PathVariable Long id) {
        try {
            songService.deleteSong(id);
            return Result.success("歌曲删除成功");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @Operation(summary = "上传歌曲封面", description = "为指定歌曲上传封面图片（支持JPG/PNG等格式）")
    @PostMapping(value = "/upload/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadSongCover(
            @RequestParam("songId") Long songId,       // 关联的歌曲ID
            @RequestParam("coverFile") MultipartFile file // 封面文件
    ) {
        try {
            // 1. 基础校验
            if (songId == null || songId <= 0) {
                return Result.error(400, "歌曲ID不能为空");
            }
            if (file.isEmpty()) {
                return Result.error(400, "请选择要上传的封面文件");
            }
            if (!isValidImageType(file)) {
                return Result.error(400, "仅支持JPG、PNG、GIF格式");
            }

            // 2. 调用服务层处理上传
            String coverUrl = songService.updateSongCover(songId, file);
            return Result.success("封面上传成功", coverUrl);
        } catch (Exception e) {
            return Result.error(500, "封面上传失败：" + e.getMessage());
        }
    }

    /**
     * 校验图片文件类型
     */
    private boolean isValidImageType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.startsWith("image/jpeg") ||
                        contentType.startsWith("image/png") ||
                        contentType.startsWith("image/gif")
        );
    }

    @Operation(summary = "获取歌曲列表", description = "支持分页、条件筛选和多字段排序的歌曲查询接口")
    @GetMapping("/getSongList")
    public Result<SongPageVO> getSongsPage(@ParameterObject SongSearchDTO dto) {
        SongPageVO result = songService.searchSongs(dto);
        return Result.success("获取成功", result);
    }

    @Operation(summary = "更新歌曲信息", description = "更新歌曲的基本信息，支持专辑名称更新，不包括封面和音频文件")
    @PutMapping("/update")
    public Result<?> updateSongInfo(@RequestBody @Valid UpdateSongDTO updateSongDTO) {
        try {
            boolean success = songService.updateSongInfo(updateSongDTO);
            return success ? 
                Result.success("歌曲信息更新成功") : 
                Result.error("歌曲信息更新失败");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @Autowired
    private IUserLikeService userLikeService;
    
    @Autowired
    private RequestProcessor requestProcessor;

    /**
     * 用户喜欢/取消喜欢歌曲
     */
    @PostMapping("/like/{songId}")
    @Operation(summary = "喜欢/取消喜欢歌曲", description = "用户喜欢或取消喜欢指定歌曲")
    @SecurityRequirement(name = "Authorization")
    @OperationLog(module = "歌曲管理", operationType = "喜欢操作", description = "用户喜欢/取消喜欢歌曲")
    public Result<Boolean> toggleLike(
            @Parameter(description = "歌曲ID", required = true)
            @PathVariable Long songId) {
        Long userId = requestProcessor.getUserId();
        boolean isLiked = userLikeService.toggleLike(userId, songId);
        return Result.success(isLiked ? "喜欢成功" : "取消喜欢成功", isLiked);
    }

    /**
     * 检查用户是否喜欢某首歌曲
     */
    @GetMapping("/like/check/{songId}")
    @Operation(summary = "检查是否喜欢歌曲", description = "检查当前用户是否喜欢指定歌曲")
    @SecurityRequirement(name = "Authorization")
    public Result<Boolean> checkLike(
            @Parameter(description = "歌曲ID", required = true)
            @PathVariable Long songId) {
        Long userId = requestProcessor.getUserId();
        boolean isLiked = userLikeService.isLiked(userId, songId);
        return Result.success(isLiked);
    }

    /**
     * 获取用户喜欢的歌曲列表
     */
    @GetMapping("/like/list")
    @Operation(summary = "获取用户喜欢的歌曲列表", description = "分页获取当前用户喜欢的歌曲列表")
    @SecurityRequirement(name = "Authorization")
    public Result<?> getUserLikedSongs(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = requestProcessor.getUserId();
        return Result.success(userLikeService.getUserLikedSongs(userId, current, size));
    }

    /**
     * 获取用户喜欢的歌曲ID列表
     */
    @GetMapping("/like/ids")
    @Operation(summary = "获取用户喜欢的歌曲ID列表", description = "获取当前用户喜欢的所有歌曲ID")
    @SecurityRequirement(name = "Authorization")
    public Result<?> getUserLikedSongIds() {
        Long userId = requestProcessor.getUserId();
        return Result.success(userLikeService.getUserLikedSongIds(userId));
    }

    @Operation(summary = "获取音乐类型列表", description = "获取所有可用的音乐类型")
    @GetMapping("/musicTypes")
    public Result<List<Map<String, Object>>> getAllMusicTypes() {
        List<Map<String, Object>> musicTypes = musicTypeService.getAllActiveMusicTypes();
        return Result.success("获取成功", musicTypes);
    }
}
