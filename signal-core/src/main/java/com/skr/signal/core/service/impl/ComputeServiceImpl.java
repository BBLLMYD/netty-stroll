package com.skr.signal.core.service.impl;

import com.skr.signal.base.rpc.client.proxy.RpcClient;
import com.skr.signal.base.rpc.server.anno.RpcServiceTag;
import com.skr.signal.common.service.ComputeService;
import com.skr.signal.common.service.RouteService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mqw
 * @create 2020-06-09-11:30
 */
@Slf4j
@RpcServiceTag(targetService = ComputeService.class)
public class ComputeServiceImpl implements ComputeService {

    @Override
    public String compute(String args) {
        String route = RpcClient.getInstance().create(RouteService.class).route(args);
        log.info("ComputeService层获取数据获取Route层数据："+route);
        return "[core]" + route + "[core]";
    }

}
