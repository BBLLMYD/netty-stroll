package com.skr.signal.base.registry;

/**
 * @author mqw
 * @create 2020-06-24-13:53
 */
public interface ServiceDiscover {

    String discover(String serviceName);

    ServiceDiscover loadPolicy(LoadPolicy loadPolicy);

    ServiceDiscover registryAddress(String registryAddress);


}