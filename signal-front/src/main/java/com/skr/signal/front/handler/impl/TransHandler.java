package com.skr.signal.front.handler.impl;

import com.skr.signal.base.rpc.client.RpcNettyClient;
import com.skr.signal.common.service.ComputeService;
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
        String requestKey = requestInfo.getRequestKey();
        String computeRest = computeService.compute(requestKey);
        ResponseInfo info = new ResponseInfo();
        info.setAnswer(computeRest);
        return info;
    }

}
