package com.skr.signal.front.handler.impl;

import com.skr.signal.base.rpc.client.RpcNettyClient;
import com.skr.signal.base.rpc.client.proxy.RpcClient;
import com.skr.signal.common.service.ComputeService;
import com.skr.signal.common.service.HelloWorldService;
import com.skr.signal.front.anno.HandlerTag;
import com.skr.signal.front.dto.RequestInfo;
import com.skr.signal.front.dto.ResponseInfo;
import com.skr.signal.front.handler.Handler;


/**
 * @author mqw
 * @create 2020-06-05-10:12
 */
@HandlerTag(path = "/front")
public class TransHandler extends Handler<RequestInfo, ResponseInfo> {

    @Override
    public ResponseInfo handle(RequestInfo requestInfo) {
        ComputeService computeService = RpcNettyClient.createProxy(ComputeService.class);
        String front = computeService.compute("你好我是front");
        ResponseInfo info = new ResponseInfo();
        info.setAnswer(front);
        return info;
    }

}
