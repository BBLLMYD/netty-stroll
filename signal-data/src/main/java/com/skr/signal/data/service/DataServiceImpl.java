package com.skr.signal.data.service;

import com.skr.signal.base.rpc.server.anno.RpcServiceTag;
import com.skr.signal.common.service.DataService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : mengqingwen 
 * create at:  2020-07-06
 * */
@Slf4j
@RpcServiceTag(targetService = DataService.class)
public class DataServiceImpl implements DataService {

    @Override
    public String data(String args) {
        return "[data]" + args + "[data]";
    }

}