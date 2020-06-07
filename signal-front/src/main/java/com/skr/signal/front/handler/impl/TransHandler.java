package com.skr.signal.front.handler.impl;

import com.skr.signal.front.anno.HandlerTag;
import com.skr.signal.front.dto.RequestInfo;
import com.skr.signal.front.dto.ResponseInfo;
import com.skr.signal.front.handler.Handler;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author mqw
 * @create 2020-06-05-10:12
 */
@HandlerTag(path = "/front")
public class TransHandler extends Handler<RequestInfo, ResponseInfo> {

    @Override
    public ResponseInfo handle(RequestInfo requestInfo) {
        ResponseInfo info = new ResponseInfo();
        info.setAnswer("answer312312312");
        return info;
    }

}
