package com.lanke.echomusic.controller;

import com.lanke.echomusic.common.Result;
import com.lanke.echomusic.entity.MusicType;
import com.lanke.echomusic.service.IMusicTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 音乐类型管理控制器
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
@RestController
@RequestMapping("/api/admin/musicType")
public class MusicTypeController {

    @Resource
    private IMusicTypeService musicTypeService;

    @Operation(summary = "获取所有音乐类型")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("/list")
    public Result<List<MusicType>> getAllMusicTypes() {
        List<MusicType> musicTypes = musicTypeService.getAllMusicTypes();
        return Result.success("获取成功", musicTypes);
    }

    @Operation(summary = "添加音乐类型")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("/add")
    public Result<Void> addMusicType(@RequestBody MusicType musicType) {
        boolean success = musicTypeService.save(musicType);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    @Operation(summary = "更新音乐类型")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("/update")
    public Result<Void> updateMusicType(@RequestBody MusicType musicType) {
        boolean success = musicTypeService.updateById(musicType);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    @Operation(summary = "删除音乐类型")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("/delete/{id}")
    public Result<Void> deleteMusicType(@PathVariable Integer id) {
        boolean success = musicTypeService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}