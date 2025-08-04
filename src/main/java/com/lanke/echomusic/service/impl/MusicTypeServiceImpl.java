package com.lanke.echomusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanke.echomusic.entity.MusicType;
import com.lanke.echomusic.mapper.MusicTypeMapper;
import com.lanke.echomusic.service.IMusicTypeService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 音乐类型配置表 服务实现类
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
@Service
public class MusicTypeServiceImpl extends ServiceImpl<MusicTypeMapper, MusicType> implements IMusicTypeService {

    @Override
    public List<Map<String, Object>> getAllActiveMusicTypes() {
        List<MusicType> musicTypes = this.list(
            new LambdaQueryWrapper<MusicType>()
                .eq(MusicType::getIsActive, true)
                .orderByAsc(MusicType::getSortOrder)
        );
        
        return musicTypes.stream()
            .map(type -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", type.getId());  // 改为 id
                map.put("musicTypeName", type.getName());  // 改为 musicTypeName
                map.put("description", type.getDescription() != null ? type.getDescription() : "");
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<MusicType> getAllMusicTypes() {
        return this.list(
            new LambdaQueryWrapper<MusicType>()
                .orderByAsc(MusicType::getSortOrder)
        );
    }
}