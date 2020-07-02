package com.skr.signal.base.registry;

/**
 * @author mqw
 * @create 2020-06-24-13:53
 */
public interface ServiceRegistry {

    void register(String service, String address);

    ServiceRegistry registryAddress(String registryAddress);

}