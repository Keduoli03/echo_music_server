package com.lanke.echomusic.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * 音频文件处理工具类 (使用 JAVE2 库)
 */
public class AudioUtils {

    private static final Logger log = LoggerFactory.getLogger(AudioUtils.class);

    /**
     * 使用 JAVE2 解析音频文件时长（单位：秒）
     */
    public static Integer parseAudioDuration(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("文件为空，无法解析时长");
            return 0;
        }

        File tempFile = null;
        try {
            // 1. 创建临时文件
            tempFile = createTempFile(file);

            // 2. 使用 JAVE2 解析时长
            return parseDurationWithJave(tempFile);
        } catch (Exception e) {
            log.error("解析音频时长失败: {}", e.getMessage());
            return 0;
        } finally {
            // 3. 清理临时文件
            deleteTempFile(tempFile);
        }
    }

    /**
     * 创建临时文件
     */
    private static File createTempFile(MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".tmp";

        File tempFile = File.createTempFile("audio-", extension);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFile;
    }

    /**
     * 使用 JAVE2 解析时长
     */
    private static Integer parseDurationWithJave(File file) {
        try {
            MultimediaObject multimediaObject = new MultimediaObject(file);
            MultimediaInfo info = multimediaObject.getInfo();

            if (info != null) {
                // 获取时长（毫秒）并转换为秒
                long durationInMillis = info.getDuration();
                int durationInSeconds = (int) (durationInMillis / 1000);

                log.info("解析到音频时长: {}秒 [{}]", durationInSeconds, file.getName());
                return durationInSeconds;
            }
        } catch (Exception e) {
            log.error("JAVE2 解析失败: {}", e.getMessage());
        }
        return 0;
    }

    /**
     * 清理临时文件
     */
    private static void deleteTempFile(File tempFile) {
        if (tempFile != null && tempFile.exists()) {
            try {
                boolean deleted = tempFile.delete();
                if (!deleted) {
                    log.warn("无法删除临时文件: {}", tempFile.getAbsolutePath());
                }
            } catch (Exception e) {
                log.error("删除临时文件失败: {}", e.getMessage());
            }
        }
    }
}
