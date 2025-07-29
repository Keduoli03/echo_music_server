package com.lanke.echomusic.service.impl;

import com.lanke.echomusic.entity.SongSinger;
import com.lanke.echomusic.mapper.SongSingerMapper;
import com.lanke.echomusic.service.ISongSingerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 歌曲-歌手关联表 服务实现类
 * </p>
 *
 * @author lanke
 * @since 2025-06-02
 */
@Service
public class SongSingerServiceImpl extends ServiceImpl<SongSingerMapper, SongSinger> implements ISongSingerService {

}
