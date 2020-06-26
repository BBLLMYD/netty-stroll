package com.skr.signal.base.registry.impl.zookeeper;

import com.skr.signal.base.registry.ServiceRegistry;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @auther mqw
 * @date 2020-06-25
 **/
@Data
@AllArgsConstructor
public class ZKServiceRegistry implements ServiceRegistry {

    private String registryAddress;

    @Override
    public void register(String service,String serviceAddress) {
        CuratorUtils.initZkClient(registryAddress);
        String servicePath = service + "/" + serviceAddress;
        CuratorUtils.createPersistentNode(servicePath);
    }

}