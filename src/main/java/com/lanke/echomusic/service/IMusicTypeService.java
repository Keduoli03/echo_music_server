package com.lanke.echomusic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lanke.echomusic.entity.MusicType;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 音乐类型配置表 服务类
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
public interface IMusicTypeService extends IService<MusicType> {
    
    /**
     * 获取所有启用的音乐类型
     */
    List<Map<String, Object>> getAllActiveMusicTypes();
    
    /**
     * 获取所有音乐类型（包括禁用的）
     */
    List<MusicType> getAllMusicTypes();
}