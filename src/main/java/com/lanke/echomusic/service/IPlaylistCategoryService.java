package com.lanke.echomusic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lanke.echomusic.entity.PlaylistCategory;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 歌单分类配置表 服务类
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
public interface IPlaylistCategoryService extends IService<PlaylistCategory> {
    
    /**
     * 获取所有启用的歌单分类
     */
    List<Map<String, Object>> getAllActiveCategories();
    
    /**
     * 获取所有歌单分类（包括禁用的）
     */
    List<PlaylistCategory> getAllCategories();
}