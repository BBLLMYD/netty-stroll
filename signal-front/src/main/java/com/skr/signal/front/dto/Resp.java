package com.skr.signal.front.dto;

import com.skr.signal.front.content.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author mqw
 * @create 2020-06-09-13:46
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Resp<T> implements Serializable {
    private static final String SUCCESS = "success";

    private boolean success;
    private int errCode;
    private String errMsg;
    private T data;

    public static <T> Resp<T> success(T data) {
        return new Resp<>(true,Constant.SUCCESS, SUCCESS, data);
    }

    public static <T> Resp<T> success() {
        return new Resp<>(true,Constant.SUCCESS, SUCCESS, null);
    }

    public static <T> Resp<T> error(Integer code, String message) {
        return new Resp<T>(false,code, message, null);
    }

}
