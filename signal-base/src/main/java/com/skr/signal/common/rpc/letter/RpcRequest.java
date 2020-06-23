package com.skr.signal.common.rpc.letter;

import lombok.Data;

/**
 * @author mqw
 * @create 2020-06-22-12:53
 */
@Data
public class RpcRequest {

    String traceId;
    String className;
    String methodName;
    Object[] parameters;
    Class<?>[] parameterTypes;

}
