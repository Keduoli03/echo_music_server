package com.lanke.echomusic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lanke.echomusic.dto.banner.BannerSearchDTO;
import com.lanke.echomusic.entity.Banner;
import com.lanke.echomusic.vo.banner.BannerPageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * Banner轮播图服务接口
 * </p>
 *
 * @author lanke
 * @since 2025-01-21
 */
public interface IBannerService extends IService<Banner> {

    /**
     * 获取启用的Banner列表（按排序顺序）
     * @return Banner列表
     */
    List<Banner> getActiveBanners();

    /**
     * 分页查询Banner列表
     * @param searchDTO 查询参数
     * @return 分页结果
     */
    BannerPageVO getBannersPage(BannerSearchDTO searchDTO);

    /**
     * 创建Banner
     * @param banner Banner信息
     * @return 创建的Banner
     */
    Banner createBanner(Banner banner);

    /**
     * 更新Banner
     * @param banner Banner信息
     * @return 更新的Banner
     */
    Banner updateBanner(Banner banner);

    /**
     * 启用/禁用Banner
     * @param id Banner ID
     * @param isActive 是否启用
     */
    void toggleBannerStatus(Long id, Integer isActive);

    /**
     * 验证跳转目标是否存在
     * @param type 跳转类型（music/album/playlist）
     * @param linkId 跳转目标ID
     * @return 是否存在
     */
    boolean validateLinkTarget(String type, Long linkId);

    /**
     * 上传Banner图片
     * @param file 图片文件
     * @return 图片URL
     */
    String uploadBannerImage(MultipartFile file);
}