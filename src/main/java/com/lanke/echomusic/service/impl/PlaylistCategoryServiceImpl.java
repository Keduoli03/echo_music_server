package com.lanke.echomusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanke.echomusic.entity.PlaylistCategory;
import com.lanke.echomusic.mapper.PlaylistCategoryMapper;
import com.lanke.echomusic.service.IPlaylistCategoryService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 歌单分类配置表 服务实现类
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
@Service
public class PlaylistCategoryServiceImpl extends ServiceImpl<PlaylistCategoryMapper, PlaylistCategory> implements IPlaylistCategoryService {

    @Override
    public List<Map<String, Object>> getAllActiveCategories() {
        List<PlaylistCategory> categories = this.list(
            new LambdaQueryWrapper<PlaylistCategory>()
                .eq(PlaylistCategory::getIsActive, true)
                .orderByAsc(PlaylistCategory::getSortOrder)
        );
        
        return categories.stream()
            .map(category -> {
                Map<String, Object> map = new HashMap<>();
                map.put("value", category.getId());
                map.put("label", category.getName());
                map.put("description", category.getDescription() != null ? category.getDescription() : "");
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<PlaylistCategory> getAllCategories() {
        return this.list(
            new LambdaQueryWrapper<PlaylistCategory>()
                .orderByAsc(PlaylistCategory::getSortOrder)
        );
    }
}