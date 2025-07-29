package com.lanke.echomusic.controller;

import com.lanke.echomusic.common.Result;
import com.lanke.echomusic.dto.album.AlbumInfoDTO;
import com.lanke.echomusic.dto.album.AlbumSearchDTO;
import com.lanke.echomusic.entity.Album;
import com.lanke.echomusic.service.IAlbumService;
import com.lanke.echomusic.vo.album.AlbumPageVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>专辑表 前端控制器</p>
 *
 * @author lanke
 * @since 2025-06-02
 */
@RestController
@Tag(name = "专辑管理", description = "专辑信息维护")
@RequestMapping("/api/album")
public class AlbumController {

    @Autowired
    private IAlbumService albumService;

    // ====================== 创建专辑 ======================
    @Operation(summary = "创建专辑")
    @PostMapping("/create")
    public Result<Long> createAlbum(@RequestBody AlbumInfoDTO dto) {
        Long albumId = albumService.createAlbum(dto);
        return Result.success("专辑创建成功", albumId);
    }

    // ====================== 查询专辑 ======================
    @Operation(summary = "获取专辑列表", description = "支持分页、条件筛选的专辑查询接口")
    @GetMapping("/getAlbumList")
    public Result<AlbumPageVO> getAlbumsPage(@ParameterObject AlbumSearchDTO searchDTO) {
        AlbumPageVO result = albumService.getAlbumsPage(searchDTO);
        return Result.success("获取成功", result);
    }
    
    // 如果需要保留原有的不分页接口，可以重命名
    @Operation(summary = "获取所有专辑列表（不分页）")
    @GetMapping("/getAllAlbums")
    public Result<List<AlbumInfoDTO>> getAllAlbums() {
        List<AlbumInfoDTO> dtoList = albumService.getAllAlbumsWithSingerName();
        return Result.success("查询成功", dtoList);
    }

    @Operation(summary = "根据ID查询专辑")
    @GetMapping("/{id}")
    public Result<AlbumInfoDTO> getAlbumById(@PathVariable Long id) {
        Album album = albumService.getById(id);
        if (album == null) {
            return Result.error("专辑不存在");
        }
        AlbumInfoDTO dto = new AlbumInfoDTO();
        BeanUtils.copyProperties(album, dto);
        return Result.success("查询成功", dto);
    }

    @Operation(summary = "根据歌手ID查询专辑列表")
    @GetMapping("/singer/{singerId}")
    public Result<List<AlbumInfoDTO>> getAlbumsBySingerId(@PathVariable Long singerId) {
        List<Album> albumList = albumService.getAlbumsBySingerId(singerId);
        List<AlbumInfoDTO> dtoList = albumList.stream()
                .map(album -> {
                    AlbumInfoDTO dto = new AlbumInfoDTO();
                    BeanUtils.copyProperties(album, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        return Result.success("查询成功", dtoList);
    }

    @Operation(summary = "修改专辑信息", description = "根据ID更新专辑信息")
    @PutMapping("/update")
    public Result<Boolean> updateAlbum(@RequestBody AlbumInfoDTO dto) {
        try {
            boolean success = albumService.updateAlbum(dto);
            return success ?
                    Result.success("专辑信息更新成功") :
                    Result.error("专辑信息更新失败");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @Operation(summary = "删除专辑", description = "根据ID删除专辑信息")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteAlbum(@PathVariable Long id) {
        try {
            boolean success = albumService.deleteAlbum(id);
            return success ?
                    Result.success("专辑删除成功") :
                    Result.error("专辑删除失败");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @Operation(summary = "上传专辑封面", description = "为指定专辑上传封面图片（支持JPG/PNG等格式）")
    @PostMapping(value = "/upload/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadAlbumCover(
            @RequestParam("albumId") Long albumId,   // 专辑ID
            @RequestParam("coverFile") MultipartFile file // 封面文件
    ) {
        try {
            // 基础校验
            if (albumId == null || albumId <= 0) {
                return Result.error(400, "专辑ID不能为空");
            }
            if (file.isEmpty()) {
                return Result.error(400, "请选择要上传的封面文件");
            }
            if (!isValidImageType(file)) {
                return Result.error(400, "仅支持JPG、PNG、GIF格式");
            }

            // 调用服务层上传封面
            String coverUrl = albumService.updateAlbumCover(albumId, file);
            return Result.success("封面上传成功", coverUrl);
        } catch (Exception e) {
            return Result.error(500, "封面上传失败：" + e.getMessage());
        }
    }

    /**
     * 校验图片类型
     */
    private boolean isValidImageType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.startsWith("image/jpeg") ||
                        contentType.startsWith("image/png") ||
                        contentType.startsWith("image/gif")
        );
    }

}