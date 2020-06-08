package com.skr.signal.core.dto;

import lombok.Data;

/**
 * @author mqw
 * @create 2020-06-08-18:23
 */
@Data
public class RpcRequest {

    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

}
