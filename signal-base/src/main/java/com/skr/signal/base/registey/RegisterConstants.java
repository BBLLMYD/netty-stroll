package com.skr.signal.base.registey;


/**
 * @author mqw
 * @create 2020-06-22-13:37
 */
public interface RegisterConstants {

    interface  ZK_Contant{
        Integer ZK_SESSION_TIMEOUT = 3000;
        String ZK_REGISTRY_PATH = "/register";
        String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
    }
}
