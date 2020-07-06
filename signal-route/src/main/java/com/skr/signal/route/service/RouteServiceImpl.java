package com.skr.signal.route.service;

import com.skr.signal.base.rpc.server.anno.RpcServiceTag;
import com.skr.signal.common.service.ComputeService;
import com.skr.signal.common.service.RouteService;
import lombok.extern.slf4j.Slf4j;

/**
 * @auther mqw
 * @date 2020-07-06
 **/
@Slf4j
@RpcServiceTag(targetService = ComputeService.class)
public class RouteServiceImpl implements RouteService {

    @Override
    public String route(String args) {
        String info = "rest from route : " + args + System.currentTimeMillis();
        log.info(info);
        return info;
    }

}
