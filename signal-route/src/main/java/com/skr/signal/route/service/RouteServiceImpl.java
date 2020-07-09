package com.skr.signal.route.service;

import com.skr.signal.base.rpc.client.proxy.RpcClient;
import com.skr.signal.base.rpc.server.anno.RpcServiceTag;
import com.skr.signal.common.service.ComputeService;
import com.skr.signal.common.service.DataService;
import com.skr.signal.common.service.RouteService;
import lombok.extern.slf4j.Slf4j;

/**
 * @auther mqw
 * @date 2020-07-06
 **/
@Slf4j
@RpcServiceTag(targetService = RouteService.class)
public class RouteServiceImpl implements RouteService {

    @Override
    public String route(String args) {
        String data = RpcClient.getInstance().create(DataService.class).data(args);
        log.info("RouteService层获取数据获取data层数据："+data);
        return "[route]" + data + "[route]";
    }

}
