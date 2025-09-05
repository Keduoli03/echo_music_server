package com.lanke.echomusic.enums;

/**
 * 评论目标类型枚举
 */
public enum CommentTargetType {
    SONG("song", "歌曲"),
    PLAYLIST("playlist", "歌单"),
    ALBUM("album", "专辑");

    private final String code;
    private final String description;

    CommentTargetType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static CommentTargetType fromCode(String code) {
        for (CommentTargetType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown comment target type: " + code);
    }
}