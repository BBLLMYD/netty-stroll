package com.skr.signal.base.registry.impl.zookeeper;

import com.skr.signal.base.registry.LoadPolicy;
import com.skr.signal.base.registry.ServiceDiscover;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @auther mqw
 * @date 2020-06-25
 **/
public class ZKServiceDiscovery implements ServiceDiscover {

    @Getter
    private String registryAddress;
    @Getter
    private LoadPolicy loadPolicy;

    @Override
    public String discover(String serviceName) {
        CuratorUtils.initZkClient(registryAddress);
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(serviceName);
        String serviceAddress = loadPolicy.filterServices(serviceUrlList);
        return serviceAddress;
    }

    @Override
    public ServiceDiscover loadPolicy(LoadPolicy loadPolicy) {
        this.loadPolicy = loadPolicy;
        return this;
    }

    @Override
    public ServiceDiscover registryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
        return this;
    }


}