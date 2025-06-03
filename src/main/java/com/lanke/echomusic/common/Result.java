package com.lanke.echomusic.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    //------------------------ 成功系列 ------------------------
    @JsonIgnore
    public boolean isSuccess() {
        return code == 200;
    }
    // 不带数据的成功
    public static <T> Result<T> success() {
        return new Result<>(HttpStatus.OK.value(), "操作成功", null);
    }

    // 带数据的成功
    public static <T> Result<T> success(T data) {
        return new Result<>(HttpStatus.OK.value(), "操作成功", data);
    }

    // 自定义消息的成功
    public static <T> Result<T> success(String msg) {
        return new Result<>(HttpStatus.OK.value(), msg, null);
    }

    // 带数据和自定义消息的成功
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(HttpStatus.OK.value(), msg, data);
    }

    //------------------------ 错误系列 ------------------------
    // 不带数据的错误
    public static <T> Result<T> error() {
        return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "操作失败", null);
    }

    // 带错误消息的错误
    public static <T> Result<T> error(String msg) {
        return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg, null);
    }

    // 带数据的错误（用于返回错误详情）
    public static <T> Result<T> error(String msg, T data) {
        return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg, data);
    }

    // 自定义状态码和消息的错误
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }
}
