package com.lanke.echomusic.controller;

import com.lanke.echomusic.common.Result;
import com.lanke.echomusic.dto.singer.SingerInfoDTO;
import com.lanke.echomusic.dto.singer.SingerSearchDTO;
import com.lanke.echomusic.entity.Singer;
import com.lanke.echomusic.service.ISingerService;
import com.lanke.echomusic.vo.singer.SingerPageVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "歌手管理", description = "歌手信息维护")
@RequestMapping("/api/singer")
public class SingerController {

    @Autowired
    private ISingerService singerService;

    @Operation(summary = "创建歌手", description = "增加歌手")
    @PostMapping("/create")
    public Result<Long> createSinger(@RequestBody SingerInfoDTO dto) {
        try {
            Long singerId = singerService.createSinger(dto);
            return Result.success("歌手创建成功", singerId);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 删除了 getSingerByName 和 getSinger 方法

    @Operation(summary = "修改歌手信息", description = "根据ID更新歌手信息")
    @PutMapping("/update")
    public Result<Boolean> updateSinger(@RequestBody SingerInfoDTO dto) {
        try {
            boolean success = singerService.updateSinger(dto);
            return success ?
                    Result.success("歌手信息更新成功") :
                    Result.error("歌手信息更新失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "删除歌手", description = "根据ID删除歌手信息")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteSinger(@PathVariable Long id) {
        try {
            boolean success = singerService.deleteSinger(id);
            return success ?
                    Result.success("歌手删除成功") :
                    Result.error("歌手删除失败");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @Operation(summary = "上传歌手头像", description = "为指定歌手上传头像图片")
    @PostMapping(value = "/upload/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadSingerAvatar(
            @RequestParam("singerId") Long singerId,
            @RequestParam("avatarFile") MultipartFile file
    ) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            if (!isValidImageType(file)) {
                return Result.error("文件格式不支持，请上传JPG、PNG或GIF格式的图片");
            }

            String avatarUrl = singerService.updateSingerAvatar(singerId, file);
            return Result.success("头像上传成功", avatarUrl);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    private boolean isValidImageType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.startsWith("image/jpeg") ||
                        contentType.startsWith("image/png") ||
                        contentType.startsWith("image/gif")
        );
    }

    @Operation(summary = "获取歌手列表", description = "支持分页、条件筛选和多字段排序的歌手查询接口")
    @GetMapping("/getSingerList")
    public Result<SingerPageVO> getSingersPage(@ParameterObject SingerSearchDTO dto) {
        SingerPageVO result = singerService.getSingersPage(dto);
        return Result.success("获取成功", result);
    }
}