package com.lanke.echomusic.enums;

/**
 * 评论状态枚举
 */
public enum CommentStatus {
    DELETED(0, "已删除"),
    NORMAL(1, "正常"),
    PENDING(2, "审核中");

    private final Integer code;
    private final String description;

    CommentStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static CommentStatus fromCode(Integer code) {
        for (CommentStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown comment status: " + code);
    }
}