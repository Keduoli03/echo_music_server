package com.lanke.echomusic.controller;

import com.lanke.echomusic.common.Result;
import com.lanke.echomusic.entity.PlaylistCategory;
import com.lanke.echomusic.service.IPlaylistCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 歌单分类管理控制器
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
@RestController
@RequestMapping("/api/admin/playlistCategory")
public class PlaylistCategoryController {

    @Resource
    private IPlaylistCategoryService playlistCategoryService;

    @Operation(summary = "获取所有歌单分类")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("/list")
    public Result<List<PlaylistCategory>> getAllCategories() {
        List<PlaylistCategory> categories = playlistCategoryService.getAllCategories();
        return Result.success("获取成功", categories);
    }

    @Operation(summary = "添加歌单分类")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("/add")
    public Result<Void> addCategory(@RequestBody PlaylistCategory category) {
        boolean success = playlistCategoryService.save(category);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    @Operation(summary = "更新歌单分类")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("/update")
    public Result<Void> updateCategory(@RequestBody PlaylistCategory category) {
        boolean success = playlistCategoryService.updateById(category);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    @Operation(summary = "删除歌单分类")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("/delete/{id}")
    public Result<Void> deleteCategory(@PathVariable Integer id) {
        boolean success = playlistCategoryService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}