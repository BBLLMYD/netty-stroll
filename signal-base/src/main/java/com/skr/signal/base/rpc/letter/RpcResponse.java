package com.skr.signal.base.rpc.letter;

import lombok.Data;

/**
 * @author mqw
 * @create 2020-06-22-13:08
 */
@Data
public class RpcResponse {

    private String traceId;
    private String error;
    private Object result;

}
