package com.skr.signal.base.registry.impl.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public final class CuratorUtils {

    private static final String ZK_REGISTRY_PATH = "/skr-rpc";
    private static final int BASE_SLEEP_TIME = 3000;
    private static final int MAX_RETRIES = 5;
    private static volatile Map<String, List<String>> serviceAddressMap = new ConcurrentHashMap<>();
    private static Set<String> registeredPathSet = ConcurrentHashMap.newKeySet();
    private static CuratorFramework zkClient;

    public static void createPersistentNode(String path) {
        try {
            path = ZK_REGISTRY_PATH +"/"+ path;
            if (registeredPathSet.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("path已经存在{}",path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
            }
            registeredPathSet.add(path);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }


    public static List<String> getChildrenNodes(String serviceName) {
        if (serviceAddressMap.containsKey(serviceName)) {
            return serviceAddressMap.get(serviceName);
        }
        List<String> result;
        String servicePath = ZK_REGISTRY_PATH + "/" + serviceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            serviceAddressMap.put(serviceName, result);
            registerWatcher(zkClient, serviceName);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
        return result;
    }

    /**
     * 清空注册中心的数据
     */
    public static void clearRegistry() {
        registeredPathSet.stream().parallel().forEach(p -> {
            try {
                zkClient.delete().forPath(p);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        });
    }

    public static CuratorFramework initZkClient(String address) {
        if(zkClient == null){
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
            CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                    .connectString(address)
                    .retryPolicy(retryPolicy)
                    .build();
            curatorFramework.start();
            zkClient = curatorFramework;
        }
        return zkClient;
    }


    private static void registerWatcher(CuratorFramework zkClient, String serviceName) {
        String servicePath = ZK_REGISTRY_PATH + "/" + serviceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            serviceAddressMap.put(serviceName, serviceAddresses);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }

    }


}
