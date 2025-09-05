package com.lanke.echomusic.controller;

import com.lanke.echomusic.common.Result;
import com.lanke.echomusic.dto.banner.BannerSearchDTO;
import com.lanke.echomusic.entity.Banner;
import com.lanke.echomusic.service.IBannerService;
import com.lanke.echomusic.vo.banner.BannerPageVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * Banner轮播图控制器
 * </p>
 *
 * @author lanke
 * @since 2025-01-21
 */
@RestController
@RequestMapping("/api/banner")
@Tag(name = "Banner管理", description = "Banner轮播图相关接口")
public class BannerController {

    @Autowired
    private IBannerService bannerService;

    /**
     * 获取启用的Banner列表
     */
    @GetMapping("/getActiveBanners")
    @Operation(summary = "获取启用的Banner列表", description = "获取所有启用状态的Banner，按排序顺序返回")
    public Result<List<Banner>> getActiveBanners() {
        List<Banner> banners = bannerService.getActiveBanners();
        return Result.success("获取成功", banners);
    }

    /**
     * 分页查询Banner列表
     */
    @GetMapping("/getBannerList")
    @Operation(summary = "分页查询Banner列表", description = "支持按标题、描述、类型、状态等条件分页查询Banner，所有参数可选")
    public Result<BannerPageVO> getBannerList(
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "Banner标题（模糊查询）") @RequestParam(required = false) String title,
            @Parameter(description = "Banner描述（模糊查询）") @RequestParam(required = false) String description,
            @Parameter(description = "跳转类型") @RequestParam(required = false) String type,
            @Parameter(description = "Banner状态（1-启用，0-禁用）") @RequestParam(required = false) Integer isActive,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "sort,asc") String orderBy) {
        
        // 构建searchDTO
        BannerSearchDTO searchDTO = new BannerSearchDTO();
        searchDTO.setCurrent(current);
        searchDTO.setSize(size);
        searchDTO.setTitle(title);
        searchDTO.setDescription(description);
        searchDTO.setType(type);
        searchDTO.setIsActive(isActive);
        searchDTO.setOrderBy(orderBy);
        
        BannerPageVO pageVO = bannerService.getBannersPage(searchDTO);
        return Result.success("获取成功", pageVO);
    }

    /**
     * 根据ID获取Banner详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取Banner详情", description = "根据Banner ID获取详细信息")
    public Result<Banner> getBannerById(
            @Parameter(description = "Banner ID", required = true)
            @PathVariable Long id) {
        Banner banner = bannerService.getById(id);
        if (banner == null) {
            return Result.error(404, "Banner不存在");
        }
        return Result.success("获取成功", banner);
    }

    /**
     * 创建Banner
     */
    @PostMapping("/create")
    @Operation(summary = "创建Banner", description = "创建新的Banner轮播图")
    public Result<Banner> createBanner(@RequestBody Banner banner) {
        try {
            Banner createdBanner = bannerService.createBanner(banner);
            return Result.success("创建成功", createdBanner);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "创建失败：" + e.getMessage());
        }
    }

    /**
     * 更新Banner
     */
    @PutMapping("/update")
    @Operation(summary = "更新Banner", description = "更新Banner信息")
    public Result<Banner> updateBanner(@RequestBody Banner banner) {
        try {
            Banner updatedBanner = bannerService.updateBanner(banner);
            return Result.success("更新成功", updatedBanner);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "更新失败：" + e.getMessage());
        }
    }

    /**
     * 启用/禁用Banner
     */
    @PutMapping("/toggle/{id}")
    @Operation(summary = "启用/禁用Banner", description = "切换Banner的启用状态")
    public Result<Void> toggleBannerStatus(
            @Parameter(description = "Banner ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "是否启用（1-启用，0-禁用）", required = true)
            @RequestParam Integer isActive) {
        try {
            bannerService.toggleBannerStatus(id, isActive);
            String message = isActive == 1 ? "Banner已启用" : "Banner已禁用";
            return Result.success(message);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "操作失败：" + e.getMessage());
        }
    }

    /**
     * 删除Banner
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除Banner", description = "根据ID删除Banner")
    public Result<Void> deleteBanner(
            @Parameter(description = "Banner ID", required = true)
            @PathVariable Long id) {
        try {
            boolean success = bannerService.removeById(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error(404, "Banner不存在或删除失败");
            }
        } catch (Exception e) {
            return Result.error(500, "删除失败：" + e.getMessage());
        }
    }

    /**
     * 上传Banner图片
     */
    @PostMapping("/upload/image")
    @Operation(summary = "上传Banner图片", description = "上传Banner轮播图片")
    public Result<String> uploadBannerImage(
            @Parameter(description = "图片文件", required = true)
            @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = bannerService.uploadBannerImage(file);
            return Result.success("上传成功", imageUrl);
        } catch (Exception e) {
            return Result.error(500, "图片上传失败: " + e.getMessage());
        }
    }
}