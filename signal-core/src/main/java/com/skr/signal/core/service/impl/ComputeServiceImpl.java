package com.skr.signal.core.service.impl;

import com.skr.signal.base.rpc.server.anno.RpcServiceTag;
import com.skr.signal.common.service.ComputeService;
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
        String info = "rest from core : " + args + System.currentTimeMillis();
        log.info(info);
        return info;
    }

}
